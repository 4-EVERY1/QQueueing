package com.example.tes24.qqueueing.channel;

public enum ContentType {
    SERIALIZE("application/octet-stream"),
    JSON("application/json"),
    TEXT_PLAIN("text/plain");

    private final String typeValue;

    ContentType(String typeValue) {
        this.typeValue = typeValue;
    }

    public String getTypeValue() {
        return typeValue;
    }
}