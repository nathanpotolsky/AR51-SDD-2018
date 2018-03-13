package com.example.nathan.myapplication;

import android.util.Log;

import java.util.LinkedList;

public class CheckerBoard extends Board{

    LinkedList<CheckersMove> ourMoves = new LinkedList<CheckersMove>();
    LinkedList<CheckersMove> theirMoves = new LinkedList<CheckersMove>();

    public CheckerBoard(){
        rows = 8;
        columns = 8;
        intBoard = new int[rows][columns];
    }

    void addPiecesToLists(){
        //Go through board, looking for our men then add to a list
        for(int i = 0; i<rows; i++)
        {
            for(int j = 0; j<columns; j++)
            {
                //If location has id 1 or 3, it means one of our pieces (3 means king)
                if(intBoard[i][j] == 1 || intBoard[i][j] == 3){
                    ourPieces.add(new Position(i, j));
                    //Log.d("myTag", "We have a piece at (" + i + ", " + j + ")");
                }
                else if(intBoard[i][j] == 2 || intBoard[i][j] == 4) {
                    theirPieces.add(new Position(i, j));
                }
            }
        }
    }

    void findValidMoves(boolean isOurTeam){
        //Default to processing moves for our team
        LinkedList<Position> piecesList = ourPieces;
        LinkedList<CheckersMove> movesList = ourMoves;
        int otherTeamMan = 2;
        int otherTeamKing = 4;

        //If we're processing moves for their team
        if(isOurTeam == false){
            piecesList = theirPieces;
            movesList = theirMoves;
            otherTeamMan = 1;
            otherTeamKing = 3;
        }

        for(int i = 0; i < piecesList.size(); i++)
        {
            int x = piecesList.get(i).x;
            int y = piecesList.get(i).y;
            Position proposedUpLeftPoint = new Position(x-1,y-1);
            Position proposedUpRightPoint = new Position(x-1,y+1);
            Position proposedDownLeftPoint = new Position(x+1,y-1);
            Position proposedDownRightPoint = new Position(x+1,y+1);
            //If proposed space is within board
            if(isWithinBoard(proposedUpLeftPoint)){
                //If proposedUpLeftPoint is empty
                if(intBoard[x-1][y-1] == 0){
                    movesList.add(new CheckersMove(new Position(x, y), proposedUpLeftPoint, false));
                    //Log.d("myTag", "(" + x + ", " + y + ") can go to (" + proposedUpLeftPoint.x + ", " + proposedUpLeftPoint.y + ")");
                }
                //If proposedUpLeftPoint has enemy piece AND the square on the other side is free
                else if((intBoard[x-1][y-1] == otherTeamMan || intBoard[x][y] == otherTeamKing) && (intBoard[x-2][y-2] == 0)){
                    proposedUpLeftPoint = new Position(x-2,y-2);
                    movesList.add(new CheckersMove(new Position(x, y), proposedUpLeftPoint, true));
                    //Log.d("myTag", "(" + x + ", " + y + ") can jump to (" + proposedUpLeftPoint.x + ", " + proposedUpLeftPoint.y + ")");
                }
            }
            //If proposed space is within board
            if(isWithinBoard(proposedUpRightPoint)){
                //If proposedRightPoint is empty
                if(intBoard[x-1][y+1] == 0){
                    movesList.add(new CheckersMove(new Position(x, y), proposedUpRightPoint, false));
                    //Log.d("myTag", "(" + x + ", " + y + ") can go to (" + proposedUpRightPoint.x + ", " + proposedUpRightPoint.y + ")");
                }
                //If proposedRightPoint has enemy piece AND the square on the other side is free
                else if((intBoard[x-1][y+1] == otherTeamMan || intBoard[x][y] == otherTeamKing) && (intBoard[x-2][y+2] == 0)){
                    proposedUpRightPoint = new Position(x-2,y+2);
                    movesList.add(new CheckersMove(new Position(x, y), proposedUpRightPoint, true));
                    //Log.d("myTag", "(" + x + ", " + y + ") can jump to (" + proposedUpRightPoint.x + ", " + proposedUpRightPoint.y + ")");
                }
            }
            //If this piece is a king and if proposed space is within board
            if(intBoard[x][y] == 3 && isWithinBoard(proposedDownLeftPoint)){
                //If proposedDownLeftPoint is empty
                if(intBoard[x+1][y-1] == 0){
                    movesList.add(new CheckersMove(new Position(x, y), proposedDownLeftPoint, false));
                    //Log.d("myTag", "(" + x + ", " + y + ") can go to (" + proposedDownLeftPoint.x + ", " + proposedDownLeftPoint.y + ")");
                }
                //If proposedDownLeftPoint has enemy piece AND the square on the other side is free
                else if((intBoard[x+1][y-1] == otherTeamMan || intBoard[x][y] == otherTeamKing) && (intBoard[x+2][y-2] == 0)){
                    proposedDownLeftPoint = new Position(x+2,y-2);
                    movesList.add(new CheckersMove(new Position(x, y), proposedDownLeftPoint, true));
                    //Log.d("myTag", "(" + x + ", " + y + ") can jump to (" + proposedDownLeftPoint.x + ", " + proposedDownLeftPoint.y + ")");
                }
            }
            //If this piece is a king and if proposed space is within board
            if(intBoard[x][y] == 3 && isWithinBoard(proposedDownRightPoint)){
                //If proposedDownRightPoint is empty
                if(intBoard[x+1][y+1] == 0){
                    movesList.add(new CheckersMove(new Position(x, y), proposedDownRightPoint, false));
                    //Log.d("myTag", "(" + x + ", " + y + ") can go to (" + proposedDownRightPoint.x + ", " + proposedDownRightPoint.y + ")");
                }
                //If proposedDownRightPoint has enemy piece AND the square on the other side is free
                else if((intBoard[x+1][y+1] == otherTeamMan || intBoard[x][y] == otherTeamKing) && (intBoard[x+2][y+2] == 0)){
                    proposedDownRightPoint = new Position(x+2,y+2);
                    movesList.add(new CheckersMove(new Position(x, y), proposedDownRightPoint, true));
                    //Log.d("myTag", "(" + x + ", " + y + ") can jump to (" + proposedDownRightPoint.x + ", " + proposedDownRightPoint.y + ")");
                }
            }
        }
    }

