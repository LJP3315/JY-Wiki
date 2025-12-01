package com.jycms.model;

import java.util.ArrayList;
import java.util.List;

public class Character {
    // 主键 人物id
    private int id;

    private int novelId;
    // 人物所属小说名称
    private String novelName;

    // 人物姓名
    private String name;
    // 人物的短评
    private String descriptionShort;
    // 人物的完整评价，可以放置在人物界面显示
    private String descriptionFull;
    // 人物图片的存放路径
    private String imageUrl;
    // 人物掌握的武功列表，一个人物实体可以掌握多种武功
    private List<MartialArt> martialArts = new ArrayList<>();

    // 进行封装，通过公共的 getter 和 setter 来对数据进行获取和修改
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getNovelId() {
        return novelId;
    }

    public void setNovelId(int novelId) {
        this.novelId = novelId;
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

    public void addMartialArt(MartialArt art) {
        this.martialArts.add(art);
    }
}