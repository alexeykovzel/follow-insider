package com.alexeykovzel.fi.common;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Consumer;

public abstract class JsonParser {

    public boolean hasOne(JsonNode node) {
        return (node != null) && (node.asInt() == 1);
    }

    public void acceptNode(JsonNode node, Consumer<JsonNode> consumer) {
        if (node.isArray()) {
            for (JsonNode arrayNode : node) {
                consumer.accept(arrayNode);
            }
        } else {
            consumer.accept(node);
        }
    }
}
