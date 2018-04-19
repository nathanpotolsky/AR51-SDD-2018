package com.example.nathan.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Button BeginButton = (Button)findViewById(R.id.BeginButton);
        BeginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent optionIntent = new Intent(getApplicationContext(), CheckersOptions1.class);
                startActivity(optionIntent);
            }
        });
        ImageView wavyView = (ImageView)findViewById(R.id.wavyID);
        Glide.with(this).load(R.drawable.wavy).into(wavyView);

    }
}
