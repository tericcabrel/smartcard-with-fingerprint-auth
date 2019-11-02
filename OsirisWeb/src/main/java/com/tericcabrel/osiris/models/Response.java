package com.tericcabrel.osiris.models;

public class Response {
    private String fileName;
    private String fileType;
    private long size;

    public Response(String fileName, String fileType, long size) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public Response setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public Response setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public long getSize() {
        return size;
    }

    public Response setSize(long size) {
        this.size = size;
        return this;
    }
}