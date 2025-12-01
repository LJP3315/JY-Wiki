package com.jycms.view.table;

import com.jycms.model.Character;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * CharacterTableModel - 用于 JTable 的独立表格模型
 * 简化：移除了操作列。
 * 列: {"ID", "姓名", "小说", "简介", "图片URL"}
 */
public class CharacterTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    // 关键修改：移除 "操作" 列
    private final String[] columns = {"ID", "姓名", "小说", "简介", "图片URL"};
    private List<Character> characters = new ArrayList<>();

    public CharacterTableModel(List<Character> characters) {
        this.characters = characters == null ? new ArrayList<>() : characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters == null ? new ArrayList<>() : characters;
        fireTableDataChanged();
    }

    public Character getCharacterAt(int row) {
        if (row >= 0 && row < characters.size()) {
            return characters.get(row);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return characters == null ? 0 : characters.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length; // 5 列
    }

    @Override
    public String getColumnName(int column) {
        if (column >= 0 && column < columns.length) {
            return columns[column];
        }
        return super.getColumnName(column);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) return Integer.class; // ID
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // 简化后，没有任何单元格是可编辑的
        return false;
    }

    @Override
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