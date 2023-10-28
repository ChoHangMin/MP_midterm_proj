package com.example.mp_midterm_proj;

public class TournamentTree {

    public TreeNode root;
    String [] playerNames;
    int i = 1;

    public TournamentTree(String [] playerNames) { // playerNames 배열을 받아옴. 해당 사이즈는 n = 2^a의 형태여야함.
        this.playerNames = playerNames;
        root = buildTree(1, playerNames.length);
    }

    public TreeNode buildTree(int start, int end) { // 최하단 레벨만 값이 채워져 있는 완전 이진 트리, 토너먼트를 만드는 recursive한 메서드
        if (start == end) {
            return new TreeNode(playerNames[start - 1]); // baseCase, 일 경우 TreeNode의 element에 playerName 값을 assign함.
        }


        int mid = (start + end) / 2; // 트리를 분해해서 baseCase로 만들기 위한 식.

        TreeNode leftSubTree = buildTree(start, mid); // leftSubTree로 분해
        TreeNode rightSubTree = buildTree(mid + 1, end); // rightSubTree로 분해
        TreeNode parentTree = new TreeNode("unknown_value " + i++); // base Case를 제외한 경우 모든 노드의 값은 unknown_value로 할당

        parentTree.left = leftSubTree; // 트리를 생성하는 구문
        parentTree.right = rightSubTree;

        leftSubTree.parent = parentTree; // StartTournamentActivity -> SelectNodeActivity -> StartTournamentActivity로 값을 반환할 때, parent의 값을 바꾸기 위해 필요함.
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
