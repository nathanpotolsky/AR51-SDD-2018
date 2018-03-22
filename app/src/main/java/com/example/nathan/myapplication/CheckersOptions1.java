package com.example.nathan.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CheckersOptions1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkers_options1);

//        Button backButton = (Button)findViewById(R.id.backButton);
//        backButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent optionIntent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(optionIntent);
//            }
//        });

//        Button forwardButton = (Button)findViewById(R.id.forwardButton);
//        forwardButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
////                Intent optionIntent = new Intent(getApplicationContext(), MainActivity.class);
////                startActivity(optionIntent);
//                if(checkCameraHardware(getApplicationContext())){
//                    Toast.makeText(CheckersOptions1.this,"You have a camera",Toast.LENGTH_LONG).show();
//                    getCameraInstance();
//                }
//                else{
//                    Toast.makeText(CheckersOptions1.this,"You do not have a camera",Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        Button forwardButton = (Button)findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent optionIntent = new Intent(getApplicationContext(), CameraFillerPage.class);
                startActivity(optionIntent);
            }
        });
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
