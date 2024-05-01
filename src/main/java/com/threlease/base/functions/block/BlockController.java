package com.threlease.base.functions.block;

import com.threlease.base.utils.blockchains.Block;
import com.threlease.base.utils.blockchains.Chain;
import com.threlease.base.utils.blockchains.Peer;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/blockchain")
public class BlockController {
    private final Chain node;

    public BlockController(Chain node, Peer peers) {
        this.node = node;
    }

    @GetMapping("/genesis")
    public Block getGenesis() {
        return Block.getGenesis();
    }

    @GetMapping("/latestBlock")
    public Block getLatestBlock() {
        return node.getLatestBlock();
    }

    @GetMapping("/chain")
    public List<Block> getChain(
            @RequestParam("page") long page
    ) {
        if (page <= 0) {
            return node.pagination(1);
        } else {
            return node.pagination(page);
        }
    }

    @PostMapping("/block")
    public Block MineBlock() {
        return node.addBlock(List.of("Block #" + (node.getLength() + 1))).getValue();
    }
}
