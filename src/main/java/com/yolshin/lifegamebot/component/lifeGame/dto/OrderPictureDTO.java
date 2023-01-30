package com.yolshin.lifegamebot.component.lifeGame.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderPictureDTO {

    @JsonProperty("picture")
    private String picture;

    @JsonProperty("description")
    private String description;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setPicture(FileDTO file) {
        this.picture = file.getFileRef();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
