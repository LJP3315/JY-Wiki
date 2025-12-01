package com.jycms.model;

public class MartialArt {
    private int id;
    private String artName;

    public MartialArt() {}

    public MartialArt(int id, String artName) {
        this.id = id;
        this.artName = artName;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getArtName() { return artName; }
    public void setArtName(String artName) { this.artName = artName; }
}