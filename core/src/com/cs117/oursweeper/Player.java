package com.cs117.oursweeper;

public class Player implements Comparable<Player>{
    private char color;
    private String name;
    private int score;
    private boolean isReady;

    public Player(String name, char color) {
        this.color = color;
        this.name = name;
        this.score = 0;
        this.isReady = false;
    }

    public char getColor() {
        return color;
    }
    public String getName() {
        return name;
    }
    public int getScore() {
        return score;
    }
    public void addPoints() {
        score++;
    }
    public void subPoints() {
        score = score - 5;
    }
    public boolean getReadyState() {
        return isReady;
    }
    public void setReady() {
        isReady = true;
    }
    public void clearScore() {
        score = 0;
    }

    @Override
    public int compareTo(Player p) {
        return getScore() - p.getScore();
    }
}
