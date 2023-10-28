package com.example.mp_midterm_proj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class StartTournamentActivity extends AppCompatActivity {
    private final Semaphore semaphore = new Semaphore(0); // 임계영역의 순차처리를 위한 구문

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_tournament); // StartTournamentActivity에 연결된 xml 레이아웃

        String [] playerNames = {"a", "b", "c", "d"}; // test할 players array
        TournamentTree testTournament = new TournamentTree(playerNames); // TournamentTree testTournament 생성

        int maxLevel = testTournament.getHeight(testTournament.root); // 최하단 레벨, player들이 들어갈 레벨을 구함. level = height + 1

        for (int i = maxLevel; i >= 0; --i) {
            bfsTraversalByLevel(testTournament.root,i); //최하단 레벨부터 따라 bfs 순회를 시작한다.
            Log.d("MP_proj", "bfsTraversal level " + i);
        }
    }
    private final ActivityResultLauncher<Intent> mNodePairResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d("MP_proj", "start mNodePairResultLauncher method.");
                    Intent data = result.getData(); // SelectNodeAcitivity로 부터 Intent를 받아온다.
                    TreeNode selectedNode = data.getParcelableExtra("selectedNode");
                    selectedNode.parent.playerName = selectedNode.playerName; // 선택된 부모노드를 선택된 값으로 채워넣는다.
                    semaphore.release(); // bfsTraversalByLevel의 반복문을 일시정지 했다가 SelectNodeActivity로부터 Intent를 성공적으로 받으면 다시 재개한다.
                }
            }
    );
    public void bfsTraversalByLevel(TreeNode root, int targetLevel) { // 원하는 레벨만 순회하여 pairNode를 SelectNodeActivity로 넘기도록 bfs순회를 하는 메서드
        if (root == null) return; // root가 없다면 return

        int TreeNodeLevel = 0; // 값을 증가시키며 targetLevel를 찾아주는 변수
        Queue<TreeNode> queue = new LinkedList<>(); // LinkedList로 Queue 생성. 순회를 위해 필요하다.
        queue.add(root); // queue 에 root값 추가

        while(!queue.isEmpty()) {
            int levelSize = queue.size();
            TreeNode [] pairNode = new TreeNode[2]; // 사용자가 선택해야할, SelectNodeActivity로 넘겨야할 pairNode 배열


            if (TreeNodeLevel == targetLevel) { // 만약 targetLevel과 level이 같을 경우
                Log.d("MP_proj", "check targetLevel" + targetLevel);

                int j = 0; // pairNode index를 위한 변수
                for (int i = 0; i < levelSize; ++i) {
                    TreeNode current = queue.poll(); // queue에서 값 빼내기
                    pairNode[j] = current; // 해당 값을 배열에 할당
                    Log.d("MP_proj", "check pairNode" + i);

                    if (j == 1) { // 만약에 1일 경우, 즉 pairNode가 모두 찼을 경우
                        Log.d("MP_proj", "full pairNode");
                        Intent intent = new Intent(StartTournamentActivity.this, SelectNodeActivity.class);
                        intent.putExtra("pairNode", pairNode); // StartTournamentActivity -> SelectNodeActivity로 pariNode객체를 넘겨준다.
                        Log.d("MP_proj", "from startTournament to selectNodeActivity");
                        mNodePairResultLauncher.launch(intent); // 선택된 결과 값을 받아오는 함수.

                        try {
                            semaphore.acquire(); // This will block until release() is called
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        j = 0; // 다시 index 값을 초기화

                    } else {
                        ++j; // 만일 아직 j = 0이라면, pairNode가 전부 안채워졌다면 ++j
                    }

                }

            } else {
                for (int i = 0; i < levelSize; ++i) {
                    TreeNode current = queue.poll();

                    if (current.left != null) {
                        queue.add(current.left);
                    }
                    if (current.right != null) {
                        queue.add(current.right);
                    }
                }
            }
            ++TreeNodeLevel;

        }


    }

}
