package com.example.nathan.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

//Allows the user to select which type of moves they want displayed by the app. Each of the
//buttons is a toggle. Other pages can access the options the user chose through the getter functions.
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
        Button forwardButton = findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent optionIntent = new Intent(getApplicationContext(), BoardDetectionActivity.class);
                startActivity(optionIntent);
            }
        });

        //Set up Show All Moves button
        Button showAllMovesButton = findViewById(R.id.toggleButton);
        showAllMovesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showAllMoves = !showAllMoves;
            }
        });

        //Set up Instant Takes button
        Button instantTakesButton = findViewById(R.id.toggleButton2);
        instantTakesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                instantTakes = !instantTakes;
            }
        });

        //Set up Instant Losses button
        Button instantLossesButton = findViewById(R.id.toggleButton3);
        instantLossesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                instantLosses = !instantLosses;
            }
        });

        //Sets up Best Move button
        Button bestMoveButton = findViewById(R.id.toggleButton4);
        bestMoveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                bestMove = !bestMove;
            }
        });

        //Sets up Worst Move button
        Button worstMoveButton = findViewById(R.id.toggleButton5);
        worstMoveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                worstMove = !worstMove;
            }
        });

        //Sets up Rank All Moves button
        Button rankAllMovesButton = findViewById(R.id.toggleButton6);
        rankAllMovesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                rankAllMoves = !rankAllMoves;
            }
        });
    }
}
