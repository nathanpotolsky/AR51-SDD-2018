package com.example.nathan.myapplication;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Comparator;

//This is an implementation of the Board class specific to Checkers. It handles finding moves
//specified by the user, as well as swapping team colors and toggling the location of kings.
public class CheckerBoard extends Board{

    ArrayList<CheckersMove> ourMoves = new ArrayList<>();
    ArrayList<CheckersMove> theirMoves = new ArrayList<>();
    ArrayList<Position> instantLosses = new ArrayList<>();

    //Initializes a dummy CheckerBoard
    public CheckerBoard() {
        rows = 8;
        columns = 8;
        intBoard = new int[rows][columns];
        intBoard[0] = new int[] {3,0,0,0,0,0,0,4};
        intBoard[1] = new int[] {0,0,0,0,0,0,0,0};
        intBoard[2] = new int[] {0,0,0,0,0,0,0,0};
        intBoard[3] = new int[] {0,0,0,0,0,0,0,0};
        intBoard[4] = new int[] {0,0,0,0,0,0,0,0};
        intBoard[5] = new int[] {0,0,0,0,0,2,0,0};
        intBoard[6] = new int[] {0,0,0,0,1,0,0,0};
        intBoard[7] = new int[] {0,0,0,0,0,0,0,0};
    }

    //Converts an Object[] to an int[][] and initializes the pieces
    public CheckerBoard(Object[] arr){
        rows = 8;
        columns = 8;
        intBoard = new int[rows][columns];
        for (int i = 0; i < arr.length; ++i) {
            int[] temp = (int[]) arr[i];
            for(int j = 0; j < temp.length; ++j) {
                intBoard[i][j] = temp[j];
            }
        }
    }

    //Toggles if a piece is a king or not at location (x, y)
    public void setKing(int x, int y) {
        if (intBoard[x][y] == 1) {
            intBoard[x][y] = 3;
        } else if (intBoard[x][y] == 2) {
            intBoard[x][y] = 4;
        } else if (intBoard[x][y] == 3) {
            intBoard[x][y] = 1;
        } else if (intBoard[x][y] == 4) {
            intBoard[x][y] = 2;
        }
    }

