package com.example.nathan.myapplication;

import android.util.Log;

import java.util.ArrayList;

//This class represents the abstract Board class. It can be extended to support other games with
//square or rectangular boards such as Connect Four and Othello.

public abstract class Board {

    int rows;
    int columns;
    int[][] intBoard;
    ArrayList<Position> ourPieces = new ArrayList<>();
    ArrayList<Position> theirPieces = new ArrayList<>();

    //Prints the board using text characters
    public ArrayList<String> printBoardText() {
        ArrayList<String> arrayList = new ArrayList<>();

        Log.d("myTag", "    0 1 2 3 4 5 6 7 ");
        Log.d("myTag", "  /-----------------");
        arrayList.add("     0_1_2_3_4_5_6_7");

        for(int i = 0; i<rows; i++)
        {
            String currentRow = i + " |";
            for(int j = 0; j<columns; j++)
            {
                if(intBoard[i][j] == 1){
                    currentRow += "1";
                }
                else if(intBoard[i][j] == 2){
                    currentRow += "2️";
                }
                else if(intBoard[i][j] == 3){
                    currentRow += "3";
                }
                else if(intBoard[i][j] == 4){
                    currentRow += "4";
                }
                else{
                    currentRow += "0";
                }
            }
            Log.d("myTag", currentRow);
            arrayList.add(currentRow);
        }
        return arrayList;
    }

    //Prints the board using emojis for certain text characters
    public ArrayList<String> printBoard(){
        ArrayList<String> arrayList = new ArrayList<>();

        Log.d("myTag", "    0 1 2 3 4 5 6 7 ");
        Log.d("myTag", "  /-----------------");
        arrayList.add("     0_1_2_3_4_5_6_7");

        for(int i = 0; i<rows; i++)
        {
            String currentRow = i + " |";
            for(int j = 0; j<columns; j++)
            {
                if(intBoard[i][j] == 1){
                    currentRow += "♦";
                }
                else if(intBoard[i][j] == 2){
                    currentRow += "♣️";
                }
                else if(intBoard[i][j] == 3){
                    currentRow += "❤️";
                }
                else if(intBoard[i][j] == 4){
                    currentRow += "\uD83D\uDDA4️";
                }
                else{
                    currentRow += "⬜";
                }
            }
            Log.d("myTag", currentRow);
            arrayList.add(currentRow);
        }
        return arrayList;
    }
}
