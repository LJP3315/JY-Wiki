package com.jycms.view;

import com.jycms.controller.SystemController;
import com.jycms.model.Character;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/** 显示 搜索 + 收藏 的主界面 */
public class MainFrame extends JFrame {
    private final SystemController controller;

    /** 搜索关键词输入框 */
    private JTextField tfKeyword;
    /** 搜索类型下拉框 */
    private JComboBox<String> cbType;
    /** 搜索按钮 */
    private JButton btnSearch;

    /** 加入收藏按钮 */
    private JButton btnAddToFav;
    /** 展示收藏夹 and 返回搜索 */
    private JToggleButton tbtnShowFav;

    private JTable table;
    private CharacterTableModel tableModel;

    public MainFrame(SystemController controller) {
        this.controller = controller;
        // 初始化 UI
        initUI();
        doSearch(); // 启动时自动加载一次数据
    }

    private void initUI() {
        setupFrameSettings();

        JPanel topPanel = createTopPanel();
        // 将顶部栏添加到主界面的 north 方位
        add(topPanel, BorderLayout.NORTH);

        JScrollPane centerScrollPane = createCenterTableArea();
        // 将下拉框居中
        add(centerScrollPane, BorderLayout.CENTER);

        setupEventListeners();
    }

    /** 设置主窗口的基本属性 */
    private void setupFrameSettings() {
        setTitle("金庸小说人物信息管理系统");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /** 创建顶部工具栏 */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // 对组件进行初始化
        tfKeyword = new JTextField(20);
        cbType = new JComboBox<>(new String[]{"人物名称", "小说名称"});
        btnSearch = new JButton("搜索");
        btnAddToFav = new JButton("加入收藏");
        tbtnShowFav = new JToggleButton("我的收藏");

        // 将组件添加到顶部面板
        topPanel.add(new JLabel("关键字:"));
        topPanel.add(tfKeyword);
        topPanel.add(new JLabel("类型:"));
        topPanel.add(cbType);
        topPanel.add(btnSearch);
        topPanel.add(btnAddToFav);
        topPanel.add(tbtnShowFav);

        return topPanel;
    }

    /** 创建滚动框 */
    private JScrollPane createCenterTableArea() {
        // 创建空的角色表，tableModel用来存数据，table用来画表格
        tableModel = new CharacterTableModel(new ArrayList<Character>());
        table = new JTable(tableModel);

        // 设置列宽
        try {
            table.getColumnModel().getColumn(0).setPreferredWidth(50);
            table.getColumnModel().getColumn(3).setPreferredWidth(300);
        } catch (ArrayIndexOutOfBoundsException e) {
            // 忽略：如果列数与预期不符
        }

        return new JScrollPane(table);
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

    /** 为可操作组件添加事件监听 */
    private void setupEventListeners() {
        SearchActionListener searchListener = new SearchActionListener();
        btnSearch.addActionListener(searchListener);
        tfKeyword.addActionListener(searchListener);

        // 鼠标点击监听器
        table.addMouseListener(new MouseAdapter() {
            @Override
            // 点击数等于2，获取当前行号
            // 获取当前行对应的人物
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row < 0) return;

                    // 将 table 中的行号转换为 tableModel 中的行号
                    int modelRow = table.convertRowIndexToModel(row);
                    // 通过 tableModel 中的行号锁定人物
                    Character c = tableModel.getCharacterAt(modelRow);
                    // 显示人物的详情界面
                    if (c != null) showDetail(c.getId());
                }
            }
        });

        // 收藏按钮的事件监听器
        btnAddToFav.addActionListener(e -> {
            // 先确保用户选中对象
            int row = table.getSelectedRow();
            // 为选中对象，弹出提示框
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "请先选择一个人物！");
                return;
            }
            // 将table转为tableModel.
            int modelRow = table.convertRowIndexToModel(row);
            // 在tableModel中找到指定人物.
            Character c = tableModel.getCharacterAt(modelRow);

            // 将选中人物的id添加到收藏.
            boolean success = controller.addToCollection(c.getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "成功加入收藏！");
            } else {
                JOptionPane.showMessageDialog(this, "该人物已在收藏夹中或操作失败。");
            }
        });

        // 显示按钮的事件监听器，下拉框有两个监听器
        tbtnShowFav.addActionListener(e -> {
            // 当选中显示收藏时，对其他按钮进行失活
            if (tbtnShowFav.isSelected()) {
                // 状态：按下 -> 显示收藏列表
                tbtnShowFav.setText("返回搜索");
                // 失活 搜索按钮 + 搜索文本框 + 加入收藏按钮
                btnSearch.setEnabled(false);
                tfKeyword.setEnabled(false);
                btnAddToFav.setEnabled(false); // 在收藏模式下禁用“加入收藏”

                // 加载收藏数据
                List<Character> favs = controller.getCollection();
                tableModel.setCharacters(favs);
            } else {
                // 未选择，返回原样
                tbtnShowFav.setText("我的收藏");
                btnSearch.setEnabled(true);
                tfKeyword.setEnabled(true);
                btnAddToFav.setEnabled(true);

                // 恢复之前的搜索结果
                doSearch();
            }
        });
    }

    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 确保在搜索模式下才能执行搜索
            if (!tbtnShowFav.isSelected()) {
                doSearch();
            }
        }
    }
}