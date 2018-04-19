package com.example.nathan.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class ARPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("myTag", "111");
        CheckerBoard checkerBoard = BoardDetectionActivity.getCheckerBoard();
        checkerBoard.printBoard();
        checkerBoard.addPiecesToLists();
        checkerBoard.findValidMoves(true);
        checkerBoard.findValidMoves(false);
//        checkerBoard.printAllPieces();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arpage);

        TextView boardOutputArea = (TextView) findViewById(R.id.boardOutputArea);
        boardOutputArea.setText("");
        for(int i = 0; i < checkerBoard.printBoard().size(); i++){
            boardOutputArea.setText(boardOutputArea.getText() + checkerBoard.printBoard().get(i) + "\n");
        }

        TextView outputArea = (TextView) findViewById(R.id.outputArea);
        outputArea.setMovementMethod(new ScrollingMovementMethod());
        outputArea.setText("");
        ArrayList<String> allPieces = checkerBoard.getAllPieces();
        outputArea.setText(outputArea.getText() + "-----All Pieces on Board-----\n");
        for(int i = 0; i < checkerBoard.getAllPieces().size(); i++){
            outputArea.setText(outputArea.getText() + checkerBoard.getAllPieces().get(i) + "\n");
        }
        outputArea.setText(outputArea.getText() + "\n");

        if(CheckersOptions1.showAllMoves()){
            outputArea.setText(outputArea.getText() + "-----All Possible Moves-----\n");
            ArrayList<String> ourMoves = checkerBoard.getAllMoves(true);
            for(int i = 0; i < ourMoves.size(); i++){
                outputArea.setText(outputArea.getText() + ourMoves.get(i) + "\n");
            }
            outputArea.setText(outputArea.getText() + "\n");

            ArrayList<String> theirMoves = checkerBoard.getAllMoves(false);
            for(int i = 0; i < theirMoves.size(); i++){
                outputArea.setText(outputArea.getText() + theirMoves.get(i) + "\n");
            }
            outputArea.setText(outputArea.getText() + "\n");
        }

        if(CheckersOptions1.instantTakes()){
            outputArea.setText(outputArea.getText() + "-----Instant Takes-----\n");
            ArrayList<CheckersMove> instantTakes = checkerBoard.instantTakes();
            for(int i = 0; i < instantTakes.size(); i++){
                outputArea.setText(outputArea.getText() + instantTakes.get(i).getInstantTakeMessage() + "\n");
            }
            outputArea.setText(outputArea.getText() + "\n");
        }

        if(CheckersOptions1.instantLosses()){
            outputArea.setText(outputArea.getText() + "-----Instant Losses-----\n");
            ArrayList<CheckersMove> instantLosses = checkerBoard.instantLosses();
            for(int i = 0; i < instantLosses.size(); i++){
                outputArea.setText(outputArea.getText() + instantLosses.get(i).getInstantLossMessage() + "\n");
            }
            outputArea.setText(outputArea.getText() + "\n");
        }

        if(CheckersOptions1.bestMove()){
            outputArea.setText(outputArea.getText() + "-----Best Move-----\n");
            CheckersMove bestMove = checkerBoard.bestMove();
            outputArea.setText(outputArea.getText() + bestMove.getBestMoveMessage() + "\n");
            outputArea.setText(outputArea.getText() + "\n");
        }

        if(CheckersOptions1.worstMove()){
            outputArea.setText(outputArea.getText() + "-----Worst Move-----\n");
            CheckersMove worstMove = checkerBoard.worstMove();
            outputArea.setText(outputArea.getText() + worstMove.getBestMoveMessage() + "\n");
            outputArea.setText(outputArea.getText() + "\n");
        }

        if(CheckersOptions1.rankAllMoves()){
            outputArea.setText(outputArea.getText() + "-----Ranked Move-----\n");
            ArrayList<CheckersMove> rankedMoves = checkerBoard.rankedMoves();
            for(int i = 0; i < rankedMoves.size(); i++){
                outputArea.setText(outputArea.getText() + String.valueOf(i+1) + "| " + rankedMoves.get(i).getRankedMessage() + "\n");
            }
            outputArea.setText(outputArea.getText() + "\n");
        }

    }
}
