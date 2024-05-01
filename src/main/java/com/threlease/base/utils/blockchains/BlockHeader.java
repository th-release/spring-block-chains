package com.threlease.base.utils.blockchains;

public class BlockHeader {
    public String version;
    public long height;
    public long timestamp;
    public String previousHash;

    public BlockHeader(Block prevBlock) {
        this.version = getVersion();
        this.timestamp = getTimestamp();
        this.height = prevBlock == null ? 0 : prevBlock.height + 1;
        this.previousHash = prevBlock == null ? "0".repeat(64) : prevBlock.hash;
    }

    public static String getVersion() {
        return "1.0.0";
    }

    public static long getTimestamp() {
        return System.currentTimeMillis();
    }
}
