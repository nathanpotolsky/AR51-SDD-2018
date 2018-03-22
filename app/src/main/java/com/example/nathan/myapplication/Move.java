package com.example.nathan.myapplication;

public abstract class Move {
    Position endPoint;
    double weight;
    String description;

//    public Move() {
//
//    }


    public Position getEndPoint() {
        return endPoint;
    }
}
