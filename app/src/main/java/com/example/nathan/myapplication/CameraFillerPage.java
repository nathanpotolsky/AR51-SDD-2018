package com.example.nathan.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CameraFillerPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_filler_page);

        Button forwardButton = (Button)findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent optionIntent = new Intent(getApplicationContext(), ColorSelection.class);
                startActivity(optionIntent);
            }
        });
    }
}
