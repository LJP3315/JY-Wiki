package com.jycms.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CharacterDAO {
    /**
    * 进行登录操作
    * @param username 获取的用户名文本框的输入
    * @param password 获取的密码文本框的输入
    * @return 是否登录成功
    */
    public boolean login(String username, String password) {
        // 在 user 表中查找匹配的用户名和密码
        String sql = "SELECT * FROM `user` WHERE username = ? AND password = ?";
        // try-with-resources 自动管理连接和语句关闭
        try (Connection conn = DBUtil.getConnection();  // 使用 DBUtil 类的静态方法，连接数据库
             PreparedStatement pstmt = conn.prepareStatement(sql)) {  // 预编译 SQL 语句
            // 保证设置参数的安全性，防止 SQL 注入
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            // 如果找到匹配的记录
            try (ResultSet rs = pstmt.executeQuery()) {
                // 登录成功，返回 true
                return rs.next();
            }
        } catch (SQLException e) {
            // 连接失败，返回 false
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param keyword 搜索文本框的关键词
     * @param type 用户选择的搜索字段 (姓名，小说名)
     * @return 输出按照字段搜索的搜索表
     * */
    public List<Character> searchCharacters(String keyword, String type) {
        // 新建人物表
        List<Character> characters = new ArrayList<>();
        String sql;
        String searchField;

        // 根据传入的 'type' 确定数据库中要搜索的字段
        if ("人物名称".equals(type)) {
            searchField = "name";
        } else if ("小说名称".equals(type)) {
            searchField = "novel_name";
        } else {
            searchField = "name";
        }

        sql = "SELECT id, name, novel_name, description_short, image_url FROM `Character` WHERE " + searchField + " LIKE ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 对占位符进行赋值，进行模糊查询
            pstmt.setString(1, "%" + keyword + "%");

            // 获取结果集合
            try (ResultSet rs = pstmt.executeQuery()) {
                // 当结果集合为空，停止遍历
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
     * @param id 人物唯一的编号
     * @return 返回人物
     */
    public Character getCharacterDetails(int id) {
        Character character = null;
        // 根据 id 查找人物信息
        String charSql = "SELECT * FROM `Character` WHERE id = ?";
        // 根据 char_id 查找人物所有的武功信息
        String artsSql = "SELECT id, art_name, art_description FROM `MartialArt` WHERE char_id = ?";

        try (Connection conn = DBUtil.getConnection()) {
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

            if (character != null) {
                try (PreparedStatement artsPstmt = conn.prepareStatement(artsSql)) {
                    artsPstmt.setInt(1, id);
                    try (ResultSet rs = artsPstmt.executeQuery()) {
                        while (rs.next()) {
                            // 一个人物可以拥有多种武功
                            // 每一次查询，新开一个武功的对象
                            MartialArt art = new MartialArt();
                            art.setId(rs.getInt("id"));
                            art.setArtName(rs.getString("art_name"));
                            art.setArtDescription(rs.getString("art_description"));
                            // 为当前人物添加武功
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