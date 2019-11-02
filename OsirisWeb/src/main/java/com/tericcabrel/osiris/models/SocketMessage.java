package com.tericcabrel.osiris.models;

public class SocketMessage {
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public SocketMessage setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public SocketMessage setMessage(String message) {
        this.message = message;
        return this;
    }
}
