package com.example.mp_midterm_proj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;


public class ShowWinnerActivity extends AppCompatActivity {

    Button buttonNode3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_winner);

        Log.d("MP_proj", "Open Show Winner Activity");

        buttonNode3 = findViewById(R.id.buttonNode3);

        Intent intent = getIntent();
        TreeNode receivedNode = intent.getParcelableExtra("TreeNode");
        Log.d("MP_proj", "Success Intent Open");

        if (receivedNode != null) {
            buttonNode3.setText(receivedNode.playerName);
        } else {
            buttonNode3.setText("결승 불러오기 실패");
        }



    }


}