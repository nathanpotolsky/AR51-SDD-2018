package com.example.nathan.myapplication;

//An extension of the Move class specified for Checkers. Handles a starting an end point, as well
//as detecting if a move is a jump move
public class CheckersMove extends Move {
    Position startPoint;
    boolean isJumpMove;

    public CheckersMove(Position startPoint, Position endPoint, boolean isJumpMove) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.isJumpMove = isJumpMove;
    }

    public Position getStartPoint() {
        return startPoint;
    }

    public boolean getIsJumpMove() {
        return isJumpMove;
    }

    //Creates a message for an instant take
    public String getInstantTakeMessage() {
        return "Our (" + startPoint.x + ", " + startPoint.y + ") can instantly jump to (" + endPoint.x + ", " + endPoint.y + ")";
    }

    //Creates a message for an instant loss
    public String getInstantLossMessage() {
        return "Our (" + ((startPoint.x + endPoint.x) / 2) + ", " + ((startPoint.y + endPoint.y) / 2) + ") is in peril by their (" + startPoint.x + ", " +startPoint.y + ")";
    }

    //Creates a message for the best move
    public String getBestMoveMessage() {
        return "Our (" + startPoint.x + ", " + startPoint.y + ") should move to (" + endPoint.x + ", " + endPoint.y + ")";
    }

    //Creates a message for the worst move
    public String getWorstMoveMessage() {
        return "Our (" + startPoint.x + ", " + startPoint.y + ") shouldn't move to (" + endPoint.x + ", " + endPoint.y + ")";
    }

    //Creates a message for a ranked move
    public String getRankedMessage() {
        return "Our (" + startPoint.x + ", " + startPoint.y + ") could move to (" + endPoint.x + ", " + endPoint.y + ")";
    }
}
