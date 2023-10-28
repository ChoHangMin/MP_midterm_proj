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
    var pairNode = arrayOfNulls<TreeNode>(2) // 사용자가 선택해야할, SelectNodeActivity로 넘겨야할 pairNode 배열

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)
    private var continuation: Continuation<Unit>? = null
    private lateinit var testTournament: TournamentTree

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_tournament) // StartTournamentActivity에 연결된 xml 레이아웃
        val playerNames = arrayOf("a", "b", "c", "d") // test할 players array
        testTournament = TournamentTree(playerNames) // TournamentTree testTournament 생성

        val maxLevel =
            testTournament.getHeight(testTournament.root) // 최하단 레벨, player들이 들어갈 레벨을 구함. level = height + 1
        Log.d("MP_proj", "Get Max Level $maxLevel")

        coroutineScope.launch {
            for (i in maxLevel downTo 0) {
                bfsTraversalByLevel(testTournament.root, i).collect { receivedPairNode ->
                    sendPairNode(receivedPairNode)
                }

            }




        }



    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun launchShowWinnerActivity() {
        val intent = Intent(this@StartTournamentActivity, ShowWinnerActivity::class.java)

        Log.d("MP_proj", "Final Preparing to put parcelableExtra into Intent ")
        intent.putExtra(
            "TreeNode",
            testTournament.root
        ) // StartTournamentActivity -> SelectNodeActivity로 pariNode객체를 넘겨준다.
        Log.d("MP_proj", "Final Successfully put parcelableExtra into Intent")
        startActivity(intent)
        Log.d("MP_proj", "Final process!")
    }

    fun updateParentNodeWithSelectedNode(tournamentRoot: TreeNode, selectedNodeName: String) {
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


    var selectNodeActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
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

            val selectedNode = data.extras?.getParcelable<TreeNode>("selectedNode")
            if (selectedNode == null) {
                Log.d("MP_proj", "selectedNode is null")
                return
            }
            Log.d("MP_proj", "selectedNode " + selectedNode.playerName)

            // selectedNode의 playerName 값을 가지는 노드를 찾아 그 노드의 부모 노드의 playerName 값을 업데이트
            updateParentNodeWithSelectedNode(testTournament.root, selectedNode.playerName)





            continuation?.resume(Unit) // 코루틴 재개


        } else {
            Log.d("MP_proj", "Result received with different resultCode: ${result.resultCode}")
        }
    }



    private suspend fun sendPairNode(pairNode: Array<TreeNode?>) = suspendCancellableCoroutine<Unit> {
            continuation ->
        this.continuation = continuation
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



    fun bfsTraversal(root: TreeNode?) {
        if (root == null) return
        val queue: Queue<TreeNode> = LinkedList()
        queue.add(root)
        while (!queue.isEmpty()) {
            val current = queue.poll()
            Log.d("MP_proj", "Final TreeNode value " + current.playerName)
            if (current.left != null) {
                queue.add(current.left)
            }
            if (current.right != null) {
                queue.add(current.right)
            }
        }
    }

    suspend fun bfsTraversalByLevel(
        root: TreeNode?,
        targetLevel: Int
    ): Flow<Array<TreeNode?>> = flow { // 원하는 레벨만 순회하여 pairNode를 SelectNodeActivity로 넘기도록 bfs순회를 하는 메서드
        if (root == null) return@flow // root가 없다면 return
        var TreeNodeLevel = 1 // 값을 증가시키며 targetLevel를 찾아주는 변수
        val queue: Queue<TreeNode> = LinkedList() // LinkedList로 Queue 생성. 순회를 위해 필요하다.
        queue.add(root) // queue 에 root값 추가
        Log.d("MP_proj", "Queue add root " + root.playerName)

            while (!queue.isEmpty()) {
                Log.d("MP_proj", "Set TreeNodeLevel $TreeNodeLevel")
                val levelSize = queue.size

                val pairNode = arrayOfNulls<TreeNode>(2)

                if (TreeNodeLevel == targetLevel) { // 만약 targetLevel과 level이 같을 경우
                    Log.d("MP_proj", "check targetLevel $targetLevel")
                    var j = 0 // pairNode index를 위한 변수
                    for (i in 0 until levelSize) {
                        val current = queue.poll() // queue에서 값 빼내기
                        pairNode[j] = current // 해당 값을 배열에 할당
                        Log.d("MP_proj", "check pairNode $i")
                        if (j == 1) { // 만약에 1일 경우, 즉 pairNode가 모두 찼을 경우
                            Log.d("MP_proj", "Full pairNode")
                            emit(pairNode.copyOf())

                            j = 0 // 다시 index 값을 초기화
                            Log.d("MP_proj", "Reset pairNode Index")
                        } else {
                            ++j // 만일 아직 j = 0이라면, pairNode가 전부 안채워졌다면 ++j
                        }
                    }
                } else {
                    for (i in 0 until levelSize) {
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
                ++TreeNodeLevel
            }



    }

}