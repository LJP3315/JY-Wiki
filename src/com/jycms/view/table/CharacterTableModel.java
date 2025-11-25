package com.jycms.view.table;

import com.jycms.model.Character;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * CharacterTableModel - 用于 JTable 的独立表格模型
 * 与 MainFrame 中的列定义保持一致：
 * 列: {"ID", "姓名", "小说", "简介", "图片", "操作"}
 */
public class CharacterTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;

    private final String[] columns = {"ID", "姓名", "小说", "简介", "图片", "操作"};
    private List<Character> characters = new ArrayList<>();

    public CharacterTableModel() {}

    public CharacterTableModel(List<Character> characters) {
        this.characters = characters == null ? new ArrayList<>() : characters;
    }

    public void setCharacters(List<Character> characters) {
        this.characters = characters == null ? new ArrayList<>() : characters;
        fireTableDataChanged();
    }

    public List<Character> getCharacters() {
        return characters;
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
        return columns.length;
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
        if (columnIndex == 5) return Object.class;  // 操作列（按钮或其它组件）
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // 仅操作列可编辑（用于按钮）
        return columnIndex == 5;
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
            case 5: return "查看更多"; // 操作列显示文本（可由 renderer/editor 处理成按钮）
            default: return null;
        }
    }
}
