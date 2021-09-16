package com.uy.esquivel.mobdeve_mp.dao;

import com.uy.esquivel.mobdeve_mp.model.Score;

import java.util.ArrayList;

public interface ScoreDAO {
    long addScore(Score score);
    ArrayList<Score> getAllScores();
}
