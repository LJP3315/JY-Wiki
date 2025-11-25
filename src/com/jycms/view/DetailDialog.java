package com.jycms.view;

import com.jycms.model.Character;
import com.jycms.model.MartialArt;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * 人物详细信息弹窗：显示图片、全描述、以及武功列表
 */
public class DetailDialog extends JDialog {
    private Character character;

    public DetailDialog(Frame owner, Character character) {
        super(owner, "人物详情 - " + character.getName(), true);
        this.character = character;
        initUI();
    }

    private void initUI() {
        setSize(700, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        // 顶部：姓名 & 小说
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel(character.getName() + "  —  " + character.getNovelName());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        topPanel.add(lblTitle, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 中间：左侧图片，右侧详情
        JPanel center = new JPanel(new BorderLayout());
        JPanel left = new JPanel(new BorderLayout());
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        lblImage.setVerticalAlignment(JLabel.CENTER);

        // 加载图片（若路径为空或加载失败则显示占位）
        String imagePath = character.getImageUrl(); // 例如: images/guojing.jpg
        ImageIcon icon = null;

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                // --- 优化点：首先尝试作为 ClassPath 资源加载 ---
                // ClassLoader.getSystemResource() 适用于从 JAR 或 Class 目录加载资源
                java.net.URL imageUrl = DetailDialog.class.getClassLoader().getResource(imagePath);

                if (imageUrl != null) {
                    icon = new ImageIcon(imageUrl);
                } else {
                    // --- 备用方案：尝试作为文件系统路径加载（以兼容旧逻辑） ---
                    File f = new File(imagePath);
                    if (f.exists()) {
                        icon = new ImageIcon(imagePath);
                    }
                }
            } catch (Exception ex) {
                // 如果 ClassPath 资源加载失败或文件系统加载失败
                icon = null;
                System.err.println("图片加载失败: " + imagePath + ", 错误: " + ex.getMessage());
            }
        }

        if (icon != null && icon.getIconWidth() > 0) {
            // 缩放图片以适应区域
            Image img = icon.getImage();
            // 220, 300 是一个示例尺寸，可以根据您的实际图片和界面调整
            Image scaled = img.getScaledInstance(220, 300, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(scaled));
        } else {
            // 如果图片加载失败，显示一个提示或默认占位图片
            lblImage.setText("<图片加载失败或不存在>");
        }

        if (icon != null) {
            // 缩放图片以适应区域
            Image img = icon.getImage();
            Image scaled = img.getScaledInstance(220, 300, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(scaled));
        } else {
            lblImage.setText("<无图片>");
        }
        left.add(lblImage, BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(240, 300));
        center.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout());
        JTextArea taFull = new JTextArea();
        taFull.setEditable(false);
        taFull.setLineWrap(true);
        taFull.setWrapStyleWord(true);
        taFull.setText(character.getDescriptionFull() == null ? "" : character.getDescriptionFull());
        JScrollPane spDesc = new JScrollPane(taFull);
        spDesc.setBorder(BorderFactory.createTitledBorder("人物全描述"));
        spDesc.setPreferredSize(new Dimension(420, 200));
        right.add(spDesc, BorderLayout.NORTH);

        // 武功列表
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (MartialArt ma : character.getMartialArts()) {
            listModel.addElement(ma.getArtName());
        }
        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spArts = new JScrollPane(list);
        spArts.setBorder(BorderFactory.createTitledBorder("关联武功（点击查看描述）"));
        spArts.setPreferredSize(new Dimension(420, 130));
        right.add(spArts, BorderLayout.CENTER);

        // 选中武功时显示描述
        JTextArea taArtDesc = new JTextArea();
        taArtDesc.setEditable(false);
        taArtDesc.setLineWrap(true);
        taArtDesc.setWrapStyleWord(true);
        JScrollPane spArtDesc = new JScrollPane(taArtDesc);
        spArtDesc.setBorder(BorderFactory.createTitledBorder("武功描述"));
        spArtDesc.setPreferredSize(new Dimension(420, 120));
        right.add(spArtDesc, BorderLayout.SOUTH);

        list.addListSelectionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx >= 0 && idx < character.getMartialArts().size()) {
                MartialArt ma = character.getMartialArts().get(idx);
                taArtDesc.setText(ma.getArtDescription());
            } else {
                taArtDesc.setText("");
            }
        });

        center.add(right, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        // 底部：关闭按钮
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("关闭");
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);
    }
}
