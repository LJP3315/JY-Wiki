package com.jycms.controller;

import com.jycms.model.Character;
import com.jycms.model.CharacterDAO;

import java.util.List;

public class SystemController {
    private CharacterDAO dao;

    public SystemController() {
        this.dao = new CharacterDAO();
    }

    public boolean login(String username, String password) {
        return dao.login(username, password);
    }

    public List<Character> searchCharacters(String keyword, String type) {
        return dao.searchCharacters(keyword == null ? "" : keyword.trim(), type);
    }

    public Character getCharacterDetails(int id) {
        return dao.getCharacterDetails(id);
    }
}
