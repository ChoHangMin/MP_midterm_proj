package com.example.mp_midterm_proj;

import android.util.Log;

public class TournamentTree {

    public TreeNode root;
    String [] playerNames;
    int i = 1;

    public static boolean isPowerOfTwo(int n) { // 예외처리를 위해 2의 거듭제곱인지 확인하는 메서드
        if (n <= 0) return false;
        return (n & (n - 1)) == 0; // 2의 거듭제곱의 경우, 이진수로 무조건 1이 하나 있는 값이기 때문에 해당 값보다 1작은 것과 & 연산을 취하면 무조건 0이 됨.
    }

    public TournamentTree(String [] playerNames) { // playerNames 배열을 받아옴. 해당 사이즈는 n = 2^a의 형태여야함.

        try {
            if (!isPowerOfTwo(playerNames.length)) {
                throw new IllegalArgumentException("playerNames의 배열의 크기가 2의 거듭제곱 형태가 아닙니다.");
            }
            this.playerNames = playerNames; // 인스턴스 변수에 저장, buildTournamentTree에서 사용하기 때문에.
            root = buildTree(1, playerNames.length);
        } catch (Exception e) {
            Log.e("TreeNode","TournamentTree parameter's form error", e);
        }

    }

    public TreeNode buildTree(int start, int end) { // 최하단 레벨만 값이 채워져 있는 완전 이진 트리, 토너먼트 대진표 만드는 recursive한 메서드
        if (start == end) {
            return new TreeNode(playerNames[start - 1]); // baseCase일 경우 TreeNode의 element에 playerName을 assign함.
        }
        int mid = (start + end) / 2; // 트리의 left, right subTree를 구하기 위해 중간 값을 구함.

        TreeNode leftSubTree = buildTree(start, mid); // leftSubTree로 분해
        TreeNode rightSubTree = buildTree(mid + 1, end); // rightSubTree로 분해
        TreeNode parentTree = new TreeNode("unknown_value " + i++); // 최하단 레벨의 노드가 아니라면 unknown_value를 할당, 구분을 위해 숫자를 붙임

        parentTree.left = leftSubTree; // SubTree의 값들을 잇는 부분, 부모 - 자식의 관계
        parentTree.right = rightSubTree;

        leftSubTree.parent = parentTree; // SelectNodeActivity에서 결과가 반환될 때 Tree의 상태를 업데이트 하기 위해 참조
        rightSubTree.parent = parentTree;

        return parentTree;
    }

    public int getHeight(TreeNode node) { // level를 찾기 위해 재귀적으로 트리의 height를 찾는 메서드. level = height + 1.
        if (node == null) return 0;

        int leftHeight = getHeight(node.left);
        int rightHeight = getHeight(node.right);

        return Math.max(leftHeight, rightHeight) + 1;

    }
}
