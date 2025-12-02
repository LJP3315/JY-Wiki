package com.jycms.model;

import java.util.ArrayList;
import java.util.List;

public class Character {
    private int id;
    // --- 新增/修改部分 ---
    private int novelId;
    private String novelName; // 通过 JOIN 查询填充
    // 泛型类型修改
    private List<CharacterArt> martialArts = new ArrayList<>();
    // ---------------------

    private String name;
    private String descriptionShort;
    private String descriptionFull;
    private String imageUrl;

    public Character() {}

    // ... 省略构造函数 ...

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNovelId() { return novelId; }
    public void setNovelId(int novelId) { this.novelId = novelId; }

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

    // --- 关键修改：返回类型和参数类型 ---
    public List<CharacterArt> getMartialArts() { return martialArts; }
    public void setMartialArts(List<CharacterArt> martialArts) { this.martialArts = martialArts; }

    public void addMartialArt(CharacterArt art) {
        this.martialArts.add(art);
    }
}