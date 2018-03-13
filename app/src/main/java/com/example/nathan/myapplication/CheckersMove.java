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
}
