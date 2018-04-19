package com.example.nathan.myapplication;

//An abstract move class that can be extended for specific games
public abstract class Move {
    Position endPoint;
    double weight;
    String description;

    public Position getEndPoint() {
        return endPoint;
    }
}
