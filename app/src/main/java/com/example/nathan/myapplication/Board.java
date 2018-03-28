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
