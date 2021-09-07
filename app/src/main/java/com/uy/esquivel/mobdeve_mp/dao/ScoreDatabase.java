package com.uy.esquivel.mobdeve_mp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScoreDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "score.db";
    private static final int DATABASE_VERSION = 1;

    //column names

    public static final String TABLE_SCORES = "scores";
    public static final String SCORE_NAME ="name";
    public static final String SCORE_SCORE ="score";



    public static final String CREATE_SCORE_TABLE =
            "create table " + TABLE_SCORES +
                    " ( "
                    + SCORE_NAME + " text, "
                    + SCORE_SCORE + " text ); ";

    public ScoreDatabase (Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SCORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);
        onCreate(db);
    }
}

