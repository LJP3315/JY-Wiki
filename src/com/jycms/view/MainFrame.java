package com.jycms.view;

import com.jycms.controller.SystemController;
import com.jycms.model.Character;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * 主窗口：搜索 + 结果表格
 */
public class MainFrame extends JFrame {
    private SystemController controller;

    private JTextField tfKeyword;
    private JComboBox<String> cbType;
    private JButton btnSearch;
    private JTable table;
    private CharacterTableModel tableModel;

    public MainFrame(SystemController controller) {
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        setTitle("Jinyong Character Management System (JY-CMS)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tfKeyword = new JTextField(30);
        cbType = new JComboBox<>(new String[]{"人物名称", "小说名称"});
        btnSearch = new JButton("搜索");

        topPanel.add(new JLabel("关键字:"));
        topPanel.add(tfKeyword);
        topPanel.add(new JLabel("搜索类型:"));
        topPanel.add(cbType);
        topPanel.add(btnSearch);

        add(topPanel, BorderLayout.NORTH);

        // table model
        tableModel = new CharacterTableModel(new ArrayList<Character>());
        table = new JTable(tableModel);

        // 动作列（按钮）
        table.getColumnModel().getColumn(tableModel.getColumnCount() - 1)
                .setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(tableModel.getColumnCount() - 1)
                .setCellEditor(new ButtonEditor(new JCheckBox()));

        // 双击行查看详情
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        Character c = tableModel.getCharacterAt(row);
                        showDetail(c.getId());
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 搜索事件
        btnSearch.addActionListener(e -> doSearch());

        // 回车触发搜索
        tfKeyword.addActionListener(e -> doSearch());
    }

    private void doSearch() {
        String keyword = tfKeyword.getText().trim();
        String type = (String) cbType.getSelectedItem();
        java.util.List<Character> results = controller.searchCharacters(keyword, type);
        tableModel.setCharacters(results);
    }

    private void showDetail(int charId) {
        // 调用 controller 获取详情并打开对话框
        Character full = controller.getCharacterDetails(charId);
        if (full != null) {
            DetailDialog dialog = new DetailDialog(this, full);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "未找到该人物的详细信息", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ---------------- CharacterTableModel ----------------
    private class CharacterTableModel extends AbstractTableModel {
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
            if (columnIndex == 5) return Object.class; // button
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 5; // 只有操作列可编辑（用于按钮）
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

    // ---------------- ButtonRenderer ----------------
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    // ---------------- ButtonEditor ----------------
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    // 打开详情
                    Character c = tableModel.getCharacterAt(selectedRow);
                    if (c != null) {
                        showDetail(c.getId());
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            selectedRow = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
