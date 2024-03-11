package com.threlease.base.functions.blockchains.p2p;

import com.threlease.base.utils.blockchains.Block;
import lombok.Getter;

@Getter
public class WsRequest {
    private String command;
    private Block Active;
}
