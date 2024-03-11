package com.threlease.base.functions.blockchains;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.threlease.base.utils.blockchains.Block;
import com.threlease.base.utils.blockchains.Chain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blockchains")
public class BlockChainsController {
    private final Chain node;

    @Autowired
    public BlockChainsController(Chain node) {
        this.node = node;
    }

    @GetMapping("/get/genesis")
    public Block getGenesis() {
        return Block.getGenesis();
    }

    @GetMapping("/get/latestBlock")
    public Block getLatestBlock() {
        return node.getLatestBlock();
    }

    @GetMapping("/get/chain")
    public List<Block> getChain(
            @RequestParam("page") long page
    ) {
        if (page <= 0) {
            return node.pagination(0);
        } else {
            return node.pagination(page);
        }
    }

    @PostMapping("/block")
    public Block MineBlock() {
        return node.addBlock(List.of(String.valueOf(System.currentTimeMillis()))).getValue();
    }
}
