package com.jycms.controller;

import com.jycms.model.Character;
import com.jycms.model.CharacterDAO;
import java.util.List;

public class SystemController {
    // 声明数据访问对象 dao
    private final CharacterDAO dao = new CharacterDAO();

    // 通过数据访问对象(DAO)中的方法来进行相关操作
    // 在 LoginFrame 窗口进行的登录操作
    public boolean login(String username, String password) {
        return dao.login(username, password);
    }

    // 在 MainFrame 窗口进行的搜索操作
    public List<Character> searchCharacters(String keyword, String type) {
        return dao.searchCharacters(keyword, type);
    }

    // 在 MainFrame 窗口进行的获取人物所有信息操作
    public Character getCharacterDetails(int charId) {
        return dao.getCharacterDetails(charId);
    }

    // 在 MainFrame 窗口进行的将选中人物加入收藏的操作
    public boolean addToCollection(int charId) {
        return dao.addToCollection(charId);
    }

    // 在 MainFrame 窗口的获取收藏夹中所有人物的操作
    public List<Character> getCollection() {
        return dao.getCollection();
    }
}