    //Switches all the teams on the board
    public void switchTeams() {
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                if (intBoard[i][j] == 1) {
                    intBoard[i][j] = 2;
                } else if (intBoard[i][j] == 2) {
                    intBoard[i][j] = 1;
                } else if (intBoard[i][j] == 3) {
                    intBoard[i][j] = 4;
                } else if (intBoard[i][j] == 4) {
                    intBoard[i][j] = 3;
                }
            }
        }
    }

    //Ranks all the moves from best (highest) weight to worst (lowest) weight
    public ArrayList<CheckersMove> rankedMoves() {
        assignMoveWeights();
        ourMoves = sortMoves(ourMoves);
        return ourMoves;
    }

    //Sorts the checker moves by weights
    public ArrayList<CheckersMove> sortMoves(ArrayList<CheckersMove> moveArrayToSort){
        moveArrayToSort.sort( new Comparator<CheckersMove>(){
            @Override
            public int compare(CheckersMove o1, CheckersMove o2){
                if (o1.weight > o2.weight) {
                    return -1;
                } else if (o1.weight < o2.weight) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        return moveArrayToSort;
    }

    //Gathers the instant losses that may befall the user
    public ArrayList<CheckersMove> instantLosses() {
        ArrayList<CheckersMove> takes = new ArrayList<>();
        Iterator<CheckersMove> itr = theirMoves.iterator();
        while (itr.hasNext()) {
            CheckersMove move = itr.next();
            if (move.isJumpMove) {
                takes.add(move);
            }
        }
        return takes;
    }

    //Gathers the instant takes that the user can make
    public ArrayList<CheckersMove> instantTakes() {
        ArrayList<CheckersMove> takes = new ArrayList<>();
        Iterator<CheckersMove> itr = ourMoves.iterator();
        while (itr.hasNext()) {
            CheckersMove move = itr.next();
            if (move.isJumpMove) {
                takes.add(move);
            }
        }
        return takes;
    }

    //Returns all the moves
    public ArrayList<CheckersMove> allMoves() {
        return ourMoves;
    }

    //Returns the best move
    public CheckersMove bestMove() {
        assignMoveWeights();
        ourMoves = sortMoves(ourMoves);
        return ourMoves.get(0);
    }

    //Returns the worst move
    public CheckersMove worstMove() {
        assignMoveWeights();
        ourMoves = sortMoves(ourMoves);
        return ourMoves.get(ourMoves.size()-1);
    }

    //Reinitializes the pieces lists
    void addPiecesToLists(){
        //Clear our piece lists to be empty
        ourPieces.clear();
        theirPieces.clear();
        //Go through board, looking for our men then add to a list
        for(int i = 0; i<rows; i++)
        {
            for(int j = 0; j<columns; j++)
            {
                //If location has id 1 or 3, it means one of our pieces (3 means king)
                if(intBoard[i][j] == 1 || intBoard[i][j] == 3){
                    ourPieces.add(new Position(i, j));
                }
                else if(intBoard[i][j] == 2 || intBoard[i][j] == 4) {
                    theirPieces.add(new Position(i, j));
                }
            }
        }
    }

    //Finds all the moves the player can make
    public void findValidMoves(boolean isOurTeam){
        //Default to processing moves for our team
        ArrayList<Position> piecesList = null;
        ArrayList<CheckersMove> movesList = null;
        int otherTeamMan = 0;
        int otherTeamKing = 0;

        //If we're processing moves for their team
        if(isOurTeam == true){
            ourMoves.clear();
            piecesList = ourPieces;
            movesList = ourMoves;
            otherTeamMan = 2;
            otherTeamKing = 4;
        }
        //If we're processing moves for their team
        else if(isOurTeam == false){
            theirMoves.clear();
            piecesList = theirPieces;
            movesList = theirMoves;
            otherTeamMan = 1;
            otherTeamKing = 3;
        }

        for(int i = 0; i < piecesList.size(); i++)
        {
            int x = piecesList.get(i).x;
            int y = piecesList.get(i).y;
            int curSpaceId = intBoard[x][y];
            Position proposedUpLeftPoint = new Position(x-1,y-1);
            Position proposedUpRightPoint = new Position(x-1,y+1);
            Position proposedDownLeftPoint = new Position(x+1,y-1);
            Position proposedDownRightPoint = new Position(x+1,y+1);
            //If proposed space is within board
            if(isWithinBoard(proposedUpLeftPoint) && (curSpaceId != 2)){
                //If proposedUpLeftPoint is empty
                if(intBoard[x-1][y-1] == 0){
                    movesList.add(new CheckersMove(new Position(x, y), proposedUpLeftPoint, false));
                }
                //If proposedUpLeftPoint has enemy piece AND the square on the other side is free
                else if((intBoard[x-1][y-1] == otherTeamMan || intBoard[x][y] == otherTeamKing) && (intBoard[x-2][y-2] == 0)){
                    proposedUpLeftPoint = new Position(x-2,y-2);
                    movesList.add(new CheckersMove(new Position(x, y), proposedUpLeftPoint, true));
                    if (!isOurTeam) {
                        instantLosses.add(new Position(x-1, y-1));
                    }
                }
            }
            //If proposed space is within board
            if(isWithinBoard(proposedUpRightPoint) && (curSpaceId != 2)){
                //If proposedRightPoint is empty
                if(intBoard[x-1][y+1] == 0){
                    movesList.add(new CheckersMove(new Position(x, y), proposedUpRightPoint, false));
                }
                //If proposedRightPoint has enemy piece AND the square on the other side is free
                else if((intBoard[x-1][y+1] == otherTeamMan || intBoard[x][y] == otherTeamKing) && (intBoard[x-2][y+2] == 0)){
                    proposedUpRightPoint = new Position(x-2,y+2);
                    movesList.add(new CheckersMove(new Position(x, y), proposedUpRightPoint, true));
                    if (!isOurTeam) {
                        instantLosses.add(new Position(x-1, y+1));
                    }
                }
            }
            //If this piece is a king and if proposed space is within board
            if(isWithinBoard(proposedDownLeftPoint) && (curSpaceId != 1)){
                //If proposedDownLeftPoint is empty
                if(intBoard[x+1][y-1] == 0){
                    movesList.add(new CheckersMove(new Position(x, y), proposedDownLeftPoint, false));
                }
                //If proposedDownLeftPoint has enemy piece AND the square on the other side is free
                else if((intBoard[x+1][y-1] == otherTeamMan || intBoard[x][y] == otherTeamKing) && (intBoard[x+2][y-2] == 0)){
                    proposedDownLeftPoint = new Position(x+2,y-2);
                    movesList.add(new CheckersMove(new Position(x, y), proposedDownLeftPoint, true));
                    if (!isOurTeam) {
                        instantLosses.add(new Position(x+1, y-1));
                    }
                }
            }
            //If this piece is a king and if proposed space is within board
            if(isWithinBoard(proposedDownRightPoint) && (curSpaceId != 1)){
                //If proposedDownRightPoint is empty
                if(intBoard[x+1][y+1] == 0){
                    movesList.add(new CheckersMove(new Position(x, y), proposedDownRightPoint, false));
                }
                //If proposedDownRightPoint has enemy piece AND the square on the other side is free
                else if((intBoard[x+1][y+1] == otherTeamMan || intBoard[x][y] == otherTeamKing) && (intBoard[x+2][y+2] == 0)){
                    proposedDownRightPoint = new Position(x+2,y+2);
                    movesList.add(new CheckersMove(new Position(x, y), proposedDownRightPoint, true));

                    if (!isOurTeam) {
                        instantLosses.add(new Position(x+1, y+1));
                    }
                }
            }
        }
    }

    //Assigns weights to the moves based on various contexts, as well as updates a description for the move
    void assignMoveWeights() {
        ArrayList<CheckersMove> moves = ourMoves;
        if (moves.size() != 0) {
            Iterator<CheckersMove> itr = moves.iterator();
            while(itr.hasNext()) {
                CheckersMove curr = itr.next();
                curr.weight = 0;
                curr.description = "";

                //Checks if a move is a jump move
                if (curr.isJumpMove) {
                    curr.weight += 2;
                    curr.description += "You can jump over a piece.";
                }

                //check if can be caught/blocks move
                Position startPoint = curr.getStartPoint();
                Position endPoint = curr.getEndPoint();

                Position upLeft = new Position(endPoint.getX()-1, endPoint.getY()-1);
                Position upRight = new Position(endPoint.getX()-1,endPoint.getY()+1);
                Position downLeft = new Position(endPoint.getX()+1, endPoint.getY()-1);
                Position downRight = new Position(endPoint.getX()+1, endPoint.getY()+1);

                //Checks if you blocked or will be captured by an enemy piece
                if (isWithinBoard(upRight) && (intBoard[upRight.getX()][upRight.getY()] == 2 || intBoard[upRight.getX()][upRight.getY()] == 4)) {
                    if (isWithinBoard(downLeft) && intBoard[downLeft.getX()][downLeft.getY()] != 0) {
                        curr.weight += 0.25;
                        if (curr.description.length() == 0) {
                            curr.description += "You blocked a piece.";
                        } else {
                            curr.description += " You blocked a piece's movement.";
                        }
                    } else {
                        curr.weight -= 1;
                        if (curr.description.length() == 0) {
                            curr.description += "You can get captured by a piece.";
                        } else {
                            curr.description += " You can get captured by a piece.";
                        }
                    }
                }

                //Checks if you will be blocked or captured by an enemy piece
                if (isWithinBoard(upLeft) && (intBoard[upLeft.getX()][upLeft.getY()] == 2 || intBoard[upLeft.getX()][upLeft.getY()] == 4)) {
                    if (isWithinBoard(downRight) && intBoard[downRight.getX()][downRight.getY()] != 0) {
                        curr.weight += 0.25;
                        if (curr.description.length() == 0) {
                            curr.description += "You blocked a piece.";
                        } else if (curr.description.contains("You blocked a piece")){
                            curr.description.replace("You blocked a piece", "You blocked two pieces");
                        } else {
                            curr.description += " You blocked a piece.";
                        }
                    } else {
                        curr.weight -= 1;
                        if (curr.description.length() == 0) {
                            curr.description += "You can get captured by a piece.";
                        } else if (curr.description.contains("You can get captured by a piece")) {
                            curr.description.replace("You can get captured by a piece", "You can get captured by two pieces");
                        } else {
                            curr.description += " You can get captured by a piece.";
                        }
                    }
                }

                //Checks if you blocked or will be capture by a piece
                if (isWithinBoard(downLeft) && intBoard[downLeft.getX()][downLeft.getY()] == 4) {
                    if (isWithinBoard(upRight) && intBoard[upRight.getX()][upRight.getY()] != 0) {
                        curr.weight += 0.25;
                        if (curr.description.length() == 0) {
                            curr.description += "You blocked a piece.";
                        } else if (curr.description.contains("You blocked a piece")){
                            curr.description.replace("You blocked a piece", "You blocked two pieces");
                        } else {
                            curr.description += " You blocked a piece.";
                        }
                    } else {
                        curr.weight -= 1;
                        if (curr.description.length() == 0) {
                            curr.description += "You can get captured by a piece.";
                        } else if (curr.description.contains("You can get captured by a piece")) {
                            curr.description.replace("You can get captured by a piece", "You can get captured by two pieces");
                        } else {
                            curr.description += " You can get captured by a piece.";
                        }
                    }
                }

                //Checks if you blocked or will be capture by a piece
                if (isWithinBoard(downRight) && intBoard[downRight.getX()][downRight.getY()] == 4) {
                    if (isWithinBoard(upLeft) && intBoard[upLeft.getX()][upLeft.getY()] != 0) {
                        curr.weight += 0.25;
                        if (curr.description.length() == 0) {
                            curr.description += "You blocked a piece.";
                        } else if (curr.description.contains("You blocked a piece")){
                            curr.description.replace("You blocked a piece", "You blocked two pieces");
                        } else {
                            curr.description += " You blocked a piece.";
                        }
                    } else {
                        curr.weight -= 1;
                        if (curr.description.length() == 0) {
                            curr.description += "You can get captured by a piece.";
                        } else if (curr.description.contains("You can get captured by a piece")) {
                            curr.description.replace("You can get captured by a piece", "You can get captured by two pieces");
                        } else {
                            curr.description += " You can get captured by a piece.";
                        }
                    }
                }

                //Checks if you will move to an edge
                if (!isWithinBoard(upLeft) || !isWithinBoard(upRight)) {
                    curr.weight += 0.5;
                    if (curr.description.length() == 0) {
                        curr.description += "You are safer on an edge.";
                    } else {
                        curr.description += " You are safer on an edge.";
                    }
                }

                //Checks if you can be kinged by moving to the opposite edge
                if (intBoard[startPoint.getX()][startPoint.getY()] == 1 && endPoint.getX() == 0) {
                    curr.weight += 1;
                    if (curr.description.length() == 0) {
                        curr.description += "You can become a king";
                    } else {
                        curr.description += " You can become a king";
                    }
                    if (isWithinBoard(downLeft) && (intBoard[downLeft.getX()][downLeft.getY()] == 2 || intBoard[downLeft.getX()][downLeft.getY()] == 4)) {
                        curr.weight += 0.5;
                        curr.description += " and capture a piece";
                    }
                    if (isWithinBoard(downRight) && (intBoard[downRight.getX()][downRight.getY()] == 2 || intBoard[downRight.getX()][downRight.getY()] == 4)) {
                        curr.weight += 0.5;
                        curr.description += " and capture a piece";
                    }
                    curr.description += ".";
                }

                //Otherwise, the move is neutral
                if (curr.description.length() == 0) {
                    curr.description += "This move is neutral.";
                }
            }
        }
    }

    //Checks if a move is within the board
    boolean isWithinBoard (Position endPoint){
        if((endPoint.x >= 0) && (endPoint.x <= rows-1) && (endPoint.y >= 0) && (endPoint.y <= columns-1)){
            return true;
        }
        return false;
    }

    //Gets the location of all the pieces
    ArrayList<String> getAllPieces(){
        ArrayList<String> arrayList = new ArrayList<>();
        for(int i = 0; i < ourPieces.size(); i++)
        {
            int x = ourPieces.get(i).getX();
            int y = ourPieces.get(i).getY();
            arrayList.add(("We have a piece at (" + x + ", " + y + ")"));
        }

        for(int i = 0; i < theirPieces.size(); i++)
        {
            int x = theirPieces.get(i).getX();
            int y = theirPieces.get(i).getY();
            arrayList.add(("They have a piece at (" + x + ", " + y + ")"));
        }
        return arrayList;
    }

    //Gets a description of all the moves
    ArrayList<String> getAllMoves(boolean ourTeam){
        ArrayList<String> arrayList = new ArrayList<>();
        assignMoveWeights();

        if(ourTeam == true){
            for(int i = 0; i < ourMoves.size(); i++)
            {
                int startX = ourMoves.get(i).getStartPoint().getX();
                int startY = ourMoves.get(i).getStartPoint().getY();
                int endX = ourMoves.get(i).getEndPoint().getX();
                int endY = ourMoves.get(i).getEndPoint().getY();
                boolean isJumpMove = ourMoves.get(i).getIsJumpMove();
                if(isJumpMove ==  false){
                    arrayList.add("Our (" + startX + ", " + startY + ") can move to (" + endX + ", " + endY + ") [move]");
                }
                else{
                    arrayList.add("Our (" + startX + ", " + startY + ") can jump to (" + endX + ", " + endY + ") [jump] [Instant Take]");
                }
            }
        }
        else {
            for (int i = 0; i < theirMoves.size(); i++) {
                int startX = theirMoves.get(i).getStartPoint().getX();
                int startY = theirMoves.get(i).getStartPoint().getY();
                int endX = theirMoves.get(i).getEndPoint().getX();
                int endY = theirMoves.get(i).getEndPoint().getY();
                boolean isJumpMove = theirMoves.get(i).getIsJumpMove();
                if (isJumpMove == false) {
                    arrayList.add("Their (" + startX + ", " + startY + ") can move to (" + endX + ", " + endY + ") [move]");
                } else {
                    arrayList.add("Their (" + startX + ", " + startY + ") can jump to (" + endX + ", " + endY + ") [jump] [Our (" + ((startX + endX) / 2) + ", " + ((startY + endY) / 2) + ") is in peril] [Instant Loss]");
                }
            }
        }
        return arrayList;
    }

}
