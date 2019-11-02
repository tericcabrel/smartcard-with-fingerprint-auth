package com.tericcabrel;

public class FingerprintScanner {
    private String id;
    private int type;

    public FingerprintScanner(String id, int type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public FingerprintScanner setId(String id) {
        this.id = id;
        return this;
    }

    public int getType() {
        return type;
    }

    public FingerprintScanner setType(int type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "FingerprintScanner{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
