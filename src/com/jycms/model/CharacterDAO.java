package com.jycms.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterDAO {

    /**
     * 登录验证（简单明文密码示例）
     * @return true 如果用户名密码匹配
     */
    public boolean login(String user, String pass) {
        String sql = "SELECT id FROM `User` WHERE username = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 模糊搜索人物（支持按人物名称或小说名称）
     * @param keyword 关键词
     * @param type "人物名称" 或 "小说名称"
     * @return 人物列表（包含简要信息）
     */
    public List<Character> searchCharacters(String keyword, String type) {
        List<Character> list = new ArrayList<>();
        String column = "name";
        if ("小说名称".equals(type) || "novel".equalsIgnoreCase(type)) {
            column = "novel_name";
        }
        String sql = "SELECT id, name, novel_name, description_short, image_url FROM `Character` WHERE " + column + " LIKE ? ORDER BY id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Character c = new Character();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setNovelName(rs.getString("novel_name"));
                    c.setDescriptionShort(rs.getString("description_short"));
                    c.setImageUrl(rs.getString("image_url"));
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取人物完整详情（包括 full 描述以及关联武功）
     */
    public Character getCharacterDetails(int id) {
        String sqlChar = "SELECT id, name, novel_name, description_short, description_full, image_url FROM `Character` WHERE id = ?";
        String sqlArts = "SELECT id, char_id, art_name, art_description FROM `MartialArt` WHERE char_id = ? ORDER BY id";
        Character c = null;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlChar)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    c = new Character();
                    c.setId(rs.getInt("id"));
                    c.setName(rs.getString("name"));
                    c.setNovelName(rs.getString("novel_name"));
                    c.setDescriptionShort(rs.getString("description_short"));
                    c.setDescriptionFull(rs.getString("description_full"));
                    c.setImageUrl(rs.getString("image_url"));
                } else {
                    return null;
                }
            }

            try (PreparedStatement ps2 = conn.prepareStatement(sqlArts)) {
                ps2.setInt(1, id);
                try (ResultSet rs2 = ps2.executeQuery()) {
                    while (rs2.next()) {
                        MartialArt ma = new MartialArt();
                        ma.setId(rs2.getInt("id"));
                        ma.setCharId(rs2.getInt("char_id"));
                        ma.setArtName(rs2.getString("art_name"));
                        ma.setArtDescription(rs2.getString("art_description"));
                        c.addMartialArt(ma);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }
}
