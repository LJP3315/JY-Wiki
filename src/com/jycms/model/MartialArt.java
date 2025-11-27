package com.jycms.model;

public class MartialArt {
    // 武功编号
    private int id;
    // 武功的名称
    private String artName;
    // 武功的描述
    private String artDescription;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getArtName() {
        return artName;
    }
    public void setArtName(String artName) {
        this.artName = artName;
    }

    public String getArtDescription() {
        return artDescription;
    }
    public void setArtDescription(String artDescription) {
        this.artDescription = artDescription;
    }
}