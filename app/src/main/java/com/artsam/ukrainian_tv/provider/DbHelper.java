package com.artsam.ukrainian_tv.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.artsam.ukrainian_tv.MainActivity;

public class DbHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "UkrTv.db";
    public static final int DB_VERSION = 1;

    // constructor matching super
    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(MainActivity.MAIN_TAG, "--- DbHelper: onCreate ---");

        db.execSQL(DbContract.Channels.CREATE_TABLE_CHANNELS);
        db.execSQL("CREATE UNIQUE INDEX channels_uk_id ON "
                + DbContract.Channels.TABLE_NAME + " ("
                + DbContract.Channels.COLUMN_CHAN_ID + ")");

        db.execSQL(DbContract.Categories.CREATE_TABLE_CATEGORIES);
        db.execSQL("CREATE UNIQUE INDEX categories_uk_name ON "
                + DbContract.Categories.TABLE_NAME + " ("
                + DbContract.Categories.COLUMN_NAME + ")");

        db.execSQL(DbContract.Elected.CREATE_TABLE_ELECTED);
        db.execSQL("CREATE UNIQUE INDEX elected_uk_id ON "
                + DbContract.Elected.TABLE_NAME + " ("
                + DbContract.Elected.COLUMN_CHAN_ID + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(MainActivity.MAIN_TAG, "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL(DbContract.Channels.DELETE_TABLE_CHANNELS);
        db.execSQL(DbContract.Categories.DELETE_TABLE_CATEGORIES);
        onCreate(db);
    }
}