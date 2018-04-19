package com.example.nathan.myapplication;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

//Allows the user to select which color their team is
public class ColorSelection extends AppCompatActivity {

    boolean selected = true;
    private static CheckerBoard checkerBoard = BoardDetectionActivity.getCheckerBoard();

    public static CheckerBoard getCheckerBoard() {return checkerBoard;}



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_selection);

        ContextWrapper team1CW = new ContextWrapper(getApplicationContext());
        File team1path = new File(team1CW.getDir("final_draught", Context.MODE_PRIVATE), "team1.png");

        //Creates the first Team image
        if(team1path.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(team1path.getAbsolutePath());
            ImageView myImage = findViewById(R.id.firstColorPiece);
            myImage.setImageBitmap(myBitmap);
        }
        else{
            Log.d("imageWrite", "TEAM1 FAILURE ---");
        }

        ContextWrapper team2CW = new ContextWrapper(getApplicationContext());
        File team2path = new File(team2CW.getDir("final_draught", Context.MODE_PRIVATE), "team2.png");

        //Creates the second Team Image
        if(team2path.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(team2path.getAbsolutePath());
            ImageView myImage = findViewById(R.id.secondColorPiece);
            myImage.setImageBitmap(myBitmap);
        }
        else{
            Log.d("imageWrite", "TEAM2 FAILURE ---");
        }

        //Creates the forward button
        Button forwardButton = findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent optionIntent = new Intent(getApplicationContext(), KingTagging.class);
                startActivity(optionIntent);
            }
        });

        //Creates the first and second Team buttons
        final Button firstColorButton = findViewById(R.id.firstColorPieceCheckMark);
        final Button secondColorButton = findViewById(R.id.secondColorPieceCheckMark);
        firstColorButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Switches teams if the selection changes states
                if (!selected) {
                    checkerBoard.switchTeams();
                }
                selected = true;
                firstColorButton.setBackgroundResource(R.drawable.greencheck);
                secondColorButton.setBackgroundColor(Color.TRANSPARENT);


            }
        });
        secondColorButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Switches teams if the selection changes states
                if (selected) {
                    checkerBoard.switchTeams();
                }
                selected = false;
                firstColorButton.setBackgroundColor(Color.TRANSPARENT);
                secondColorButton.setBackgroundResource(R.drawable.greencheck);
            }
        });
    }
}
