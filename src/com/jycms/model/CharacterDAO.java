package com.jycms.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterDAO {

    /**
     * 简单的用户登录校验
     * 修正：表名 User 添加反引号 `User`
     */
    public boolean login(String username, String password) {
        // 修正: 使用反引号 `User` 括起表名
        String sql = "SELECT * FROM `User` WHERE username = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 搜索人物
     * 修正：表名 Character 添加反引号 `Character`
     */
    public List<Character> searchCharacters(String keyword, String type) {
        List<Character> characters = new ArrayList<>();
        String sql;
        String searchField;

        if ("人物名称".equals(type)) {
            searchField = "name";
        } else if ("小说名称".equals(type)) {
            searchField = "novel_name";
        } else {
            searchField = "name";
        }

        // 修正: 使用反引号 `Character` 括起表名
        sql = "SELECT id, name, novel_name, description_short, image_url FROM `Character` WHERE " + searchField + " LIKE ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Character c = new Character();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setNovelName(rs.getString("novel_name"));
                    c.setDescriptionShort(rs.getString("description_short"));
                    c.setImageUrl(rs.getString("image_url"));
                    characters.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return characters;
    }

    /**
     * 获取人物详细信息（包括武功）
     * 修正：表名 Character 和 MartialArt 添加反引号
     */
    public Character getCharacterDetails(int id) {
        Character character = null;
        // 修正: 使用反引号 `Character` 括起表名
        String charSql = "SELECT * FROM `Character` WHERE id = ?";
        // 修正: 使用反引号 `MartialArt` 括起表名
        String artsSql = "SELECT id, art_name, art_description FROM `MartialArt` WHERE char_id = ?";

        try (Connection conn = DBUtil.getConnection()) {

            // 1. 获取人物基础信息
            try (PreparedStatement charPstmt = conn.prepareStatement(charSql)) {
                charPstmt.setInt(1, id);
                try (ResultSet rs = charPstmt.executeQuery()) {
                    if (rs.next()) {
                        character = new Character();
                        character.setId(rs.getInt("id"));
                        character.setName(rs.getString("name"));
                        character.setNovelName(rs.getString("novel_name"));
                        character.setDescriptionShort(rs.getString("description_short"));
                        character.setDescriptionFull(rs.getString("description_full"));
                        character.setImageUrl(rs.getString("image_url"));
                        character.setMartialArts(new ArrayList<MartialArt>());
                    }
                }
            }

            // 2. 获取关联武功信息
            if (character != null) {
                try (PreparedStatement artsPstmt = conn.prepareStatement(artsSql)) {
                    artsPstmt.setInt(1, id);
                    try (ResultSet rs = artsPstmt.executeQuery()) {
                        while (rs.next()) {
                            MartialArt art = new MartialArt();
                            art.setId(rs.getInt("id"));
                            art.setArtName(rs.getString("art_name"));
                            art.setArtDescription(rs.getString("art_description"));
                            character.getMartialArts().add(art);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return character;
    }
}