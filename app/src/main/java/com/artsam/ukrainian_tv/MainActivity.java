package com.artsam.ukrainian_tv;

import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.artsam.ukrainian_tv.provider.DbContract;
import com.artsam.ukrainian_tv.provider.MyContentProvider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String MAIN_TAG = "my_app";

    // URL to get JSON
    public static final String URL_CHANNEL = "https://t2dev.firebaseio.com/CHANNEL.json";
    public static final String URL_CATEGORY = "https://t2dev.firebaseio.com/CATEGORY.json";
    public static final int JSON_HASH_CHANNEL = 868312451;
    public static final int JSON_HASH_CATEGORY = -1930017696;

    // Channel JSON Node names
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_DESCRIPTION = "description";
    private static final String TAG_TV_URL = "tvURL";
    private static final String TAG_SITE_URL = "siteURL";
    private static final String TAG_LOGO_URL = "logoURL";
    private static final String TAG_STREAM_URL = "streamURL";
    private static final String TAG_YOUTUBE_URL = "youtubeURL";
    private static final int CATEGORIES_LOADER = 0;
    private static final int CHANNELS_LOADER = 1;
    private static final int ELECTED_LOADER = 2;
    public static final int NUM_OF_CATEGORIES = 7;

    private MainActivity mMainActivity = this;
    private ProgressDialog mProgressDialog;
    private HashMap<String, String> mCategories;
    private String mCategory = "Общеукраинские";
    private MySimpleCursorAdapter mAdapter;
    private Menu mNavigationMenu;
    private Cursor mElected;
    private TextView tvCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCategories = new HashMap<>();

        tvCategory = (TextView) findViewById(R.id.tv_category);
        tvCategory.setText(mCategory);

        getSupportLoaderManager().initLoader(ELECTED_LOADER, null, this);

        mAdapter = new MySimpleCursorAdapter(getApplicationContext(),
                R.layout.card_lv_item, null,
                new String[]{DbContract.Channels.COLUMN_CHAN_NAME},
                new int[]{R.id.tv_channel_name}, 0);

        ListView mLvChannels = (ListView) findViewById(R.id.lv_channels);
        mLvChannels.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(CHANNELS_LOADER, null, this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mNavigationMenu = navigationView.getMenu();

        mNavigationMenu.add(R.id.nav_elected, "Избранные".hashCode(),
                7, R.string.menu_item_elected);

        getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_sync:
                new DownloadJsonTask().execute(URL_CATEGORY, URL_CHANNEL);
                break;
            case R.id.action_clear:
//                new DbHelper(this).onUpgrade(new DbHelper(this).getWritableDatabase(), 1, 1);
                getContentResolver().delete(MyContentProvider.CONTENT_URI_CHANN, null, null);
                getContentResolver().delete(MyContentProvider.CONTENT_URI_CATEGORIES, null, null);
                mNavigationMenu.clear();

                mNavigationMenu.add(R.id.nav_elected, "Избранные".hashCode(),
                        7, R.string.menu_item_elected);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if (!String.valueOf(item.getTitle()).equals(getString(R.string.menu_item_elected))) {
            mCategory = String.valueOf(item.getTitle());
            getSupportLoaderManager().restartLoader(CHANNELS_LOADER, null, this);
            tvCategory.setText(mCategory);
        } else {
            mAdapter.swapCursor(mElected);
            tvCategory.setText(R.string.menu_item_elected);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case CATEGORIES_LOADER:
                Log.d(MAIN_TAG, "CursorLoader: onCreate CATEGORIES_LOADER ");
                cursorLoader = new CursorLoader(getApplicationContext(),
                        MyContentProvider.CONTENT_URI_CATEGORIES,
                        new String[]{DbContract.Categories.COLUMN_NAME},
                        null, null, null);
                break;
            case CHANNELS_LOADER:
                Log.d(MAIN_TAG, "CursorLoader: onCreate CHANNELS_LOADER ");
                String projection[] = {DbContract.Channels.COLUMN_ID,
                        DbContract.Channels.COLUMN_CHAN_ID,
                        DbContract.Channels.COLUMN_CHAN_NAME,
                        DbContract.Channels.COLUMN_TV_URI};

                String select = "(" + DbContract.Channels.COLUMN_CATEGORY + " == '" + mCategory + "')";

                cursorLoader = new CursorLoader(getApplicationContext(),
                        MyContentProvider.CONTENT_URI_CHANN,
                        projection, select, null, null);
                break;
            case ELECTED_LOADER:
                Log.d(MAIN_TAG, "CursorLoader: onCreate ELECTED_LOADER");
                cursorLoader = new CursorLoader(getApplicationContext(),
                        MyContentProvider.CONTENT_URI_ELECTED,
                        new String[]{DbContract.Elected.COLUMN_ID,
                                DbContract.Elected.COLUMN_CHAN_ID,
                                DbContract.Elected.COLUMN_CHAN_NAME,
                                DbContract.Elected.COLUMN_TV_URI},
                        null, null, null);
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        Log.d(MAIN_TAG, "CursorLoader: onLoadFinished");
        if (loader != null) {
            switch (loader.getId()) {
                case CATEGORIES_LOADER:
                    initNavMenu(data);
                    break;
                case CHANNELS_LOADER:
                    mAdapter.swapCursor(data);
                    break;
                case ELECTED_LOADER:
                    mElected = data;
                    break;
            }
        }
    }

    private void initNavMenu(Cursor data) {
        Log.d(MAIN_TAG, "MainActivity: onLoadFinished: initNavMenu");
        if (data != null && mNavigationMenu.size() <= 1) {
            Log.d(MAIN_TAG, "MainActivity: onLoadFinished: initNavMenu_if");

            // this is order of categories in navigation menu
            // Each number corresponds to the category position in menu
            // I know it`s hardcoding, but it`s also a simple solution
            int[] order = {6, 4, 0, 2, 3, 1, 5, 7, 8, 9, 10, 11};


            if (data.moveToFirst() && data.getCount() >= NUM_OF_CATEGORIES) {
                do {
                    int itemId = data.getString(data.
                            getColumnIndex(DbContract.Categories.COLUMN_NAME)).hashCode();
                    String itemTitle = data.getString(
                            data.getColumnIndex(DbContract.Categories.COLUMN_NAME));
                    mNavigationMenu.add(R.id.nav_categories,
                            itemId,
                            order[data.getPosition()],
                            itemTitle);
                } while (data.moveToNext());

                getSupportLoaderManager().destroyLoader(CATEGORIES_LOADER);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(MAIN_TAG, "CursorLoader: onLoaderReset");
        switch (loader.getId()) {
            case CATEGORIES_LOADER:
                break;
            case CHANNELS_LOADER:
                mAdapter.swapCursor(null);
                break;
        }
    }

    private class DownloadJsonTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            doInternal(url);

            url = params[1];
            doInternal(url);

            return null;
        }

        private String doInternal(String url) {
            String isDone = null;
            try {
                String jsonStr;
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                jsonStr = EntityUtils.toString(entity);
                parseJsonToDb(jsonStr);

                isDone = "done";
            } catch (UnsupportedEncodingException e) {
                isDone = null;
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                isDone = null;
                e.printStackTrace();
            } catch (IOException e) {
                isDone = null;
                e.printStackTrace();
            }
            return isDone;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            getSupportLoaderManager().initLoader(CATEGORIES_LOADER, null, mMainActivity);
        }

        private void parseJsonToDb(String jsonStr) {
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    Iterator itr = jsonObj.keys();

                    switch (jsonStr.hashCode()) {
                        case JSON_HASH_CATEGORY:
                            Log.d(MAIN_TAG, "MainActivity: parseJsonToDb: CATEGORY");

                            while (itr.hasNext()) {
                                String categoryStr = itr.next().toString();
                                JSONObject category = jsonObj.getJSONObject(categoryStr);
                                Iterator itrChannels = category.keys();

                                ContentValues values = new ContentValues();
                                values.put(DbContract.Categories.COLUMN_NAME, categoryStr);
                                getContentResolver().insert(MyContentProvider.CONTENT_URI_CATEGORIES, values);

                                while (itrChannels.hasNext()) {
                                    String key = itrChannels.next().toString();
                                    mCategories.put(key, categoryStr);
                                }
                            }
                            break;

                        case JSON_HASH_CHANNEL:
                            Log.d(MAIN_TAG, "MainActivity: parseJsonToDb: CHANNEL");

                            while (itr.hasNext()) {
                                String key = itr.next().toString();
                                JSONObject ch = jsonObj.getJSONObject(key);

                                ContentValues values = new ContentValues();
                                values.put(DbContract.Channels.COLUMN_CHAN_ID, ch.getString(TAG_ID));
                                values.put(DbContract.Channels.COLUMN_CHAN_NAME, ch.getString(TAG_NAME));
                                values.put(DbContract.Channels.COLUMN_CHAN_DESC, ch.getString(TAG_DESCRIPTION));
                                values.put(DbContract.Channels.COLUMN_TV_URI, ch.getString(TAG_TV_URL));
                                values.put(DbContract.Channels.COLUMN_SITE_URI, ch.getString(TAG_SITE_URL));
                                values.put(DbContract.Channels.COLUMN_LOGO_URI, ch.getString(TAG_LOGO_URL));
                                values.put(DbContract.Channels.COLUMN_STREAM_URI, ch.getString(TAG_STREAM_URL));
                                values.put(DbContract.Channels.COLUMN_YOUTUBE_URI, ch.getString(TAG_YOUTUBE_URL));
                                values.put(DbContract.Channels.COLUMN_CATEGORY,
                                        mCategories.get(key));

                                getContentResolver().insert(MyContentProvider.CONTENT_URI_CHANN, values);

                            }
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Couldn't get any data from the url",
                        Toast.LENGTH_LONG).show();
                Log.e(MAIN_TAG, "DownloadJsonTask: Couldn't get any data from the url");
            }
        }
    }

    public class MySimpleCursorAdapter extends SimpleCursorAdapter
            implements View.OnClickListener {

        private ImageSwitcher sw;
        private ViewBinder mViewBinder;

        public MySimpleCursorAdapter(Context context, int layout, Cursor c,
                                     String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final ViewBinder binder = mViewBinder;
            final int count = mTo.length;
            final int[] from = mFrom;
            final int[] to = mTo;

            String channelId = cursor.getString(
                    cursor.getColumnIndex(DbContract.Channels.COLUMN_CHAN_ID));

            ContentValues valuesForElected = new ContentValues();
            valuesForElected.put(DbContract.Elected.COLUMN_CHAN_ID, channelId);
            valuesForElected.put(DbContract.Elected.COLUMN_CHAN_NAME, cursor.getString(
                    cursor.getColumnIndex(DbContract.Channels.COLUMN_CHAN_NAME)));
            valuesForElected.put(DbContract.Elected.COLUMN_TV_URI, cursor.getString(
                    cursor.getColumnIndex(DbContract.Channels.COLUMN_TV_URI)));


            for (int i = 0; i < count; i++) {

                final View v = view.findViewById(to[i]);
                if (v != null) {
                    boolean bound = false;
                    if (binder != null) {
                        bound = binder.setViewValue(v, cursor, from[i]);
                    }

                    if (!bound) {
                        String text = cursor.getString(from[i]);
                        if (text == null) {
                            text = "";
                        }

                        if (v instanceof TextView) {
                            setViewText((TextView) v, text);
                        } else if (v instanceof ImageView) {
                            setViewImage((ImageView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                    " view that can be bounds by this SimpleCursorAdapter");
                        }
                    }
                }
            }

            sw = (ImageSwitcher) view.findViewById(R.id.imageSwitcher);
            sw.removeAllViews();
            sw.setFactory(new ViewFactory() {
                @Override
                public View makeView() {
                    ImageView myView = new ImageView(getApplicationContext());
                    myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    myView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                    return myView;
                }
            });
            sw.setOnClickListener(this);

            sw.setImageResource(R.drawable.elect_f);
            sw.setTag(false);

            if (mElected != null && mElected.moveToFirst()) {
                do {
                    if (mElected.getString(
                            mElected.getColumnIndex(DbContract.Elected.COLUMN_CHAN_ID))
                            .equals(channelId)) {
                        sw.setImageResource(R.drawable.elect_t);
                        sw.setTag(true);
                    }
                } while (mElected.moveToNext());
            }

            sw.setTag(R.string.sw_tag_key, valuesForElected);
        }

        @Override
        public void onClick(View view) {
            ImageSwitcher switcher = (ImageSwitcher) view;

            if (!(boolean) view.getTag()) {
                Log.d(MAIN_TAG, "become true");

                getContentResolver().insert(MyContentProvider.CONTENT_URI_ELECTED,
                        (ContentValues) view.getTag(R.string.sw_tag_key));

                switcher.setImageResource(R.drawable.elect_t);
                switcher.setTag(true);

            } else {
                Log.d(MAIN_TAG, "become false");

                int _id = -1;

                if (mElected.moveToFirst()) {
                    do {
                        String channelId = (String) ((ContentValues) view.getTag(R.string.sw_tag_key))
                                .get(DbContract.Elected.COLUMN_CHAN_ID);
                        if (mElected.getString(mElected.getColumnIndex(DbContract.Elected.COLUMN_CHAN_ID))
                                .equals(channelId)) {
                            _id = mElected.getInt(mElected.getColumnIndex(DbContract.Elected.COLUMN_ID));
                        }
                    } while (mElected.moveToNext());
                }

                Uri uri = Uri.parse(MyContentProvider.CONTENT_URI_ELECTED + "/" + _id);
                getContentResolver().delete(uri, null, null);

                switcher.setImageResource(R.drawable.elect_f);
                switcher.setTag(false);
            }
        }
    }
}
