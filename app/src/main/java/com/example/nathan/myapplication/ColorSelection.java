package com.example.nathan.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ColorSelection extends AppCompatActivity {

    boolean on = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_selection);

        Button forwardButton = (Button)findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent optionIntent = new Intent(getApplicationContext(), KingTagging.class);
                startActivity(optionIntent);
            }
        });

        final Button firstColorButton = (Button) findViewById(R.id.firstColorPieceCheckMark);
        final Button secondColorButton = (Button)findViewById(R.id.secondColorPieceCheckMark);
        firstColorButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!on) {
                    PhotoPreview.getCheckerBoard().switchTeams();
                }
                on = true;
                firstColorButton.setBackgroundResource(R.drawable.greencheck);
                secondColorButton.setBackgroundColor(Color.TRANSPARENT);


            }
        });
        secondColorButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (on) {
                    PhotoPreview.getCheckerBoard().switchTeams();
                }
                on = false;
                firstColorButton.setBackgroundColor(Color.TRANSPARENT);
                secondColorButton.setBackgroundResource(R.drawable.greencheck);
            }
        });
    }
}
