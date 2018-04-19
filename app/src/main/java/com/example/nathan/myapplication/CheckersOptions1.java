package com.example.nathan.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.util.Log;

public class CheckersOptions1 extends AppCompatActivity {

    /** Initial option selections are false */
    private static boolean showAllMoves = false;
    private static boolean rankAllMoves = false;
    private static boolean instantTakes = false;
    private static boolean instantLosses = false;
    private static boolean bestMove = false;
    private static boolean worstMove = false;

    /** Return current selection of each option */
    public static boolean showAllMoves() {
        return showAllMoves;
    }
    public static boolean rankAllMoves() {
        return rankAllMoves;
    }
    public static boolean instantTakes() {
        return instantTakes;
    }
    public static boolean instantLosses() {
        return instantLosses;
    }
    public static boolean bestMove() {
        return bestMove;
    }
    public static boolean worstMove() {
        return worstMove;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkers_options1);

        // Set up forward button
        Button forwardButton = (Button)findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent optionIntent = new Intent(getApplicationContext(), BoardDetectionActivity.class);
                startActivity(optionIntent);
            }
        });

        Button showAllMovesButton = (Button)findViewById(R.id.toggleButton);
        showAllMovesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showAllMoves = !showAllMoves;
            }
        });

        Button instantTakesButton = (Button)findViewById(R.id.toggleButton2);
        instantTakesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                instantTakes = !instantTakes;
            }
        });

        Button instantLossesButton = (Button)findViewById(R.id.toggleButton3);
        instantLossesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                instantLosses = !instantLosses;
            }
        });

        Button bestMoveButton = (Button)findViewById(R.id.toggleButton4);
        bestMoveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                bestMove = !bestMove;
            }
        });

        Button worstMoveButton = (Button)findViewById(R.id.toggleButton5);
        worstMoveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                worstMove = !worstMove;
            }
        });

        Button rankAllMovesButton = (Button)findViewById(R.id.toggleButton6);
        rankAllMovesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                rankAllMoves = !rankAllMoves;
                Log.d("myTag", "rankAllMoves: " + rankAllMoves);
            }
        });
    }
}
