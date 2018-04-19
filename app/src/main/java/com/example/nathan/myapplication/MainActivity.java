package com.example.nathan.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//The main page for the app where multiple games are implemented.
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creates the checkers button. Directs the user to the option selection page.
        Button checkersButton = (Button)findViewById(R.id.checkersButton);
        checkersButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent optionIntent = new Intent(getApplicationContext(), CheckersOptions1.class);
                startActivity(optionIntent);
            }
        });

        //Creates the Connect Four button. Performs a test on a dummy board.
        Button connectFourButton = (Button)findViewById(R.id.connectFourButton);
        connectFourButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                CheckerBoard tempCheckerBoard = new CheckerBoard();
                tempCheckerBoard.printBoard();
                tempCheckerBoard.addPiecesToLists();
                tempCheckerBoard.findValidMoves(true);
                tempCheckerBoard.findValidMoves(false);
                Log.d("myTag", "  \\-----------------");
                tempCheckerBoard.getAllPieces();
            }
        });
    }
}
