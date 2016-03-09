package com.artsam.ukrainian_tv.provider;

import android.provider.BaseColumns;

public final class DbContract {
    // To prevent someone from accidentally instantiating
    // the contract class give it an empty constructor.
    public DbContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class Channels implements BaseColumns {
        // table
        public static final String TABLE_NAME = "channels";

        // fields
        public static final String COLUMN_ID = _ID;
        public static final String COLUMN_CHAN_ID = "id";
        public static final String COLUMN_CHAN_NAME = "name";
        public static final String COLUMN_CHAN_DESC = "description";
        public static final String COLUMN_TV_URI = "tvURL";
        public static final String COLUMN_SITE_URI = "siteURL";
        public static final String COLUMN_LOGO_URI = "logoURL";
        public static final String COLUMN_STREAM_URI = "streamURL";
        public static final String COLUMN_YOUTUBE_URI = "youtubeURL";
        public static final String COLUMN_CATEGORY = "category";

        // SQLite script to create table
        public static final String CREATE_TABLE_CHANNELS = "create table " + TABLE_NAME + " ("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_CHAN_ID + " text, "
                + COLUMN_CHAN_NAME + " text, "
                + COLUMN_CHAN_DESC + " text, "
                + COLUMN_TV_URI + " text, "
                + COLUMN_SITE_URI + " text, "
                + COLUMN_LOGO_URI + " text, "
                + COLUMN_STREAM_URI + " text, "
                + COLUMN_YOUTUBE_URI + " text, "
                + COLUMN_CATEGORY + " text" + ");";

        // SQLite script to delete table
        public static final String DELETE_TABLE_CHANNELS =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class Categories implements BaseColumns {
        // table
        public static final String TABLE_NAME = "categories";

        // fields
        public static final String COLUMN_ID = _ID;
        public static final String COLUMN_NAME = "name";

        public static final String CREATE_TABLE_CATEGORIES = "create table " + TABLE_NAME + " ("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_NAME + " text" + ");";

        // SQLite script to delete table
        public static final String DELETE_TABLE_CATEGORIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static abstract class Elected implements BaseColumns {
        // table
        public static final String TABLE_NAME = "elected";

        // fields
        public static final String COLUMN_ID = _ID;
        public static final String COLUMN_CHAN_ID = "id";
        public static final String COLUMN_CHAN_NAME = "name";
        public static final String COLUMN_TV_URI = "tvURL";

        // SQLite script to create table
        public static final String CREATE_TABLE_ELECTED = "create table " + TABLE_NAME + " ("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_CHAN_NAME + " text, "
                + COLUMN_TV_URI + " text, "
                + COLUMN_CHAN_ID + " text" + ");";

        // SQLite script to delete table
        public static final String DELETE_TABLE_ELECTED =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
