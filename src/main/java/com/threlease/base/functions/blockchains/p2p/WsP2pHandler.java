package com.threlease.base.functions.blockchains.p2p;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.threlease.base.utils.Failable;
import com.threlease.base.utils.blockchains.Block;
import com.threlease.base.utils.blockchains.Chain;
import com.threlease.base.utils.responses.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WsP2pHandler extends TextWebSocketHandler {
    private final Chain node;
    private static final ConcurrentHashMap<String, WebSocketSession> client = new ConcurrentHashMap<String, WebSocketSession>();

    @Autowired
    public WsP2pHandler(Chain node) {
        this.node = node;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        client.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        client.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        String id = session.getId();
        client.forEach((key, value) -> {
            String payload = message.getPayload();
            ObjectMapper objectMapper = new ObjectMapper();

            if (key.equals(id)) {
                try {
                    WsRequest request = objectMapper.readValue(payload, WsRequest.class);
                    BasicResponse response = BasicResponse.builder().build();
                    switch (request.getCommand()) {
                        case "new":
                            value.sendMessage(new TextMessage(request.getCommand()));
                            break;
                        case "latest_block":
                            response = BasicResponse.builder()
                                    .success(true)
                                    .message(Optional.of(request.getCommand()))
                                    .data(Optional.of(node.getLatestBlock()))
                                    .build();

                            value.sendMessage(new TextMessage(response.toJson()));
                            break;
                        case "all_block":
                            Optional<List<Block>> all_block = all_block();

                            response = BasicResponse.builder()
                                    .success(all_block.isPresent())
                                    .message(Optional.of(request.getCommand()))
                                    .data(Optional.of(all_block))
                                    .build();

                            value.sendMessage(new TextMessage(response.toJson()));
                            break;
                        case "receivedChain":
                            this.receivedChain(value);
                            break;
                        default:
                            response = BasicResponse.builder()
                                    .success(false)
                                    .message(Optional.of("올바르지 못한 요청 값 입니다."))
                                    .data(Optional.empty())
                                    .build();

                            value.sendMessage(new TextMessage(response.toJson()));
                            break;
                    }
                } catch (JsonProcessingException e) {
                    BasicResponse response = BasicResponse.builder()
                            .success(false)
                            .message(Optional.of("올바르지 못한 요청 값 입니다."))
                            .data(Optional.empty())
                            .build();
                    try {
                        value.sendMessage(new TextMessage(response.toJson()));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public Optional<List<Block>> all_block() throws IOException {
        Failable<Long, String> isValid = this.node.addToChain(node.getLatestBlock());
        if (isValid.isError()) return Optional.empty();
        return Optional.of(node.getChain());
    }

    public void receivedChain(WebSocketSession ws) throws IOException {
        BasicResponse response = BasicResponse.builder()
                .success(true)
                .build();

//                            Todo: 체인을 교체하는 코드 (보다 긴 체인 선택하기)
        ws.sendMessage(new TextMessage(response.toJson()));
    }
}
