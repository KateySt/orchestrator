package com.orchestrator.product.model;

public record EventPayload(String source, String type, String data) {
}