package com.threlease.base.utils.blockchains;

import lombok.Data;

import java.util.List;

public class Config {
    static Block GENESIS = new Block(null, List.of("[cth Genesis]"), null);
    static long DIFFICULTY_ADJUSTMENT_INTERVAL = 10;
    static long BLOCK_GENERATION_INTERVAL = 10;
    static long BLOCK_GENERATION_TIME_UNIT = 60;
}
