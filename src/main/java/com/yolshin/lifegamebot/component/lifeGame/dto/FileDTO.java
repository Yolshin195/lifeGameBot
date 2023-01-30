package com.yolshin.lifegamebot.component.lifeGame.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FileDTO {

    @JsonProperty("fileRef")
    private String fileRef;

    @JsonProperty("name")
    private String name;

    @JsonProperty("size")
    private int size;


    public String getFileRef() {
        return fileRef;
    }

    public void setFileRef(String fileRef) {
        this.fileRef = fileRef;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
