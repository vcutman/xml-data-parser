package com.vangel.xmldp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public class IrrAdsDbCreator extends SQLiteOpenHelper {
    private static String TAG = "IrrAdsDbCreator";

    private static final String DATABASE_NAME = "irr_auto_catalog.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_AUTO_CATALOG_TABLE =
        "create table auto_catalog(id INTEGER PRIMARY KEY AUTOINCREMENT, creation_date TEXT, host TEXT)";

    private static final String SQL_CREATE_OFFERS_TABLE =
            "create table offers(id INTEGER PRIMARY KEY AUTOINCREMENT, auto_catalog_id INTEGER, " +
                    " date TEXT," +
                    " mark TEXT," +
                    " type TEXT)";

    public IrrAdsDbCreator(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_AUTO_CATALOG_TABLE);
        db.execSQL(SQL_CREATE_OFFERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS offers");
        db.execSQL("DROP TABLE IF EXISTS auto_catalog");

        onCreate(db);
    }
}
