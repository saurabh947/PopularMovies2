package com.saurabh.popularmovies2.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A SQLite Db for maintaining user's favorite movies.
 */
public class MovieSqlDb extends SQLiteOpenHelper {
    public static final String TAG = MovieSqlDb.class.getSimpleName();

    public MovieSqlDb(Context context) {
        super(context, ProviderSqlHelper.DB_NAME, null, ProviderSqlHelper.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                ProviderSqlHelper.TABLE_MOVIE + "(" +
                ProviderSqlHelper.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                ProviderSqlHelper.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                ProviderSqlHelper.COLUMN_IS_FAVORITE + " TEXT DEFAULT 'false');";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + ProviderSqlHelper.TABLE_MOVIE);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + ProviderSqlHelper.TABLE_MOVIE + "'");

        onCreate(db);
    }
}
