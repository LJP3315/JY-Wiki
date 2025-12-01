package com.jycms.controller;

import com.jycms.model.Character;
import com.jycms.model.CharacterDAO;
import java.util.List;

public class SystemController {
    private final CharacterDAO dao = new CharacterDAO();

    public boolean login(String username, String password) {
        return dao.login(username, password);
    }

    public List<Character> searchCharacters(String keyword, String type) {
        return dao.searchCharacters(keyword, type);
    }

    public Character getCharacterDetails(int charId) {
        return dao.getCharacterDetails(charId);
    }

    // --- 新增部分 ---
    public boolean addToCollection(int charId) {
        return dao.addToCollection(charId);
    }

    public List<Character> getCollection() {
        return dao.getCollection();
    }
    // ----------------
}