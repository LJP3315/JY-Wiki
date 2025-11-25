package com.jycms.model;

import java.util.List;

public class Character {
    private int id;
    private String name;
    private String novelName;
    private String descriptionShort;
    private String descriptionFull;
    private String imageUrl;
    private List<MartialArt> martialArts;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getNovelName() {
        return novelName;
    }
    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }

    public String getDescriptionShort() {
        return descriptionShort;
    }
    public void setDescriptionShort(String descriptionShort) {
        this.descriptionShort = descriptionShort;
    }

    public String getDescriptionFull() {
        return descriptionFull;
    }
    public void setDescriptionFull(String descriptionFull) {
        this.descriptionFull = descriptionFull;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<MartialArt> getMartialArts() {
        return martialArts;
    }
    public void setMartialArts(List<MartialArt> martialArts) {
        this.martialArts = martialArts;
    }
}