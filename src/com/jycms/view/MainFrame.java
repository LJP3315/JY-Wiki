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

    // --- 新增 UI 组件 ---
    private JButton btnAddToFav;
    private JToggleButton tbtnShowFav;
    // ------------------

    private JTable table;
    private CharacterTableModel tableModel;

    public MainFrame(SystemController controller) {
        this.controller = controller;
        initUI();
        doSearch();
    }

    private void initUI() {
        setupFrameSettings();

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JScrollPane centerScrollPane = createCenterTableArea();
        add(centerScrollPane, BorderLayout.CENTER);

        setupEventListeners();
    }

    private void setupFrameSettings() {
        setTitle("Jinyong Character Management System (JY-CMS)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        tfKeyword = new JTextField(20);
        cbType = new JComboBox<>(new String[]{"人物名称", "小说名称"});
        btnSearch = new JButton("搜索");

        // --- 新增组件初始化 ---
        btnAddToFav = new JButton("加入收藏");
        tbtnShowFav = new JToggleButton("我的收藏");
        // --------------------

        topPanel.add(new JLabel("关键字:"));
        topPanel.add(tfKeyword);
        topPanel.add(new JLabel("类型:"));
        topPanel.add(cbType);
        topPanel.add(btnSearch);

        // --- 添加到面板 ---
        topPanel.add(new JSeparator(SwingConstants.VERTICAL)); // 可选：视觉分隔
        topPanel.add(btnAddToFav);
        topPanel.add(tbtnShowFav);
        // ----------------

        return topPanel;
    }

    private JScrollPane createCenterTableArea() {
        // (保持之前的简化版本)
        tableModel = new CharacterTableModel(new ArrayList<Character>());
        table = new JTable(tableModel);
        // 简单设置列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(300);
        return new JScrollPane(table);
    }

    private void setupEventListeners() {
        SearchActionListener searchListener = new SearchActionListener();
        btnSearch.addActionListener(searchListener);
        tfKeyword.addActionListener(searchListener);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    int modelRow = table.convertRowIndexToModel(row);
                    if (modelRow >= 0) {
                        Character c = tableModel.getCharacterAt(modelRow);
                        if (c != null) showDetail(c.getId());
                    }
                }
            }
        });

        // --- 新增事件监听器 ---

        // 1. 加入收藏按钮逻辑
        btnAddToFav.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "请先选择一个人物！");
                return;
            }
            // 获取选中人物
            int modelRow = table.convertRowIndexToModel(row);
            Character c = tableModel.getCharacterAt(modelRow);

            // 调用控制器
            boolean success = controller.addToCollection(c.getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "成功加入收藏！");
            } else {
                JOptionPane.showMessageDialog(this, "该人物已在收藏夹中。");
            }
        });

        // 2. 查看收藏切换按钮逻辑
        tbtnShowFav.addActionListener(e -> {
            if (tbtnShowFav.isSelected()) {
                // 状态：按下 -> 显示收藏列表
                tbtnShowFav.setText("返回搜索");
                btnSearch.setEnabled(false);   // 禁用搜索按钮
                tfKeyword.setEnabled(false);   // 禁用输入框
                btnAddToFav.setEnabled(false); // 在收藏列表里禁用“加入收藏”

                // 加载收藏数据
                List<Character> favs = controller.getCollection();
                tableModel.setCharacters(favs);

            } else {
                // 状态：弹起 -> 返回搜索模式
                tbtnShowFav.setText("我的收藏");
                btnSearch.setEnabled(true);
                tfKeyword.setEnabled(true);
                btnAddToFav.setEnabled(true);

                // 恢复之前的搜索结果
                doSearch();
            }
        });
        // --------------------
    }

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