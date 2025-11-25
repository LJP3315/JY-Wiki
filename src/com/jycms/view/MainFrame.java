package com.jycms.view;

import com.jycms.controller.SystemController;
import com.jycms.model.Character;
import com.jycms.view.table.ButtonEditor;
import com.jycms.view.table.ButtonRenderer;
import com.jycms.view.table.CharacterTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private final SystemController controller;

    private JTextField tfKeyword;
    private JComboBox<String> cbType;
    private JButton btnSearch;
    private JTable table;
    private CharacterTableModel tableModel;

    public MainFrame(SystemController controller) {
        this.controller = controller;
        initUI();
        // 初始加载一次数据
        doSearch();
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

        // 动作列设置
        table.getColumnModel().getColumn(tableModel.getColumnCount() - 1)
                .setCellRenderer(new ButtonRenderer());
        // 传入 MainFrame 作为 ownerFrame
        table.getColumnModel().getColumn(tableModel.getColumnCount() - 1)
                .setCellEditor(new ButtonEditor(table, controller, this));

        // 双击行查看详情 (使用匿名内部类)
        table.addMouseListener(new MouseAdapter() {
            @Override
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

        // 搜索事件 (使用命名内部类)
        SearchActionListener searchListener = new SearchActionListener();
        btnSearch.addActionListener(searchListener);
        tfKeyword.addActionListener(searchListener);
    }

    /**
     * 搜索按钮和回车的 ActionListener (命名内部类)
     */
    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            doSearch();
        }
    }

    private void doSearch() {
        String keyword = tfKeyword.getText().trim();
        String type = (String) cbType.getSelectedItem();
        java.util.List<Character> results = controller.searchCharacters(keyword, type);
        tableModel.setCharacters(results);
    }

    /**
     * 显示人物详情的逻辑
     */
    public void showDetail(int charId) {
        Character full = controller.getCharacterDetails(charId);
        if (full != null) {
            DetailDialog dialog = new DetailDialog(this, full);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "未找到该人物的详细信息", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }
}