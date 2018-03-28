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

public class MainActivity extends AppCompatActivity {
    //private Camera mCamera = null;
    //private CheckersCamera mCameraView = null;


    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // Example of a call to a native method
//        TextView tv = findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());

        Button checkersButton = (Button)findViewById(R.id.checkersButton);
        checkersButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent optionIntent = new Intent(getApplicationContext(), CheckersOptions1.class);
                startActivity(optionIntent);
            }
        });

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
                tempCheckerBoard.printAllPieces();
                tempCheckerBoard.printAllMoves();
            }
        });
    }

    public void open(View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Which do you prefer?");

        alertDialogBuilder.setPositiveButton("IOS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MainActivity.this,"You selected IOS",Toast.LENGTH_LONG).show();
            }
        });

        alertDialogBuilder.setNeutralButton("Leave App", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("Android",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MainActivity.this,"You selected Android",Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
}
