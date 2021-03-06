package com.cs117.oursweeper;

public class Tile {
    // '*' = Mine
    // '0' = Nothing
    // '1' to '8' = nearby mines
    // 'A' to 'D' = marked by player

    private char value;
    private char numericValue;
    private boolean isRevealed = false;
    private boolean isFlagged = false;

    public Tile() {
        this.value = '0';
        this.numericValue = '0';
    }

    public char getValue() {return numericValue;}
    public void setValue(char value) {
        this.value = value;
        this.numericValue = value;
    }
    public void addOne() { // Do Nothing when it's a mine.
        if (value != '*') {
            value += 1;
            numericValue += 1;
        }
    }
    // Return false if tile is already revealed
    public boolean reveal() {
        if (isRevealed || isFlagged) return false;
        isRevealed = true;
        return true;
    }
    public char display() {
        if (isRevealed && !isFlagged) return numericValue;
        if (isFlagged) return value;
        else return '?';
    }

    public int flag(char c) {
        if (isRevealed) return 0;
        if (isFlagged && value == c) {
            isFlagged = false;
            return 1;
        }
        else if (!isFlagged) {
            isFlagged = true;
            value = c;
            return -1;
        }
        return 0;
    }
}
