package com.uy.esquivel.mobdeve_mp.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.uy.esquivel.mobdeve_mp.model.Score;

import java.util.ArrayList;

public class ScoreDAOSQLImpl implements ScoreDAO{

    private SQLiteDatabase database;
    private ScoreDatabase scoreDatabase;

    public ScoreDAOSQLImpl(Context context) {
        scoreDatabase = new ScoreDatabase(context);
    }

    @Override
    public long addScore(Score score) {
        ContentValues values = new ContentValues();

        values.put(ScoreDatabase.SCORE_NAME, score.getName());
        values.put(ScoreDatabase.SCORE_SCORE, score.getScore());

        database = scoreDatabase.getWritableDatabase();

        long id = database.insert (ScoreDatabase.TABLE_SCORES,
                null,
                values);

        if (database != null){
            scoreDatabase.close();
        }

        return id;
    }

    @Override
    public ArrayList<Score> getTop10Scores() {
        ArrayList<Score> result = new ArrayList<>();
        String [] columns = {
                ScoreDatabase.SCORE_NAME,
                ScoreDatabase.SCORE_SCORE};

        database = scoreDatabase.getReadableDatabase();

        Cursor cursor = database.query(ScoreDatabase.TABLE_SCORES,
                columns,
                null,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            Score temp = new Score();
            temp.setName(cursor.getString(0));
            temp.setScore(cursor.getInt(1));
            result.add (temp);
            cursor.moveToNext();
        }

        if (cursor!=null){
            cursor.close();
        }

        if (database != null){
            database.close();
        }

        return result;
    }



}
