package com.example.nathan.myapplication;

import android.util.Log;

public class CheckerBoard extends Board{

    public CheckerBoard(){
        rows = 8;
        columns = 8;
        intBoard = new int[rows][columns];
    }

    void addOurPiecesToList(){
        //Go through board, looking for our men then add to a list
        for(int i = 0; i<rows; i++)
        {
            for(int j = 0; j<columns; j++)
            {
                //If location has id 1 or 3, it means one of our pieces (3 means king)
                if(intBoard[i][j] == 1 || intBoard[i][j] == 3){
                    ourPieces.add(new Point(i, j));
                    Log.d("myTag", "We have a piece at (" + i + ", " + j + ")");
                }
            }

        }
    }

    void findValidMoves(){
        for(int i = 0; i < ourPieces.size(); i++)
        {
            int x = ourPieces.get(i).x;
            int y = ourPieces.get(i).y;
            Point proposedUpLeftPoint = new Point(x-1,y-1);
            Point proposedUpRightPoint = new Point(x-1,y+1);
            Point proposedDownLeftPoint = new Point(x+1,y-1);
            Point proposedDownRightPoint = new Point(x+1,y+1);
            //If proposed space is within board
            if(isWithinBoard(proposedUpLeftPoint)){
                //If proposedUpLeftPoint is empty
                if(intBoard[x-1][y-1] == 0){
                    Log.d("myTag", "(" + x + ", " + y + ") can go to (" + proposedUpLeftPoint.x + ", " + proposedUpLeftPoint.y + ")");
                }
                //If proposedUpLeftPoint has enemy piece AND the square on the other side is free
                else if((intBoard[x-1][y-1] == 2 || intBoard[x][y] == 4) && (intBoard[x-2][y-2] == 0)){
                    proposedUpLeftPoint = new Point(x-2,y-2);
                    Log.d("myTag", "(" + x + ", " + y + ") can jump to (" + proposedUpLeftPoint.x + ", " + proposedUpLeftPoint.y + ")");
                }
            }
            //If proposed space is within board
            if(isWithinBoard(proposedUpRightPoint)){
                //If proposedRightPoint is empty
                if(intBoard[x-1][y+1] == 0){
                    Log.d("myTag", "(" + x + ", " + y + ") can go to (" + proposedUpRightPoint.x + ", " + proposedUpRightPoint.y + ")");
                }
                //If proposedRightPoint has enemy piece AND the square on the other side is free
                else if((intBoard[x-1][y+1] == 2 || intBoard[x][y] == 4) && (intBoard[x-2][y+2] == 0)){
                    proposedUpRightPoint = new Point(x-2,y+2);
                    Log.d("myTag", "(" + x + ", " + y + ") can jump to (" + proposedUpRightPoint.x + ", " + proposedUpRightPoint.y + ")");
                }
            }
            //If this piece is a king and if proposed space is within board
            if(intBoard[x][y] == 3 && isWithinBoard(proposedDownLeftPoint)){
                //If proposedDownLeftPoint is empty
                if(intBoard[x+1][y-1] == 0){
                    Log.d("myTag", "(" + x + ", " + y + ") can go to (" + proposedDownLeftPoint.x + ", " + proposedDownLeftPoint.y + ")");
                }
                //If proposedDownLeftPoint has enemy piece AND the square on the other side is free
                else if((intBoard[x+1][y-1] == 2 || intBoard[x][y] == 4) && (intBoard[x+2][y-2] == 0)){
                    proposedDownLeftPoint = new Point(x+2,y-2);
                    Log.d("myTag", "(" + x + ", " + y + ") can jump to (" + proposedDownLeftPoint.x + ", " + proposedDownLeftPoint.y + ")");
                }
            }
            //If this piece is a king and if proposed space is within board
            if(intBoard[x][y] == 3 && isWithinBoard(proposedDownRightPoint)){
                //If proposedDownRightPoint is empty
                if(intBoard[x+1][y+1] == 0){
                    Log.d("myTag", "(" + x + ", " + y + ") can go to (" + proposedDownRightPoint.x + ", " + proposedDownRightPoint.y + ")");
                }
                //If proposedDownRightPoint has enemy piece AND the square on the other side is free
                else if((intBoard[x+1][y+1] == 2 || intBoard[x][y] == 4) && (intBoard[x+2][y+2] == 0)){
                    proposedDownRightPoint = new Point(x+2,y+2);
                    Log.d("myTag", "(" + x + ", " + y + ") can jump to (" + proposedDownRightPoint.x + ", " + proposedDownRightPoint.y + ")");
                }
            }
            else{

            }
        }
    }

    boolean isWithinBoard (Point endPoint){
        if((endPoint.x >= 0) && (endPoint.x <= rows-1) && (endPoint.y >= 0) && (endPoint.y <= columns-1)){
            return true;
        }
        return false;
    }

//    boolean isValidMove (Point startPoint, Point endPoint){
//        if((endPoint.x >= 0) && (endPoint.x <= rows-1) && (endPoint.y >= 0) && (endPoint.y <= columns-1)){
//            return true;
//        }
//        return false;
//    }
}
