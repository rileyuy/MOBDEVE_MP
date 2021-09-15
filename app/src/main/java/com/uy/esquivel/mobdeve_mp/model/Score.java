package com.uy.esquivel.mobdeve_mp.model;

import java.io.Serializable;
import java.util.Comparator;

public class Score implements Serializable {

    private String name = "";
    private int score = 0;

    public Score() {
    }

    public Score(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return this.score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }


    @Override
    public String toString (){
        return this.name + "'s score is: " + this.score + "!";
    }
}

class ScoreComparator implements Comparator<Score> {
    public int compare(Score score1, Score score2) {
        return score1.getScore() - score2.getScore();
    }
}

