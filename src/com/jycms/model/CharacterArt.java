package com.jycms.model;

/**
 * 人物武功关联实体：实现 Character 和 MartialArt 的多对多关系，
 * 携带关系属性 artDescription。
 */
public class CharacterArt {
    private int id;
    private int charId;
    private int artId;
    private String artDescription;
    // 用于 UI 显示的辅助字段，通过 JOIN 查询填充
    private String artName;

    public CharacterArt() {}

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCharId() { return charId; }
    public void setCharId(int charId) { this.charId = charId; }
    public int getArtId() { return artId; }
    public void setArtId(int artId) { this.artId = artId; }
    public String getArtDescription() { return artDescription; }
    public void setArtDescription(String artDescription) { this.artDescription = artDescription; }
    public String getArtName() { return artName; }
    public void setArtName(String artName) { this.artName = artName; }
}