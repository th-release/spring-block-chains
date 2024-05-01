package com.threlease.base.utils.blockchains;

import java.util.List;

public class Config {
    static Block GENESIS = new Block(null, List.of("Block #1"), null);
    static long DIFFICULTY_ADJUSTMENT_INTERVAL = 10;
    static long BLOCK_GENERATION_INTERVAL = 10;
    static long BLOCK_GENERATION_TIME_UNIT = 60;
}