package com.threlease.base.utils.blockchains;

import com.threlease.base.utils.Hash;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree {
    // 잎 노드의 해시 값을 저장하는 리스트
    List<String> merkleLeaves;

    // 생성자에서 트랜잭션의 리스트를 받아서 잎 노드의 해시 값을 계산합니다.
    public MerkleTree(List<String> transactionList) {
        merkleLeaves = new ArrayList<>();
        for (String tx : transactionList) {
            merkleLeaves.add(new Hash().generateSHA256(tx));
        }
    }

    // Merkle Root를 계산합니다.
    public String getMerkleRoot() {
        List<String> tempTreeLayer = merkleLeaves;
        while(tempTreeLayer.size() > 1) {
            ArrayList<String> newLayer = new ArrayList<>();
            for (int i=0; i < tempTreeLayer.size(); i+=2) {
                // 마지막 해시가 홀수개인 경우 복제하여 추가
                if (i == tempTreeLayer.size() - 1) {
                    tempTreeLayer.add(tempTreeLayer.get(tempTreeLayer.size() - 1));
                }
                newLayer.add(new Hash().generateSHA256(tempTreeLayer.get(i) + tempTreeLayer.get(i+1)));
            }
            tempTreeLayer = newLayer;
        }
        return tempTreeLayer.size() == 1 ? tempTreeLayer.get(0) : "";
    }

    /*
    * public static void main(String[] args) {
        List<String> txList = Arrays.asList("tx1", "tx2", "tx3", "tx4"); // 트랜잭션 목록
        MerkleTree merkleTree = new MerkleTree(txList);
        String merkleRoot = merkleTree.getMerkleRoot();
        System.out.println("Merkle Root: " + merkleRoot);
    * }
    * */
}
