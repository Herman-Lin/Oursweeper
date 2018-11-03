package com.cs117.oursweeper;

public class Tile {
    // '*' = Mine
    // '0' = Nothing
    // '1' to '8' = nearby mines
    // 'A' to 'D' = marked by player

    private char value;
    private boolean isRevealed = true;

    public Tile() {
        this.value = '0';
    }

    public char getValue() {return value;}
    public void setValue(char value) {this.value = value;}
    public void addOne() { // Do Nothing when it's a mine.
        if (value != '*') {
            value += 1;
        }
    }
    // Return false if tile is already revealed
    public boolean reveal() {
        if (isRevealed) return false;
        isRevealed = true;
        return true;
    }
    public char display() {
        if (isRevealed) return value;
        else return '?';
    }
}
