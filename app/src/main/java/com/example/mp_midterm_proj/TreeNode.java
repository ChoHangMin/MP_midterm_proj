package com.example.mp_midterm_proj;

import android.os.Parcel;
import android.os.Parcelable; // 정의한 복합 데이터를 액티비티 간 전송하기 위해 필요한 라이브러리
import android.util.Log;

public class TreeNode implements Parcelable { // TreeNode, playerName, left, right, parent의 정보를 가지고 있는 노드
    String playerName;
    TreeNode left;
    TreeNode right;
    TreeNode parent;

    public TreeNode(String playerName) { // 노드의 값을 assign 하는 생성자
        this.playerName = playerName;
        Log.d("MP_proj", "Create TreeNode " + playerName);
    }

    protected TreeNode(Parcel in) { // 이 아래는 Intent를 통해 TreeNode 객체를 넘길 수 있게 해주는 구문. 크게 중요치 않음
        playerName = in.readString();
        left = in.readParcelable(TreeNode.class.getClassLoader());
        right = in.readParcelable(TreeNode.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(playerName);
        dest.writeParcelable(left, flags);
        dest.writeParcelable(right, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TreeNode> CREATOR = new Creator<TreeNode>() {
        @Override
        public TreeNode createFromParcel(Parcel in) {
            return new TreeNode(in);
        }

        @Override
        public TreeNode[] newArray(int size) {
            return new TreeNode[size];
        }
    };
}
