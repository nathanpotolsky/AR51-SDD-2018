package com.example.nathan.myapplication;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class KingTagging extends AppCompatActivity {

    private static CheckerBoard checkerBoard = ColorSelection.getCheckerBoard();

    public static CheckerBoard getCheckerBoard() {return checkerBoard;}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_king_tagging);

        ContextWrapper normalizedCW = new ContextWrapper(getApplicationContext());
        File normalizedPath = new File(normalizedCW.getDir("dank_memes", Context.MODE_PRIVATE), "normalizedCheckerboard.png");

        if(normalizedPath.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(normalizedPath.getAbsolutePath());

            Log.d("imageWrite", normalizedPath.getAbsolutePath());

            ImageView myImage = (ImageView) findViewById(R.id.normalizedView);

            myImage.setImageBitmap(myBitmap);
        }
        else{
            Log.d("imageWrite", "FAILURE ---");
        }

        Button forwardButton = (Button)findViewById(R.id.forwardButton);
        forwardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent optionIntent = new Intent(getApplicationContext(), ARPage.class);
                startActivity(optionIntent);
            }
        });

        final Button King11 = findViewById(R.id.king11);
        final Button King12 = findViewById(R.id.king12);
        final Button King13 = findViewById(R.id.king13);
        final Button King14 = findViewById(R.id.king14);
        final Button King15 = findViewById(R.id.king15);
        final Button King16 = findViewById(R.id.king16);
        final Button King17 = findViewById(R.id.king17);
        final Button King18 = findViewById(R.id.king18);

        final Button King21 = findViewById(R.id.king21);
        final Button King22 = findViewById(R.id.king22);
        final Button King23 = findViewById(R.id.king23);
        final Button King24 = findViewById(R.id.king24);
        final Button King25 = findViewById(R.id.king25);
        final Button King26 = findViewById(R.id.king26);
        final Button King27 = findViewById(R.id.king27);
        final Button King28 = findViewById(R.id.king28);

        final Button King31 = findViewById(R.id.king31);
        final Button King32 = findViewById(R.id.king32);
        final Button King33 = findViewById(R.id.king33);
        final Button King34 = findViewById(R.id.king34);
        final Button King35 = findViewById(R.id.king35);
        final Button King36 = findViewById(R.id.king36);
        final Button King37 = findViewById(R.id.king37);
        final Button King38 = findViewById(R.id.king38);

        final Button King41 = findViewById(R.id.king41);
        final Button King42 = findViewById(R.id.king42);
        final Button King43 = findViewById(R.id.king43);
        final Button King44 = findViewById(R.id.king44);
        final Button King45 = findViewById(R.id.king45);
        final Button King46 = findViewById(R.id.king46);
        final Button King47 = findViewById(R.id.king47);
        final Button King48 = findViewById(R.id.king48);

        final Button King51 = findViewById(R.id.king51);
        final Button King52 = findViewById(R.id.king52);
        final Button King53 = findViewById(R.id.king53);
        final Button King54 = findViewById(R.id.king54);
        final Button King55 = findViewById(R.id.king55);
        final Button King56 = findViewById(R.id.king56);
        final Button King57 = findViewById(R.id.king57);
        final Button King58 = findViewById(R.id.king58);

        final Button King61 = findViewById(R.id.king61);
        final Button King62 = findViewById(R.id.king62);
        final Button King63 = findViewById(R.id.king63);
        final Button King64 = findViewById(R.id.king64);
        final Button King65 = findViewById(R.id.king65);
        final Button King66 = findViewById(R.id.king66);
        final Button King67 = findViewById(R.id.king67);
        final Button King68 = findViewById(R.id.king68);

        final Button King71 = findViewById(R.id.king71);
        final Button King72 = findViewById(R.id.king72);
        final Button King73 = findViewById(R.id.king73);
        final Button King74 = findViewById(R.id.king74);
        final Button King75 = findViewById(R.id.king75);
        final Button King76 = findViewById(R.id.king76);
        final Button King77 = findViewById(R.id.king77);
        final Button King78 = findViewById(R.id.king78);

        final Button King81 = findViewById(R.id.king81);
        final Button King82 = findViewById(R.id.king82);
        final Button King83 = findViewById(R.id.king83);
        final Button King84 = findViewById(R.id.king84);
        final Button King85 = findViewById(R.id.king85);
        final Button King86 = findViewById(R.id.king86);
        final Button King87 = findViewById(R.id.king87);
        final Button King88 = findViewById(R.id.king88);

        King11.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King11);}});
        King12.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King12);}});
        King13.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King13);}});
        King14.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King14);}});
        King15.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King15);}});
        King16.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King16);}});
        King17.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King17);}});
        King18.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King18);}});

        King21.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King21);}});
        King22.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King22);}});
        King23.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King23);}});
        King24.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King24);}});
        King25.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King25);}});
        King26.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King26);}});
        King27.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King27);}});
        King28.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King28);}});

        King31.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King31);}});
        King32.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King32);}});
        King33.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King33);}});
        King34.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King34);}});
        King35.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King35);}});
        King36.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King36);}});
        King37.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King37);}});
        King38.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King38);}});

        King41.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King41);}});
        King42.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King42);}});
        King43.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King43);}});
        King44.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King44);}});
        King45.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King45);}});
        King46.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King46);}});
        King47.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King47);}});
        King48.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King48);}});

        King51.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King51);}});
        King52.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King52);}});
        King53.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King53);}});
        King54.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King54);}});
        King55.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King55);}});
        King56.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King56);}});
        King57.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King57);}});
        King58.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King58);}});

        King61.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King61);}});
        King62.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King62);}});
        King63.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King63);}});
        King64.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King64);}});
        King65.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King65);}});
        King66.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King66);}});
        King67.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King67);}});
        King68.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King68);}});

        King71.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King71);}});
        King72.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King72);}});
        King73.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King73);}});
        King74.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King74);}});
        King75.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King75);}});
        King76.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King76);}});
        King77.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King77);}});
        King78.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King78);}});

        King81.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King81);}});
        King82.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King82);}});
        King83.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King83);}});
        King84.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King84);}});
        King85.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King85);}});
        King86.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King86);}});
        King87.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King87);}});
        King88.setOnClickListener(new View.OnClickListener(){public void onClick(View v){selectKing(King88);}});


    }

    public void selectKing(Button currentSquare){
        if(currentSquare.isSelected()){
            currentSquare.setBackgroundColor(Color.TRANSPARENT);
            currentSquare.setSelected(false);
        }
        else{
            currentSquare.setBackgroundResource(R.drawable.crown);
            currentSquare.setSelected(true);
            if(currentSquare.getTag() != null){
                String name = currentSquare.getTag().toString();
                Log.d("myTag2", "button tag " + name);
                int y = Integer.parseInt(name.substring(name.length()-1));
                int x = Integer.parseInt(name.substring(name.length()-2, name.length()-1));
                checkerBoard.setKing(x, y);
            } else {
                Log.d("myTag2", String.valueOf(currentSquare.getId()));
            }
        }
    }

}
