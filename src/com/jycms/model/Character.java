package com.jycms.model;

import java.util.ArrayList;
import java.util.List;

public class Character {
    private int id;
    private String name;
    private String novelName;
    private String descriptionShort;
    private String descriptionFull;
    private String imageUrl;

    // 关联武功
    private List<MartialArt> martialArts = new ArrayList<>();

    public Character() {}

    public Character(int id, String name, String novelName, String descriptionShort, String descriptionFull, String imageUrl) {
        this.id = id;
        this.name = name;
        this.novelName = novelName;
        this.descriptionShort = descriptionShort;
        this.descriptionFull = descriptionFull;
        this.imageUrl = imageUrl;
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNovelName() { return novelName; }
    public void setNovelName(String novelName) { this.novelName = novelName; }

    public String getDescriptionShort() { return descriptionShort; }
    public void setDescriptionShort(String descriptionShort) { this.descriptionShort = descriptionShort; }

    public String getDescriptionFull() { return descriptionFull; }
    public void setDescriptionFull(String descriptionFull) { this.descriptionFull = descriptionFull; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<MartialArt> getMartialArts() { return martialArts; }
    public void setMartialArts(List<MartialArt> martialArts) { this.martialArts = martialArts; }

    public void addMartialArt(MartialArt art) { this.martialArts.add(art); }
}
