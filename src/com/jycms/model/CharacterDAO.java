package com.jycms.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterDAO {

    public boolean login(String user, String pass) {
        // (登录逻辑保持不变，此处省略)
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
     * 修改：使用 JOIN Novel 查询小说名称
     */
    public List<Character> searchCharacters(String keyword, String type) {
        List<Character> list = new ArrayList<>();
        // 核心修改：关联 Novel 表获取小说标题 (别名 novel_title)
        StringBuilder sql = new StringBuilder("SELECT c.*, n.title as novel_title FROM `Character` c LEFT JOIN `Novel` n ON c.novel_id = n.id WHERE ");

        if ("小说名称".equals(type)) {
            sql.append("n.title LIKE ?");
        } else {
            sql.append("c.name LIKE ?");
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToCharacter(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 修改：详情查询使用 JOIN Novel，武功查询使用三表联查
     */
    public Character getCharacterDetails(int id) {
        Character c = null;
        // SQL Char：关联 Novel 表
        String sqlChar = "SELECT c.*, n.title as novel_title FROM `Character` c LEFT JOIN `Novel` n ON c.novel_id = n.id WHERE c.id = ?";
        // SQL Arts：关联 CharacterArt 和 MartialArt 表
        String sqlArts = "SELECT ca.id, ca.art_description, ma.art_name " +
                "FROM `CharacterArt` ca " +
                "JOIN `MartialArt` ma ON ca.art_id = ma.id " +
                "WHERE ca.char_id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            // 1. 获取人物基础信息
            try (PreparedStatement ps = conn.prepareStatement(sqlChar)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        c = mapResultSetToCharacter(rs); // 包含 full description
                    } else {
                        return null;
                    }
                }
            }

            // 2. 获取关联武功信息 (三表联查)
            if (c != null) {
                try (PreparedStatement ps2 = conn.prepareStatement(sqlArts)) {
                    ps2.setInt(1, id);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            CharacterArt ca = new CharacterArt(); // 映射到 CharacterArt
                            ca.setId(rs2.getInt("id"));
                            ca.setArtDescription(rs2.getString("art_description"));
                            ca.setArtName(rs2.getString("art_name"));
                            c.addMartialArt(ca);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    // --- 新增：收藏功能相关方法 ---

    public boolean addToCollection(int charId) {
        String sql = "INSERT INTO `Collection` (char_id) VALUES (?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, charId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // 捕获到 SQL 异常，可能是重复收藏 (UNIQUE KEY 约束)
            // e.printStackTrace();
            return false;
        }
    }

    public List<Character> getCollection() {
        List<Character> list = new ArrayList<>();
        // 三表关联：Collection -> Character -> Novel
        String sql = "SELECT c.*, n.title as novel_title FROM `Collection` col " +
                "JOIN `Character` c ON col.char_id = c.id " +
                "LEFT JOIN `Novel` n ON c.novel_id = n.id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToCharacter(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- 辅助方法：统一映射逻辑 ---
    private Character mapResultSetToCharacter(ResultSet rs) throws SQLException {
        Character c = new Character();
        c.setId(rs.getInt("id"));
        c.setNovelId(rs.getInt("novel_id"));
        c.setName(rs.getString("name"));
        c.setNovelName(rs.getString("novel_title"));
        c.setDescriptionShort(rs.getString("description_short"));
        c.setImageUrl(rs.getString("image_url"));
        // 尝试获取 description_full，如果 select * 包含了它就能获取到
        try {
            c.setDescriptionFull(rs.getString("description_full"));
        } catch (SQLException e) {
            // 忽略，因为搜索列表不需要 full description
        }
        return c;
    }
}