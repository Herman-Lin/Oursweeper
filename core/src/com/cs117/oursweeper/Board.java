package com.cs117.oursweeper;

import com.cs117.oursweeper.Tile;

import java.util.Random;

public class Board {
    private Tile[][] mineGrid;
    private int mineCount;
    private int tilesToReveal;

    public Board() {
        int actMines = 0;
        // tilesToReveal = 621;
        tilesToReveal = 3;
        mineCount = 99;
        mineGrid = new Tile[24][30]; // y, x; (0,0) = top left
        for (int i = 0; i < 24; i++) {
            for(int j = 0; j < 30; j++) {
                mineGrid[i][j] = new Tile();
            }
        }
        while (actMines < mineCount) {
            int randomY = (int)(Math.random() * 24);
            int randomX = (int)(Math.random() * 30);
            if (mineGrid[randomY][randomX].getValue() == '0') {
                mineGrid[randomY][randomX].setValue('*');
                actMines++;
            }
        }
        for (int i = 0; i < 24; i++) {
            for(int j = 0; j < 30; j++) {
                if (mineGrid[i][j].getValue() == '*') {
                    if (i != 0)             mineGrid[i-1][j].addOne();
                    if (j != 0)             mineGrid[i][j-1].addOne();
                    if (i != 23)            mineGrid[i+1][j].addOne();
                    if (j != 29)            mineGrid[i][j+1].addOne();
                    if (i != 0 && j != 0)   mineGrid[i-1][j-1].addOne();
                    if (i != 0 && j != 29)  mineGrid[i-1][j+1].addOne();
                    if (i != 23 && j != 0)  mineGrid[i+1][j-1].addOne();
                    if (i != 23 && j != 29) mineGrid[i+1][j+1].addOne();
                }
            }
        }
    }

    public Board(String boardString) {
        // tilesToReveal = 621;
        tilesToReveal = 3;
        mineCount = 99;
        mineGrid = new Tile[24][30];
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 30; j++) {
                mineGrid[i][j] = new Tile();
                mineGrid[i][j].setValue(boardString.charAt(j + 30 * i));
            }
        }
    }

    // Flag the specific tile with specific player
    public void flag(Player p, int x, int y) {
        this.mineCount += mineGrid[y][x].flag(p.getColor());
        return;
    }

    // Return -1 if tile was previously revealed, 0 if bomb, 1 if normal;
    public int reveal(int x, int y) {
        if (x < 0 || y < 0 || y > 23 || x > 29) return 1;
        if (mineGrid[y][x].reveal()) {
            if (mineGrid[y][x].getValue() == '*') {
                this.mineCount--;
                return 0;
            }
            else if (mineGrid[y][x].getValue() == '0') {
                tilesToReveal--;
                this.reveal(x-1, y);
                this.reveal(x, y - 1);
                this.reveal(x+1,y);
                this.reveal(x,y+1);
                this.reveal(x-1,y-1);
                this.reveal(x-1,y+1);
                this.reveal(x+1,y-1);
                this.reveal(x+1,y+1);
            }
            else
                tilesToReveal--;
            return 1;
        }
        else return -1;
    }

    public char[][] displayBoard() {
        char[][] result = new char[24][30];
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 30; j++) {
                result[i][j] = mineGrid[i][j].display();
            }
        }
        return result;
    }
    public int getMineCount() {
        return this.mineCount;
    }

    public boolean isCleared() {
        return this.tilesToReveal <= 0;
    }

    public String exportBoard() {
        String result = "";
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 30; j++) {
                result += mineGrid[i][j].getValue();
            }
        }
        return result;
    }
}
