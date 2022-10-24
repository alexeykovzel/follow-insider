package com.alexeykovzel.fi.utils;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.function.Consumer;

public abstract class JsonParser {

    public boolean hasOne(JsonNode node) {
        return (node != null) && (node.asInt() == 1);
    }

    public void handleAnyNode(JsonNode node, Consumer<JsonNode> consumer) {
        if (node.isArray()) {
            for (JsonNode arrayNode : node) {
                consumer.accept(arrayNode);
            }
        } else {
            consumer.accept(node);
        }
    }
}
