package com.jycms.view;

import com.jycms.controller.SystemController;
import com.jycms.model.Character;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/** * 显示 搜索 + 收藏 的主界面
 * 集成：搜索、详情（双击）、加入收藏、切换收藏夹、移除收藏（新按钮）
 */
public class MainFrame extends JFrame {
    private final SystemController controller;

    // 搜索组件
    private JTextField tfKeyword;
    private JComboBox<String> cbType;
    private JButton btnSearch;

    // 收藏管理组件
    private JButton btnAddToFav;
    private JToggleButton tbtnShowFav;
    // 【新增】移除收藏按钮，仅在收藏模式下显示
    private JButton btnRemoveFav;

    private JTable table;
    private CharacterTableModel tableModel;

    private boolean isCollectionMode = false;

    public MainFrame(SystemController controller) {
        this.controller = controller;
        initUI();
        doSearch(); // 启动时自动加载一次数据
    }

    private void initUI() {
        setupFrameSettings();

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JScrollPane centerScrollPane = createCenterTableArea();
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
        // 【新增】初始化移除收藏按钮
        btnRemoveFav = new JButton("移除收藏");
        btnRemoveFav.setEnabled(false); // 默认禁用/隐藏

        // 将组件添加到顶部面板
        topPanel.add(new JLabel("关键字:"));
        topPanel.add(tfKeyword);
        topPanel.add(new JLabel("类型:"));
        topPanel.add(cbType);
        topPanel.add(btnSearch);

        // 收藏管理按钮组
        topPanel.add(btnAddToFav);
        topPanel.add(tbtnShowFav);
        topPanel.add(btnRemoveFav); // 【新增】加入移除按钮

        return topPanel;
    }

    /** 创建滚动框 */
    private JScrollPane createCenterTableArea() {
        tableModel = new CharacterTableModel(new ArrayList<Character>());
        table = new JTable(tableModel);

        // 设置列宽
        try {
            table.getColumnModel().getColumn(0).setPreferredWidth(50);
            table.getColumnModel().getColumn(3).setPreferredWidth(300);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Warning: Table model column count mismatch.");
        }

        return new JScrollPane(table);
    }

    // --- 业务逻辑方法 ---

    private void doSearch() {
        String keyword = tfKeyword.getText().trim();
        String type = (String) cbType.getSelectedItem();
        List<Character> results = controller.searchCharacters(keyword, type);
        tableModel.setCharacters(results);
    }

    private void loadCollectionData() {
        List<Character> favs = controller.getCollection();
        if (favs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "收藏夹中还没有人物。", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
        tableModel.setCharacters(favs);
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

    /** 移除收藏的业务逻辑 */
    private void onRemoveFromCollection() {
        // 1. 确保在收藏模式下
        if (!isCollectionMode) return;

        // 2. 确保选中了一行
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请选择要移除的人物！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. 获取人物ID和姓名
        int modelRow = table.convertRowIndexToModel(row);
        Character c = tableModel.getCharacterAt(modelRow);

        if (c != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "确定要将人物 [" + c.getName() + "] 从收藏夹中移除吗？",
                    "确认移除", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                if (controller.removeFromCollection(c.getId())) {
                    JOptionPane.showMessageDialog(this, "成功移除收藏！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    loadCollectionData(); // 刷新收藏夹视图
                } else {
                    JOptionPane.showMessageDialog(this, "移除失败，请检查数据库连接或日志。", "失败", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    /** 为可操作组件添加事件监听 */
    private void setupEventListeners() {
        SearchActionListener searchListener = new SearchActionListener();
        btnSearch.addActionListener(searchListener);
        tfKeyword.addActionListener(searchListener);

        // 双击查看详情监听器：仅在搜索模式下有效
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 双击且处于非收藏模式（即搜索模式）
                if (e.getClickCount() == 2 && !isCollectionMode) {
                    int row = table.getSelectedRow();
                    if (row < 0) return;

                    int modelRow = table.convertRowIndexToModel(row);
                    Character c = tableModel.getCharacterAt(modelRow);
                    if (c != null) showDetail(c.getId());
                }
            }
        });

        // 加入收藏按钮的事件监听器 (C)
        btnAddToFav.addActionListener(e -> {
            // 确保用户选中对象且处于搜索模式
            int row = table.getSelectedRow();
            if (row < 0 || isCollectionMode) {
                JOptionPane.showMessageDialog(this, "请在搜索结果中选择一个人物加入收藏！");
                return;
            }
            int modelRow = table.convertRowIndexToModel(row);
            Character c = tableModel.getCharacterAt(modelRow);

            boolean success = controller.addToCollection(c.getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "成功加入收藏！");
            } else {
                JOptionPane.showMessageDialog(this, "该人物已在收藏夹中或操作失败。");
            }
        });

        // 【新增】移除收藏按钮的事件监听器 (D)
        btnRemoveFav.addActionListener(e -> onRemoveFromCollection());


        // 切换视图按钮的事件监听器 (R / 视图切换)
        tbtnShowFav.addActionListener(e -> {
            isCollectionMode = tbtnShowFav.isSelected(); // 更新状态标志

            if (isCollectionMode) {
                // 状态：按下 -> 显示收藏列表
                tbtnShowFav.setText("返回搜索");

                // 切换按钮状态：禁用搜索，启用移除收藏
                btnSearch.setEnabled(false);
                tfKeyword.setEnabled(false);
                cbType.setEnabled(false);
                btnAddToFav.setEnabled(false);
                btnRemoveFav.setEnabled(true); // 【关键】启用移除按钮

                loadCollectionData(); // 加载收藏数据
            } else {
                // 状态：未选择 -> 返回搜索界面
                tbtnShowFav.setText("我的收藏");

                // 切换按钮状态：启用搜索，禁用移除收藏
                btnSearch.setEnabled(true);
                tfKeyword.setEnabled(true);
                cbType.setEnabled(true);
                btnAddToFav.setEnabled(true);
                btnRemoveFav.setEnabled(false); // 【关键】禁用移除按钮

                doSearch(); // 恢复之前的搜索结果
            }
            table.repaint();
        });
    }

    private class SearchActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isCollectionMode) {
                doSearch();
            }
        }
    }
}