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

    private static boolean showAllMoves = false;
    private static boolean rankAllMoves = false;
    private static boolean instantTakes = false;
    private static boolean instantLosses = false;
    private static boolean bestMove = false;
    private static boolean worstMove = false;

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
                Intent optionIntent = new Intent(getApplicationContext(), ARPage.class);
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
