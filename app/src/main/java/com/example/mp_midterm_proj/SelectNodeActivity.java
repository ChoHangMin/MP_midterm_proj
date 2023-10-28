package com.example.mp_midterm_proj;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Semaphore;

public class SelectNodeActivity extends AppCompatActivity {

    Button buttonNode1, buttonNode2; // 버튼 두개 생성
    TreeNode[] nodes; // 여기에 추가




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_node);

        Log.d("MP_proj", "SelectNodeActivity onCreate started.");

        buttonNode1 = findViewById(R.id.buttonNode1); // 버튼 id 연결
        buttonNode2 = findViewById(R.id.buttonNode2);

        Intent intent = getIntent(); // StartTournament로 부터 intent를 받아옴



        try {
            Log.d("MP_proj", "Trying to get parcelableExtra from Intent");

            Parcelable[] parcelables = intent.getParcelableArrayExtra("pairNode");
            if (parcelables != null) {
                nodes = new TreeNode[parcelables.length];
                for (int i = 0; i < parcelables.length; i++) {
                    nodes[i] = (TreeNode) parcelables[i];
                }

                if(nodes != null && nodes.length > 0) {
                    Log.d("MP_proj", "TreeNode array received successfully");
                    Log.d("MP_proj", "Received Pair node " + nodes[0].playerName + nodes[1].playerName);

                    buttonNode1.setText(nodes[0].playerName);
                    buttonNode2.setText(nodes[1].playerName);

                } else {
                    Log.e("MP_proj", "Failed to receive TreeNode array");
                }
            } else {
                Log.e("MP_proj", "Parcelable array is null");
            }

        } catch (Exception e) {
            Log.e("MP_proj", "Error while getting parcelableExtra: ", e);
        }



        buttonNode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedNode", nodes[0]); // 0을 선택했을 경우 해당 결과를 다시 StartTournamentActivity에 반환
                Log.d("MP_proj", "nodes " + nodes[0].playerName);

                Log.d("MP_proj", "Returning result with selectedNode 0.");

                // 결과를 설정하고 Activity를 종료
                setResult(RESULT_OK, resultIntent);
                finish();



            }
        });

        buttonNode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedNode", nodes[1]);
                Log.d("MP_proj", "nodes " + nodes[1].playerName);

                Log.d("MP_proj", "Returning result with selectedNode 1.");

                // 결과를 설정하고 Activity를 종료
                setResult(RESULT_OK, resultIntent);
                finish();

            }

        });
    }


}
