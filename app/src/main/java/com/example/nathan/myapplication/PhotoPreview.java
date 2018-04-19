package com.example.nathan.myapplication;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

//Shows a preview of the captured images to the user to ensure that a proper photo was taken.
//Shows a normalized view of the board and two pieces representing the opposing teams.
public class PhotoPreview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

        //Retrieves the normalized board file
        ContextWrapper normalizedCW = new ContextWrapper(getApplicationContext());
        File normalizedPath = new File(normalizedCW.getDir("final_draught", Context.MODE_PRIVATE), "normalizedCheckerboard.png");

        if(normalizedPath.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(normalizedPath.getAbsolutePath());
            ImageView myImage = findViewById(R.id.photoPreview);
            myImage.setImageBitmap(myBitmap);
        }
        else{
            Log.d("imageWrite", "NORMALIZED FAILURE ---");
        }

        //Retrieves the first team color image
        ContextWrapper team1CW = new ContextWrapper(getApplicationContext());
        File team1path = new File(team1CW.getDir("final_draught", Context.MODE_PRIVATE), "team1.png");

        if(team1path.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(team1path.getAbsolutePath());
            ImageView myImage = findViewById(R.id.team1);
            myImage.setImageBitmap(myBitmap);
        }
        else{
            Log.d("imageWrite", "TEAM1 FAILURE ---");
        }

        //Retrieves the second team color image
        ContextWrapper team2CW = new ContextWrapper(getApplicationContext());
        File team2path = new File(team2CW.getDir("final_draugt", Context.MODE_PRIVATE), "team2.png");

        if(team2path.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(team2path.getAbsolutePath());
            ImageView myImage = findViewById(R.id.team2);
            myImage.setImageBitmap(myBitmap);
        }
        else{
            Log.d("imageWrite", "TEAM2 FAILURE ---");
        }

        Button AcceptPhotoButton = findViewById(R.id.AcceptPhotoButon);

        AcceptPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent optionIntent = new Intent(getApplicationContext(), ColorSelection.class);
                startActivity(optionIntent);
            }
        });
    }
}
