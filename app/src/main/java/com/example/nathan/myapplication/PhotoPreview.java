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

public class PhotoPreview extends AppCompatActivity {

    private static CheckerBoard checkerBoard = new CheckerBoard();

    public static CheckerBoard getCheckerBoard()
    {
        return checkerBoard;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

//        File imgFile = new  File("/sdcard/Images/test_image.jpg");
        ContextWrapper normalizedCW = new ContextWrapper(getApplicationContext());
        File normalizedPath = new File(normalizedCW.getDir("dank_memes", Context.MODE_PRIVATE), "normalizedCheckerboard.png");

        if(normalizedPath.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(normalizedPath.getAbsolutePath());

            Log.d("imageWrite", normalizedPath.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.photoPreview);

            myImage.setImageBitmap(myBitmap);
//            Log.d("imageWrite", "SUCCESS ---");
//
//            String pathStr = path1.getAbsolutePath();
//            Log.d("imageWrite", "Path: " + pathStr);
//            File directory = new File(pathStr);
//            File[] files = directory.listFiles();
//            Log.d("imageWrite", "Size: "+ files.length);
//            for (int i = 0; i < files.length; i++)
//            {
//                Log.d("imageWrite", "FileName:" + files[i].getName());
//                Log.d("imageWrite", "FileName:" + files[i]);
//            }
        }
        else{
            Log.d("imageWrite", "FAILURE ---");
        }

        ContextWrapper team1CW = new ContextWrapper(getApplicationContext());
        File team1path = new File(team1CW.getDir("dank_memes", Context.MODE_PRIVATE), "team1.png");

        if(team1path.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(team1path.getAbsolutePath());

            Log.d("imageWrite", team1path.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.team1);

            myImage.setImageBitmap(myBitmap);
        }
        else{
            Log.d("imageWrite", "FAILURE ---");
        }

        ContextWrapper team2CW = new ContextWrapper(getApplicationContext());
        File team2path = new File(team2CW.getDir("dank_memes", Context.MODE_PRIVATE), "team2.png");

        if(team2path.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(team2path.getAbsolutePath());

            Log.d("imageWrite", team2path.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.team2);

            myImage.setImageBitmap(myBitmap);
        }
        else{
            Log.d("imageWrite", "FAILURE ---");
        }

        Button AcceptPhotoButon = (Button) findViewById(R.id.AcceptPhotoButon);

        AcceptPhotoButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent optionIntent = new Intent(getApplicationContext(), ColorSelection.class);
                startActivity(optionIntent);
            }
        });
    }
}
