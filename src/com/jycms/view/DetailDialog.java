package com.jycms.view;

import com.jycms.model.Character;
import com.jycms.model.CharacterArt;
// import com.jycms.model.MartialArt; // 不再需要

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * 人物详细信息弹窗：显示图片、全描述、以及武功列表
 * 最终要求：恢复紧凑布局，武功全部显示，武功描述1行高。
 */
public class DetailDialog extends JDialog {
    private Character character;

    public DetailDialog(Frame owner, Character character) {
        super(owner, "人物详情 - " + character.getName(), true);
        this.character = character;
        initUI();
    }

    private void initUI() {
        // 恢复到最初的紧凑尺寸
        setSize(700, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        // 顶部：姓名 & 小说
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblTitle = new JLabel(character.getName() + "  —  " + character.getNovelName());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        topPanel.add(lblTitle);
        add(topPanel, BorderLayout.NORTH);

        // 中间：左侧图片，右侧详情
        JPanel center = new JPanel(new BorderLayout());

        // ----------------------------------------------------
        // 左侧：图片区域 (保持 200x250 左右)
        // ----------------------------------------------------
        JPanel left = new JPanel(new BorderLayout());
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        lblImage.setVerticalAlignment(JLabel.CENTER);

        // 尝试加载图片
        File imgFile = new File(character.getImageUrl());
        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(character.getImageUrl());
            Image image = icon.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(image));
        } else {
            lblImage.setText("图片加载失败：" + character.getImageUrl());
        }

        left.add(lblImage, BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(220, 0)); // 固定左侧宽度
        center.add(left, BorderLayout.WEST);

        // ----------------------------------------------------
        // 右侧：人物描述 + 武功列表/描述 (使用 BorderLayout 分割，恢复最初的上下布局)
        // ----------------------------------------------------
        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // --- 1. 人物全描述 (恢复为中间区域的 NORTH 部分) ---
        JTextArea taDesc = new JTextArea(character.getDescriptionFull());
        taDesc.setEditable(false);
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);
        JScrollPane spDesc = new JScrollPane(taDesc);
        spDesc.setBorder(BorderFactory.createTitledBorder("人物详细描述"));
        // 恢复较小的首选尺寸
        spDesc.setPreferredSize(new Dimension(450, 150));
        right.add(spDesc, BorderLayout.NORTH);

        // --- 2. 武功列表 (恢复为中间区域的 CENTER 部分) ---
        List<CharacterArt> arts = character.getMartialArts();
        DefaultListModel<String> artsModel = new DefaultListModel<>();
        for (CharacterArt ca : arts) {
            artsModel.addElement(ca.getArtName());
        }
        JList<String> list = new JList<>(artsModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // 关键设置：取消 VisibleRowCount 限制，显示全部
        // list.setVisibleRowCount(-1); // 默认行为

        JScrollPane spArts = new JScrollPane(list);
        spArts.setBorder(BorderFactory.createTitledBorder("关联武功（点击查看描述）"));
        // 恢复尺寸
        spArts.setPreferredSize(new Dimension(450, 150));
        right.add(spArts, BorderLayout.CENTER);

        // --- 3. 武功描述 (恢复为中间区域的 SOUTH 部分) ---
        JTextArea taArtDesc = new JTextArea(1, 40); // 关键设置：1 行高
        taArtDesc.setEditable(false);
        taArtDesc.setLineWrap(true);
        taArtDesc.setWrapStyleWord(true);

        JScrollPane spArtDesc = new JScrollPane(taArtDesc);
        spArtDesc.setBorder(BorderFactory.createTitledBorder("武功描述"));
        // 调整尺寸以匹配 1 行高
        spArtDesc.setPreferredSize(new Dimension(450, 45));
        right.add(spArtDesc, BorderLayout.SOUTH);

        // 添加右侧面板到中心
        center.add(right, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // ----------------------------------------------------
        // 列表选择监听器 (不变)
        // ----------------------------------------------------
        list.addListSelectionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx >= 0 && idx < arts.size()) {
                CharacterArt ca = arts.get(idx);
                taArtDesc.setText(ca.getArtDescription());
                taArtDesc.setCaretPosition(0);
            } else {
                taArtDesc.setText("");
            }
        });

        // ----------------------------------------------------
        // 底部：关闭按钮
        // ----------------------------------------------------
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("关闭");
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);
    }
}