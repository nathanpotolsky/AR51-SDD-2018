package com.example.nathan.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ARPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("myTag", "111");
        CheckerBoard checkerBoard = PhotoPreview.getCheckerBoard();
        checkerBoard.printBoard();
        checkerBoard.addPiecesToLists();
        checkerBoard.printAllPieces();
        Log.d("myTag", String.valueOf(checkerBoard.rows));
        Log.d("myTag", "222");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arpage);
    }
}
