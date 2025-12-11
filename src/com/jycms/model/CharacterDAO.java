package com.jycms.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterDAO {
    // 进行登录操作
    // 检验 username, password 是否存在于 user 表中
    public boolean login(String user, String pass) {
        String sql = "select id from user where username = ? and password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // 用传入的形参替换sql语句中的占位符.
            ps.setString(1, user);
            ps.setString(2, pass);
            // 获取sql语句运行的结果，如果存在返回true，否则返回false.
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 通过关键词和搜索类型，锁定符合关键字要求的人物
    public List<Character> searchCharacters(String keyword, String type) {
        // 创建一个元素类型为 Character 的动态数组
        List<Character> list = new ArrayList<>();
        // 将 n.name 修改为 novel_title
        // 使用视图，根据类型执行对应的操作
        StringBuilder sql = new StringBuilder("select * from v_character_base where ");

        if ("小说名称".equals(type)) {
            sql.append("novel_title like ?");
        } else {
            sql.append("name like ?");
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
        String sqlChar = "select * from v_character_base where id = ?";
        // SQL Arts：关联 CharacterArt 和 MartialArt 表
        String sqlArts = "select * from v_character_arts where char_id = ?";
        // 查询当前人物的所有关系
        String sqlRelations = "select * from v_character_relations where char_id_a = ?";

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
            // 获取选中人物的关系信息
            if (c != null) {
                try (PreparedStatement ps3 = conn.prepareStatement(sqlRelations)) {
                    ps3.setInt(1, id); // 查找所有 char_id_a 是当前人物的关系
                    try (ResultSet rs3 = ps3.executeQuery()) {
                        while (rs3.next()) {
                            CharacterRelation cr = new CharacterRelation();
                            cr.setId(rs3.getInt("id"));
                            cr.setCharIdA(id); // 当前人物 ID
                            cr.setCharIdB(rs3.getInt("char_id_b"));
                            cr.setRelationType(rs3.getString("relation_type"));
                            cr.setDescription(rs3.getString("description"));
                            cr.setRelatedCharName(rs3.getString("related_char_name"));

                            c.addRelation(cr); // 使用 Character 对象的 addRelation 方法
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
        String sql = "INSERT INTO `collection` (char_id) VALUES (?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, charId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * [DELETE] 移除人物收藏（从 collection 表中删除记录）
     * 这是一个关键的 DELETE 操作，用于弥补 CRUD 闭环
     * @param charId 要移除收藏的人物 ID
     * @return 移除是否成功
     */
    public boolean removeFromCollection(int charId) {
        // SQL: 从 collection 表中删除指定 char_id 的记录
        String sql = "DELETE FROM collection WHERE char_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, charId);

            // executeUpdate() 返回受影响的行数，大于 0 即为成功删除
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Character> getCollection() {
        List<Character> list = new ArrayList<>();
        // 三表关联：Collection -> Character -> Novel
        String sql = "select * from v_collection_details";
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