    boolean isWithinBoard (Position endPoint){
        if((endPoint.x >= 0) && (endPoint.x <= rows-1) && (endPoint.y >= 0) && (endPoint.y <= columns-1)){
            return true;
        }
        return false;
    }

    void printAllPieces(){
        for(int i = 0; i < ourPieces.size(); i++)
        {
            int x = ourPieces.get(i).getX();
            int y = ourPieces.get(i).getY();
            Log.d("myTag", "We have a piece at (" + x + ", " + y + ")");
        }

        for(int i = 0; i < theirPieces.size(); i++)
        {
            int x = theirPieces.get(i).getX();
            int y = theirPieces.get(i).getY();
            Log.d("myTag", "They have a piece at (" + x + ", " + y + ")");
        }
    }

    void printAllMoves(){
        for(int i = 0; i < ourMoves.size(); i++)
        {
            int startX = ourMoves.get(i).getStartPoint().getX();
            int startY = ourMoves.get(i).getStartPoint().getY();
            int endX = ourMoves.get(i).getEndPoint().getX();
            int endY = ourMoves.get(i).getEndPoint().getY();
            boolean isJumpMove = ourMoves.get(i).getIsJumpMove();
            if(isJumpMove ==  false){
                Log.d("myTag", "Our (" + startX + ", " + startY + ") can move to (" + endX + ", " + endY + ") [move]");
            }
            else{
                Log.d("myTag", "Our (" + startX + ", " + startY + ") can jump to (" + endX + ", " + endY + ") [jump] [Instant Take]");
            }
        }

        for(int i = 0; i < theirMoves.size(); i++)
        {
            int startX = theirMoves.get(i).getStartPoint().getX();
            int startY = theirMoves.get(i).getStartPoint().getY();
            int endX = theirMoves.get(i).getEndPoint().getX();
            int endY = theirMoves.get(i).getEndPoint().getY();
            boolean isJumpMove = theirMoves.get(i).getIsJumpMove();
            if(isJumpMove ==  false){
                Log.d("myTag", "Their (" + startX + ", " + startY + ") can move to (" + endX + ", " + endY + ") [move]");
            }
            else{
                Log.d("myTag", "Their (" + startX + ", " + startY + ") can jump to (" + endX + ", " + endY + ") [jump] [Our (" + ((startX+endX)/2) + ", " + ((startY+endY)/2) + ") is in peril] [Instant Loss]");
            }
        }
    }

}
