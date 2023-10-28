package com.example.mp_midterm_proj;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SelectNodeActivity extends AppCompatActivity {

    Button buttonNode1, buttonNode2; // 버튼 두개 생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_node);

        buttonNode1 = findViewById(R.id.buttonNode1); // 버튼 id 연결
        buttonNode2 = findViewById(R.id.buttonNode2);

        Intent intent = getIntent(); // StartTournament로 부터 intent를 받아옴
        TreeNode[] parcelableExtra = (TreeNode[]) intent.getParcelableArrayExtra("pairNode");

        Log.d("MP_proj", "SelectNodeActivity onCreate started.");

        if(parcelableExtra != null) {
            Log.d("MP_proj", "TreeNode array received successfully");
        } else {
            Log.e("MP_proj", "Failed to receive TreeNode array");
        }

        buttonNode1.setText(parcelableExtra[0].playerName); // 받아온 값을 buttonNode1 값으로 설정
        buttonNode2.setText(parcelableExtra[1].playerName);

        buttonNode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedNode", parcelableExtra[0]); // 0을 선택했을 경우 해당 결과를 다시 StartTournamentActivity에 반환

                Log.d("MP_proj", "Returning result with selectedNode 1.");

                // 결과를 설정하고 Activity를 종료
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });

        buttonNode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedNode", parcelableExtra[1]);

                Log.d("MP_proj", "Returning result with selectedNode 1.");

                // 결과를 설정하고 Activity를 종료
                setResult(RESULT_OK, resultIntent);
                finish();

            }

        });
    }


}
