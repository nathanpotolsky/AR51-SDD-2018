package com.example.nathan.myapplication;

import android.util.Log;

import java.util.LinkedList;

public abstract class Board {

    int rows;
    int columns;
    int[][] intBoard;
    LinkedList<Position> ourPieces = new LinkedList<Position>();
    LinkedList<Position> theirPieces = new LinkedList<Position>();

    public void printBoard(){
        intBoard[0] = new int[] {0,0,0,0,0,0,0,0};
        intBoard[1] = new int[] {0,0,0,0,0,0,0,0};
        intBoard[2] = new int[] {0,0,0,0,0,0,0,0};
        intBoard[3] = new int[] {0,0,0,0,0,0,0,0};
        intBoard[4] = new int[] {0,0,0,0,0,0,0,0};
        intBoard[5] = new int[] {0,0,0,1,0,0,0,0};
        intBoard[6] = new int[] {0,0,0,0,2,0,0,0};
        intBoard[7] = new int[] {0,0,0,1,0,1,0,0};

        Log.d("myTag", "    0 1 2 3 4 5 6 7 ");
        Log.d("myTag", "  /-----------------");

        for(int i = 0; i<rows; i++)
        {
            String currentRow = i + " |";
            for(int j = 0; j<columns; j++)
            {
                currentRow += " " + intBoard[i][j];
            }
            Log.d("myTag", currentRow);
        }
    }
}
