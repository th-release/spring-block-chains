package com.threlease.base.utils.blockchains;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threlease.base.utils.Failable;
import com.threlease.base.utils.Hash;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class Block extends BlockHeader {
    public String merkleRoot;
    public String hash;
    public long nonce;
    public long difficulty;
    public List<String> data;

    public Block(Block _prevBlock, List<String> _data, Block _adjustmentBlock) {
        super(_prevBlock);
        if (_prevBlock == null) {
            this.height = 0;
            this.previousHash = "0".repeat(64);
        }
        this.merkleRoot = getMerkleRoot(_data);
        this.hash = createBlockHash(this);
        this.nonce = 0;
        this.difficulty = Block.getDifficulty(
                this,
                _adjustmentBlock,
                _prevBlock
        );
        this.data = _data;
    }

    public static Block getGenesis() {
        return Config.GENESIS;
    }

    public static String getMerkleRoot(List<String> data) {
        MerkleTree merkleTree = new MerkleTree(data);
        return merkleTree.getMerkleRoot();
    }

    public static String createBlockHash(Block _block) {
        String values = _block.version + _block.timestamp + _block.height + _block.merkleRoot + _block.previousHash + _block.difficulty + _block.nonce;
        return new Hash().generateSHA256(values);
    }

    public static Block generateBlock(Block _prevBlock, List<String> _data, Block _adjustmentBlock) {
        Block generateBlock =  new Block(_prevBlock, _data, _adjustmentBlock);
        return Block.findBlock(generateBlock);
    }

    public static Block findBlock(Block _generateBlock) {
        String hash;
        long nonce = 0;

        while (true) {
            nonce++;
            _generateBlock.nonce = nonce;
            hash = Block.createBlockHash(_generateBlock);

            String binary = new Hash().hexToBinary(hash);

            boolean result = binary.startsWith(
                    "0".repeat((int) _generateBlock.difficulty)
            );

            if (result) {
                _generateBlock.hash = hash;
                return _generateBlock;
            }
        }
    }

    public static long getDifficulty(
            Block _newBlock,
            Block _adjustmentBlock,
            Block _previousBlock
    ) {
        if (_newBlock.height <= 9) return 0;
        if (_newBlock.height <= 19) return 1;

        if (_newBlock.height % Config.DIFFICULTY_ADJUSTMENT_INTERVAL != 0)
            return _previousBlock.difficulty;

        long timeTaken = _newBlock.timestamp - _adjustmentBlock.timestamp;
        long timeExpected =
                Config.BLOCK_GENERATION_TIME_UNIT *
                Config.BLOCK_GENERATION_INTERVAL *
                Config.DIFFICULTY_ADJUSTMENT_INTERVAL;

        if (timeTaken < timeExpected / 2) return _adjustmentBlock.difficulty + 1;
        else if (timeTaken > timeExpected * 2)
            return _adjustmentBlock.difficulty - 1;

        return _adjustmentBlock.difficulty;
    }

    public static Failable<Block, String> isValidNewBlock(
            Block _newBlock,
            Block _prevBlock
    ) {
        if (_prevBlock.height + 1 != _newBlock.height) {
            return Failable.error("height error");
        }
        if (!Objects.equals(_prevBlock.hash, _newBlock.previousHash)) {
            return Failable.error("previousHash error");
        }
        if (!Objects.equals(createBlockHash(_newBlock), _newBlock.hash)) {
            return Failable.error("block hash error");
        }

        return Failable.success(_newBlock);
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
