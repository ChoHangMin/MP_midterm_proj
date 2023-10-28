package com.example.mp_midterm_proj

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.LinkedList
import java.util.Queue
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

class StartTournamentActivity : AppCompatActivity() {

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    private var continuation: Continuation<Unit>? = null
    private lateinit var testTournament: TournamentTree

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_tournament) // StartTournamentActivity에 연결된 xml 레이아웃
        val playerNames = arrayOf("a", "b", "c", "d") // test할 임시 players array
        testTournament = TournamentTree(playerNames) // TournamentTree testTournament 생성

        val height =
            testTournament.getHeight(testTournament.root) // 최하단 레벨, player들이 들어갈 레벨을 구함.
        Log.d("MP_proj", "Get Max Level $height")

        coroutineScope.launch {
            for (i in height downTo 0) {
                bfsTraversalByLevel(testTournament.root, i).collect { receivedPairNode ->
                    sendPairNode(receivedPairNode)
                }
            }
            try {
                launchShowWinnerActivity()

            } catch (e : Exception) {
                Log.e("MP_proj", "Error Message LauchShowWinnerActivity", e)
            }

        }
    }

    private suspend fun bfsTraversalByLevel(
        root: TreeNode?,
        targetLevel: Int
    ): Flow<Array<TreeNode?>> = flow { // 원하는 레벨만 순회하여 pairNode를 SelectNodeActivity로 넘기도록 bfs순회를 하는 메서드
        if (root == null) return@flow // root가 없다면 return

        val queue: Queue<TreeNode> = LinkedList() // LinkedList로 Queue 생성. 순회를 위해 필요하다.
        var bfsLevel = 1 // 해당 메서드 내에서 값을 증가시키며 targetLevel를 찾아주는 변수

        queue.add(root) // queue 에 root값 추가
        Log.d("MP_proj", "Queue add root " + root.playerName)

        while (!queue.isEmpty()) {
            Log.d("MP_proj", "Set TreeNodeLevel $bfsLevel")
            val queueSize = queue.size
            val pairNode = arrayOfNulls<TreeNode>(2) // 사용자가 선택해야할, SelectNodeActivity로 넘겨야할 pairNode 배열

            if (bfsLevel == targetLevel) { // 만약 targetLevel과 level이 같을 경우
                Log.d("MP_proj", "check targetLevel $targetLevel")
                var j = 0 // pairNode index를 위한 변수
                for (i in 0 until queueSize) {
                    val current = queue.poll() // queue에서 값, 즉 객체 제거
                    pairNode[j] = current // 해당 객체를 pairNdoe 배열에 할당
                    Log.d("MP_proj", "check pairNode $i")
                    if (j == 1) { // 만약에 1일 경우, 즉 pairNode가 모두 찼을 경우
                        Log.d("MP_proj", "Full pairNode")
                        emit(pairNode.copyOf()) // pairNode를 내보냄
                        j = 0 // 다시 pairNode의 index 값 j를 초기화
                        Log.d("MP_proj", "Reset pairNode Index")
                    } else {
                        ++j // pairNode가 아직 다 안 채워졌다면 ++j
                    }
                }
            } else { // targetLevel이 아닌 경우에는 bfs 탐색만 진행
                for (i in 0 until queueSize) {
                    val current = queue.poll()
                    if (current.left != null) {
                        queue.add(current.left)
                        Log.d("MP_proj", "Queue add left node " + current.left.playerName)
                    }
                    if (current.right != null) {
                        queue.add(current.right)
                        Log.d("MP_proj", "Queue add right node " + current.right.playerName)
                    }
                }
            }
            ++bfsLevel
        }
    }


    private suspend fun sendPairNode(pairNode: Array<TreeNode?>) = suspendCancellableCoroutine<Unit> { // coroutine은 일시정지가 됨
            continuation ->
        this.continuation = continuation // coroutine의 일시정지 및 재개를 제어하는 객체
        val intent = Intent(this@StartTournamentActivity, SelectNodeActivity::class.java)

        Log.d("MP_proj", "Preparing to put parcelableExtra into Intent")
        intent.putExtra(
            "pairNode",
            pairNode
        ) // StartTournamentActivity -> SelectNodeActivity로 pariNode객체를 넘겨준다.
        Log.d("MP_proj", "Successfully put parcelableExtra into Intent")

        selectNodeActivityResultLauncher.launch(intent)

        continuation.invokeOnCancellation {
            // 코루틴이 취소될 때 필요한 작업을 여기에 추가 (예: 리스너 해제 등)
        }

    }

    private var selectNodeActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>( // 실제 Intent가 실행되고 해당 값을 받아결과를 넘겨줌
        ActivityResultContracts.StartActivityForResult() // 다른 액티비티를 시작하고 그 결과를 받아오겠다"는 약속(계약)입니다
    ) { result: ActivityResult ->
        handleActivityResult(result)
    }
    private fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            Log.d("MP_proj", "Result received with RESULT_OK")

            val data = result.data
            if (data == null) {
                Log.d("MP_proj", "Data is null")
                return
            }

            val selectedNode = data.extras?.getParcelable<TreeNode>("selectedNode") // 받아온 결과를 TreeNode의 형태로 변환
            if (selectedNode == null) {
                Log.d("MP_proj", "selectedNode is null")
                return
            }
            Log.d("MP_proj", "selectedNode " + selectedNode.playerName)

            updateParentNodeWithSelectedNode(testTournament.root, selectedNode.playerName)
            // selectedNode의 playerName 값을 가지는 노드를 찾아 그 노드의 부모 노드의 playerName 값을 업데이트

            continuation?.resume(Unit) // coroutine 재개

        } else {
            Log.d("MP_proj", "Result received with different resultCode: ${result.resultCode}")
        }
    }

    private fun updateParentNodeWithSelectedNode(tournamentRoot: TreeNode, selectedNodeName: String) { // bfs순회를 하며 해당 값이 있을 경우 부모노드 값을 업데이트
        val queue: Queue<TreeNode> = LinkedList()
        queue.add(tournamentRoot)

        while (queue.isNotEmpty()) {
            val currentNode = queue.poll()

            // 왼쪽 자식 노드 확인
            if (currentNode.left != null) {
                if (currentNode.left.playerName == selectedNodeName) {
                    currentNode.playerName = selectedNodeName
                    Log.d("MP_proj", "update successfully!")
                    return
                } else {
                    queue.add(currentNode.left)
                }
            }

            // 오른쪽 자식 노드 확인
            if (currentNode.right != null) {
                if (currentNode.right.playerName == selectedNodeName) {
                    currentNode.playerName = selectedNodeName
                    return
                } else {
                    queue.add(currentNode.right)
                }
            }
        }
    }

    private fun launchShowWinnerActivity() { // 우승자 결과창으로 전환되게 하는 메서드
        val intent = Intent(this@StartTournamentActivity, ShowWinnerActivity::class.java)

        Log.d("MP_proj", "Final Preparing to put parcelableExtra into Intent ")
        intent.putExtra(
            "TreeNode",
            testTournament.root
        ) // StartTournamentActivity -> SelectNodeActivity로 pariNode객체를 넘겨준다.
        Log.d("MP_proj", "Final Successfully put parcelableExtra into Intent")

        try {
            startActivity(intent)

        } catch (e : Exception) {
            Log.e("MP_proj", "Error startActivity", e)

        }

        Log.d("MP_proj", "Final process!")
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}