package com.jycms.view.table;

import com.jycms.model.Character;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CharacterTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "姓名", "小说", "简介", "图片", "操作"};
    private List<Character> characters;

    public CharacterTableModel(List<Character> characters) {
        this.characters = characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters;
        fireTableDataChanged();
    }

    public Character getCharacterAt(int row) {
        if (row >= 0 && row < characters.size()) return characters.get(row);
        return null;
    }

    @Override
    public int getRowCount() {
        return characters == null ? 0 : characters.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) return Integer.class;
        if (columnIndex == 5) return Object.class;
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Character c = characters.get(rowIndex);
        switch (columnIndex) {
            case 0: return c.getId();
            case 1: return c.getName();
            case 2: return c.getNovelName();
            case 3: return c.getDescriptionShort();
            case 4: return c.getImageUrl();
            case 5: return "查看更多";
            default: return "";
        }
    }
}