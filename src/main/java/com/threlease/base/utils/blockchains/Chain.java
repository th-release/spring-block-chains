package com.threlease.base.utils.blockchains;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threlease.base.utils.Failable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class Chain {
    private List<Block> blockChain = new ArrayList<>();
    public Chain() {
        this.blockChain.add(Block.getGenesis());
    }

    public List<Block> getChain() {
        return this.blockChain;
    }

    public List<Block> pagination(long page) {
        long startIndex = (page - 1) * 15;
        long endIndex = blockChain.size() < 15 ? blockChain.size() : Math.min(startIndex + 15, blockChain.size());

        return this.blockChain.subList((int) startIndex, (int) endIndex);
    }

    public long getLength() {
        return this.blockChain.size();
    }

    public Block getLatestBlock() {
        return this.blockChain.get(this.blockChain.size() - 1);
    }

    public Failable<Block, String> addBlock(List<String> data) {
        Block prevBlock = this.getLatestBlock();

        Block adjustmentBlock = this.getAdjustmentBlock();
        Block newBlock = Block.generateBlock(prevBlock, data, adjustmentBlock);

        Failable<Block, String> isValid = Block.isValidNewBlock(newBlock, prevBlock);

        if (isValid.isError())
            return Failable.error(isValid.getError());

        this.blockChain.add(newBlock);
        return Failable.success(newBlock);
    }

    public Block getAdjustmentBlock() {
        long currentLength = this.getLength();
        return this.getLength() < Config.DIFFICULTY_ADJUSTMENT_INTERVAL
                ? Block.getGenesis()
                : this.blockChain.get((int) (currentLength - Config.DIFFICULTY_ADJUSTMENT_INTERVAL));
    }

    public Failable<Boolean, String> addToChain(Block _recvBlock) {
        Failable<Block, String> isValid = Block.isValidNewBlock(
                _recvBlock,
                this.getLatestBlock()
        );
        if (isValid.isError())
            return Failable.error(isValid.getError());

        this.blockChain.add(_recvBlock);
        return Failable.success(true);
    }

    public Failable<Long, String> isValidChain(List<Block> _chain) {
        Block genesis = _chain.get(0);
        if (!Objects.equals(genesis.hash, Block.getGenesis().hash))
            return Failable.error("Genesis Match Error");

        // ToDo : 나머지 체인에 대한 검증 코드
        for (long i = 1; i < _chain.size(); i++) {
            Block newBlock = _chain.get((int) i);
            Block previousBlock = _chain.get((int) (i - 1));
            Failable<Block, String> isValid = Block.isValidNewBlock(newBlock, previousBlock);
            if (isValid.isError()) return Failable.error(isValid.getError());
        }

        return Failable.success(null);
    }

    public Failable<Long, String> replaceChain(List<Block> receivedChain) {
        Block latestReceivedBlock = receivedChain.get(receivedChain.size() - 1);
        Block latestBlock = getLatestBlock();

        if (latestReceivedBlock.height == 0) {
            return Failable.error("받은 체인의 최신 블록이 제네시스 블록입니다.");
        }

        if (latestReceivedBlock.height <= latestBlock.height) {
            return Failable.error("자신의 체인이 더 길거나 같습니다.");
        }

        this.blockChain = receivedChain;
        return Failable.success(null);
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}