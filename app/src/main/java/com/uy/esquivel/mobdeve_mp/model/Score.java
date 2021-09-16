package com.uy.esquivel.mobdeve_mp.model;

import java.io.Serializable;
import java.util.Comparator;

public class Score implements Serializable, Comparable<Score> {

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

    @Override
    public int compareTo(Score s1) {
        if(this.score < s1.getScore())
            return 1;
        else if(this.score > s1.getScore())
            return -1;
        else{
            return this.name.trim().toLowerCase().compareTo(s1.getName().trim().toLowerCase());
        }
    }
}


