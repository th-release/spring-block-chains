package com.threlease.base.utils.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Data
@Getter
@Setter
@Builder
public class BasicResponse {
    private boolean success;
    private Optional<String> message;
    private Optional<Object> data;

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNode = objectMapper.createObjectNode();

        jsonNode.put("success", success);
        message.ifPresent(s -> jsonNode.put("message", s));
        data.ifPresent(o -> jsonNode.put("data", objectMapper.valueToTree(o)));

        return objectMapper.writeValueAsString(jsonNode);
    }
}
