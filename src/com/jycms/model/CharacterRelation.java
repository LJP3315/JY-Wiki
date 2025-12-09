package com.jycms.model;

public class CharacterRelation {
    private int id;
    private int charIdA;
    private int charIdB;
    private String relationType;
    private String description;

    private String relatedCharName;

    public CharacterRelation() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCharIdA() {
        return charIdA;
    }

    public void setCharIdA(int charIdA) {
        this.charIdA = charIdA;
    }

    public int getCharIdB() {
        return charIdB;
    }

    public void setCharIdB(int charIdB) {
        this.charIdB = charIdB;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRelatedCharName() {
        return relatedCharName;
    }

    public void setRelatedCharName(String relatedCharName) {
        this.relatedCharName = relatedCharName;
    }
}
