package com.example.nathan.myapplication;

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

    public String getInstantTakeMessage() {
        return "Our (" + startPoint.x + ", " + startPoint.y + ") can instantly jump to (" + endPoint.x + ", " + endPoint.y + ")";
    }

    public String getInstantLossMessage() {
        return "Our (" + ((startPoint.x + endPoint.x) / 2) + ", " + ((startPoint.y + endPoint.y) / 2) + ") is in peril by their (" + startPoint.x + ", " +startPoint.y + ")";
    }

    public String getBestMoveMessage() {
        return "Our (" + startPoint.x + ", " + startPoint.y + ") should move to (" + endPoint.x + ", " + endPoint.y + ")";
    }

    public String getWorstMoveMessage() {
        return "Our (" + startPoint.x + ", " + startPoint.y + ") shouldn't move to (" + endPoint.x + ", " + endPoint.y + ")";
    }

    public String getRankedMessage() {
        return "Our (" + startPoint.x + ", " + startPoint.y + ") could move to (" + endPoint.x + ", " + endPoint.y + ")";
    }
}
