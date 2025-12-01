package com.jycms.view;

import com.jycms.controller.SystemController;
import com.jycms.model.Character;
import com.jycms.view.table.CharacterTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

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

    // --- UI Initialization Methods ---

    /**
     * 初始化主窗口界面，按照层次进行组织。
     */
    private void initUI() {
        // 1. 窗口基础设置
        setupFrameSettings();

        // 2. 顶部面板（搜索区域）
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 3. 中央面板（数据表格区域）
        JScrollPane centerScrollPane = createCenterTableArea();
        add(centerScrollPane, BorderLayout.CENTER);

        // 4. 绑定事件监听器
        setupEventListeners();
    }

    /**
     * 设置主窗口的基本属性（标题、大小、关闭操作等）。
     */
    private void setupFrameSettings() {
        setTitle("Jinyong Character Management System (JY-CMS)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 创建并配置顶部的搜索面板。
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        tfKeyword = new JTextField(30);
        cbType = new JComboBox<>(new String[]{"人物名称", "小说名称"});
        btnSearch = new JButton("搜索");

        topPanel.add(new JLabel("关键字:"));
        topPanel.add(tfKeyword);
        topPanel.add(new JLabel("搜索类型:"));
        topPanel.add(cbType);
        topPanel.add(btnSearch);

        return topPanel;
    }

    /**
     * 创建并配置中央的 JTable 区域。
     * 简化：不再配置操作列。
     * @return 包含 JTable 的 JScrollPane
     */
    private JScrollPane createCenterTableArea() {
        // 1. 创建 TableModel 和 JTable
        tableModel = new CharacterTableModel(new ArrayList<Character>());
        table = new JTable(tableModel);

        // 可选：调整列宽以适应内容
        table.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        table.getColumnModel().getColumn(3).setPreferredWidth(300); // 简介

        // 2. 移除操作列配置 (ButtonRenderer/ButtonEditor)

        // 3. 添加到滚动面板
        return new JScrollPane(table);
    }

    /**
     * 设置所有事件监听器（搜索按钮、关键词回车、表格双击等）。
     */
    private void setupEventListeners() {
        // 搜索事件 (命名内部类)
        SearchActionListener searchListener = new SearchActionListener();
        btnSearch.addActionListener(searchListener);
        tfKeyword.addActionListener(searchListener); // 关键词回车也触发搜索

        // 双击行查看详情
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    // 将视图行索引转换为模型行索引，以防排序
                    int modelRow = table.convertRowIndexToModel(row);
                    if (modelRow >= 0) {
                        Character c = tableModel.getCharacterAt(modelRow);
                        if (c != null) {
                            showDetail(c.getId());
                        }
                    }
                }
            }
        });
    }

    // --- Event and Business Logic Methods ---

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
        List<Character> results = controller.searchCharacters(keyword, type);
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
            JOptionPane.showMessageDialog(this,
                    "未找到该人物的详细信息",
                    "提示",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}