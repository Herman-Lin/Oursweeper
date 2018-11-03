package com.cs117.oursweeper;

import com.cs117.oursweeper.Tile;

import java.util.Random;

public class Board {
    private Tile[][] mineGrid;
    private boolean[][] touchable;
    private int mineCount = 99;

    public Board() {
        int actMines = 0;
        touchable = new boolean[30][24]; // y, x; (0,0) = top left
        mineGrid = new Tile[30][24]; // y, x; (0,0) = top left
        for (int i = 0; i < 30; i++) {
            for(int j = 0; j < 24; j++) {
                touchable[i][j] = true;
                mineGrid[i][j] = new Tile();
            }
        }
        touchable[0][0] = false;
        touchable[0][1] = false;
        touchable[1][0] = false;
        while (actMines < mineCount) {
            int randomY = (int)(Math.random() * 30);
            int randomX = (int)(Math.random() * 24);
            if (mineGrid[randomY][randomX].getValue() == '0') {
                mineGrid[randomY][randomX].setValue('*');
                actMines++;
            }
        }

        for (int i = 0; i < 30; i++) {
            for(int j = 0; j < 24; j++) {
                if (mineGrid[i][j].getValue() == '*') {
                    if (i != 0)             mineGrid[i-1][j].addOne();
                    if (j != 0)             mineGrid[i][j-1].addOne();
                    if (i != 29)            mineGrid[i+1][j].addOne();
                    if (j != 23)            mineGrid[i][j+1].addOne();
                    if (i != 0 && j != 0)   mineGrid[i-1][j-1].addOne();
                    if (i != 0 && j != 23)  mineGrid[i-1][j+1].addOne();
                    if (i != 29 && j != 0)  mineGrid[i+1][j-1].addOne();
                    if (i != 29 && j != 23) mineGrid[i+1][j+1].addOne();
                }
            }
        }
    }

    // Return False if tile was not revealed AND it is a mine
    public boolean reveal(int i, int j) {
        if (mineGrid[i][j].reveal()) {
            if (mineGrid[i][j].getValue() == '0') {
                if (i != 0)             mineGrid[i-1][j].reveal();
                if (j != 0)             mineGrid[i][j-1].reveal();
                if (i != 29)            mineGrid[i+1][j].reveal();
                if (j != 23)            mineGrid[i][j+1].reveal();
                if (i != 0 && j != 0)   mineGrid[i-1][j-1].reveal();
                if (i != 0 && j != 23)  mineGrid[i-1][j+1].reveal();
                if (i != 29 && j != 0)  mineGrid[i+1][j-1].reveal();
                if (i != 29 && j != 23) mineGrid[i+1][j+1].reveal();
                return true;
            }
            return mineGrid[i][j].getValue() != '*';
        }
        else return true;
    }

    public char[][] displayBoard() {
        char[][] result = new char[30][24];
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 24; j++) {
                result[i][j] = mineGrid[i][j].display();
            }
        }
        return result;
    }
    public int getMineCount() {
        return this.mineCount;
    }
}
