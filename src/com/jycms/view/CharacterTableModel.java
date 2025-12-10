package com.jycms.view;

import com.jycms.model.Character;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CharacterTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "姓名", "小说", "简介", "图片URL"};
    private List<Character> characters = new ArrayList<>();

    // 构造函数，如果人物列表为空，返回空列表，否则返回传入的参数
    public CharacterTableModel(List<Character> characters) {
        this.characters = characters == null ? new ArrayList<>() : characters;
    }

    // 刷新人物表格
    public void setCharacters(List<Character> characters) {
        this.characters = characters == null ? new ArrayList<>() : characters;
        // 通知 JTable 重绘自身
        fireTableDataChanged();
    }

    // 获取用户点击对象的行号
    public Character getCharacterAt(int row) {
        if (row >= 0 && row < characters.size()) {
            return characters.get(row);
        }
        return null;
    }

    @Override
    // 显示行数
    public int getRowCount() {
        return characters == null ? 0 : characters.size();
    }

    @Override
    // 显示列数
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    // 将列名传给 JTable
    public String getColumnName(int column) {
        if (column >= 0 && column < columns.length) {
            return columns[column];
        }
        return super.getColumnName(column);
    }

    @Override
    // 返回列对象的类型
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) return Integer.class; // ID
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // 单元格设置为不可编辑
        return false;
    }

    @Override
    // 获取指定列、指定行的信息
    public Object getValueAt(int rowIndex, int columnIndex) {
        Character c = getCharacterAt(rowIndex);
        if (c == null) return null;
        switch (columnIndex) {
            case 0: return c.getId();
            case 1: return c.getName();
            case 2: return c.getNovelName();
            case 3: return c.getDescriptionShort();
            case 4: return c.getImageUrl();
            default: return null;
        }
    }
}