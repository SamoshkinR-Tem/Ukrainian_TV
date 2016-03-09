package com.artsam.ukrainian_tv.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.artsam.ukrainian_tv.MainActivity;
import com.artsam.ukrainian_tv.provider.DbContract;
import com.artsam.ukrainian_tv.provider.DbHelper;

import java.sql.SQLDataException;
import java.sql.SQLException;

public class MyContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.artsam.ukrainiantv.provider";
    public static final String PATH_CHANNELS = "channels";
    public static final String PATH_CATEGORIES = "categories";
    public static final String PATH_ELECTED = "elected";

    public static final Uri CONTENT_URI_CHANN = Uri.parse("content://" + AUTHORITY
            + "/" + PATH_CHANNELS);
    public static final Uri CONTENT_URI_CATEGORIES = Uri.parse("content://" + AUTHORITY
            + "/" + PATH_CATEGORIES);
    public static final Uri CONTENT_URI_ELECTED = Uri.parse("content://" + AUTHORITY
            + "/" + PATH_ELECTED);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/channels";

    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/channel";
    // used for the UriMatcher
    private static final int CHANNELS = 10;
    private static final int CHANNEL_ID = 20;
    private static final int CATEGORIES = 30;
    private static final int CATEGORIES_ID = 40;
    private static final int ELECTED = 50;
    private static final int ELECTED_ID = 60;

    public static final int OR_REPLACE = 5;

    private static final UriMatcher sURIMatcher;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(AUTHORITY, PATH_CHANNELS, CHANNELS);
        sURIMatcher.addURI(AUTHORITY, PATH_CHANNELS + "/#", CHANNEL_ID);
        sURIMatcher.addURI(AUTHORITY, PATH_CATEGORIES, CATEGORIES);
        sURIMatcher.addURI(AUTHORITY, PATH_CATEGORIES + "/#", CATEGORIES_ID);
        sURIMatcher.addURI(AUTHORITY, PATH_ELECTED, ELECTED);
        sURIMatcher.addURI(AUTHORITY, PATH_ELECTED + "/#", ELECTED_ID);
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        String path;
        long id = 0;
        switch (sURIMatcher.match(uri)) {
            case CHANNELS:
//                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: insert: CHANNELS");
                id = sqlDB.insertWithOnConflict(DbContract.Channels.TABLE_NAME, null, values, OR_REPLACE);
                path = PATH_CHANNELS;
                break;
            case CHANNEL_ID:
//                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: insert: CHANNEL_ID");
                path = PATH_CHANNELS;
                break;
            case CATEGORIES:
//                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: insert: CATEGORIES");
                id = sqlDB.insertWithOnConflict(DbContract.Categories.TABLE_NAME, null, values, OR_REPLACE);
                path = PATH_CATEGORIES;
                break;
            case CATEGORIES_ID:
//                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: insert: CATEGORIES_ID");
                path = PATH_CATEGORIES;
                break;
            case ELECTED:
//                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: insert: ELECTED");
                id = sqlDB.insertWithOnConflict(DbContract.Elected.TABLE_NAME, null, values, OR_REPLACE);
                path = PATH_ELECTED;
                break;
            case ELECTED_ID:
//                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: insert: ELECTED_ID");
                path = PATH_ELECTED;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(path + "/" + id);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        switch (sURIMatcher.match(uri)) {
            case CHANNELS:
                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: query: CHANNELS");
                cursor = db.query(DbContract.Channels.TABLE_NAME, projection, selection, null, null, null, null);
                break;
            case CHANNEL_ID:
                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: query: CHANNEL_ID");
                cursor = db.query(DbContract.Channels.TABLE_NAME, projection, selection, null, null, null, null);
                break;
            case CATEGORIES:
                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: query: CATEGORIES");
                cursor = db.query(DbContract.Categories.TABLE_NAME, projection, selection, null, null, null, null);
                break;
            case CATEGORIES_ID:
                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: query: CATEGORIES_ID");
                cursor = db.query(DbContract.Categories.TABLE_NAME, projection, selection, null, null, null, null);
                break;
            case ELECTED:
                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: query: ELECTED");
                cursor = db.query(DbContract.Elected.TABLE_NAME, projection, selection, null, null, null, null);
                break;
            case ELECTED_ID:
                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: query: ELECTED_ID");
                cursor = db.query(DbContract.Elected.TABLE_NAME, projection, selection, null, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        String id;
        int rowsDeleted = -1;
        switch (sURIMatcher.match(uri)) {
            case CHANNELS:
                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: deleted: CHANNELS");
                rowsDeleted = sqlDB.delete(DbContract.Channels.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case CHANNEL_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DbContract.Channels.TABLE_NAME,
                            DbContract.Channels.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(DbContract.Channels.TABLE_NAME,
                            DbContract.Channels.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case CATEGORIES:
                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: deleted: CATEGORIES");
                rowsDeleted = sqlDB.delete(DbContract.Categories.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case CATEGORIES_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(DbContract.Categories.TABLE_NAME,
                            DbContract.Categories.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(DbContract.Categories.TABLE_NAME,
                            DbContract.Categories.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case ELECTED:
                Log.d(MainActivity.MAIN_TAG, "MyContentProvider: deleted: ELECTED");
                rowsDeleted = sqlDB.delete(DbContract.Elected.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case ELECTED_ID:
                id = uri.getLastPathSegment();
                if (!id.equals(String.valueOf(-1))) {
                    if (TextUtils.isEmpty(selection)) {
                        rowsDeleted = sqlDB.delete(DbContract.Elected.TABLE_NAME,
                                DbContract.Elected.COLUMN_ID + "=" + id,
                                null);
                    } else {
                        rowsDeleted = sqlDB.delete(DbContract.Categories.TABLE_NAME,
                                DbContract.Elected.COLUMN_ID + "=" + id
                                        + " and " + selection,
                                selectionArgs);
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
