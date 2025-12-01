package com.jycms.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterDAO {

    public boolean login(String user, String pass) {
        // (登录逻辑保持不变，此处省略以节省篇幅)
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
     * 修改：使用 JOIN 查询，支持按小说标题或人物名称搜索
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
     * 修改：详情查询也需要 JOIN 获取小说名
     */
    public Character getCharacterDetails(int id) {
        Character c = null;
        // 核心修改：关联查询
        String sqlChar = "SELECT c.*, n.title as novel_title FROM `Character` c LEFT JOIN `Novel` n ON c.novel_id = n.id WHERE c.id = ?";
        String sqlArts = "SELECT id, art_name, art_description FROM `MartialArt` WHERE char_id = ?";

        try (Connection conn = DBUtil.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlChar)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        c = mapResultSetToCharacter(rs);
                    } else {
                        return null;
                    }
                }
            }
            // 加载武功逻辑不变
            if (c != null) {
                try (PreparedStatement ps2 = conn.prepareStatement(sqlArts)) {
                    ps2.setInt(1, id);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            MartialArt ma = new MartialArt();
                            ma.setId(rs2.getInt("id"));
                            ma.setArtName(rs2.getString("art_name"));
                            ma.setArtDescription(rs2.getString("art_description"));
                            c.addMartialArt(ma);
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
        if (isCollected(charId)) return false; // 防止重复
        String sql = "INSERT INTO `Collection` (char_id) VALUES (?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, charId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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

    public boolean isCollected(int charId) {
        String sql = "SELECT id FROM `Collection` WHERE char_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, charId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- 辅助方法：统一映射逻辑 ---
    private Character mapResultSetToCharacter(ResultSet rs) throws SQLException {
        Character c = new Character();
        c.setId(rs.getInt("id"));
        c.setNovelId(rs.getInt("novel_id"));
        c.setName(rs.getString("name"));
        // 注意：这里使用的是 JOIN 出来的别名 'novel_title'
        c.setNovelName(rs.getString("novel_title"));
        c.setDescriptionShort(rs.getString("description_short"));
        c.setImageUrl(rs.getString("image_url"));
        // 尝试获取 description_full，如果 select * 包含了它就能获取到
        try {
            c.setDescriptionFull(rs.getString("description_full"));
        } catch (SQLException e) {
            // 搜索列表可能不包含 full description，忽略错误
        }
        return c;
    }
}