package com.example.nathan.myapplication;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

public class PhotoPreview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

//        File imgFile = new  File("/sdcard/Images/test_image.jpg");
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File path = cw.getDir("dank_memes", Context.MODE_PRIVATE);

        if(path.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(path.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.photoPreview);

            myImage.setImageBitmap(myBitmap);
            Log.d("imageWrite", "SUCCESS ---");

            String pathStr = path.getAbsolutePath();
            Log.d("imageWrite", "Path: " + pathStr);
            File directory = new File(pathStr);
            File[] files = directory.listFiles();
            Log.d("imageWrite", "Size: "+ files.length);
            for (int i = 0; i < files.length; i++)
            {
                Log.d("imageWrite", "FileName:" + files[i].getName());
            }
        }
        else{
            Log.d("imageWrite", "FAILURE ---");
        }
    }
}
