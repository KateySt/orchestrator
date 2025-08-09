package com.orchestrator.notification.model;

public record EventPayload(String source, String type, String data) {
}