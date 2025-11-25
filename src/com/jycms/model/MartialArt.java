package com.jycms.model;

public class MartialArt {
    private int id;
    private int charId;
    private String artName;
    private String artDescription;

    public MartialArt() {}

    public MartialArt(int id, int charId, String artName, String artDescription) {
        this.id = id;
        this.charId = charId;
        this.artName = artName;
        this.artDescription = artDescription;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCharId() { return charId; }
    public void setCharId(int charId) { this.charId = charId; }

    public String getArtName() { return artName; }
    public void setArtName(String artName) { this.artName = artName; }

    public String getArtDescription() { return artDescription; }
    public void setArtDescription(String artDescription) { this.artDescription = artDescription; }
}
