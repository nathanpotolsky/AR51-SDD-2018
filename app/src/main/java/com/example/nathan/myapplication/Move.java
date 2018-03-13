package com.example.nathan.myapplication;

public abstract class Move {
    Position endPoint;
    int weight;
    String description;

//    public Move() {
//
//    }


    public Position getEndPoint() {
        return endPoint;
    }
}
