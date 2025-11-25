package com.jycms.view;

import com.jycms.model.Character;
import com.jycms.model.MartialArt;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class DetailDialog extends JDialog {
    private final Character character;
    private final JTextArea taArtDesc;

    public DetailDialog(Frame owner, Character character) {
        super(owner, "人物详情 - " + character.getName(), true);
        this.character = character;
        this.taArtDesc = new JTextArea();
        initUI();
    }

    private void initUI() {
        setSize(700, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));

        // 1. 顶部：姓名 & 小说
        JLabel lblTitle = new JLabel(character.getName() + "  —  " + character.getNovelName());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // 2. 中间：左侧图片，右侧详情
        JPanel center = new JPanel(new BorderLayout(10, 0));

        // 2.1 左侧图片
        JPanel left = new JPanel(new BorderLayout());
        JLabel lblImage = createScaledImageLabel();
        left.add(lblImage, BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(240, 300));
        center.add(left, BorderLayout.WEST);

        // 2.2 右侧详情
        JPanel right = new JPanel(new BorderLayout(0, 10));

        // 人物全描述
        JTextArea taFull = new JTextArea();
        taFull.setEditable(false);
        taFull.setLineWrap(true);
        taFull.setWrapStyleWord(true);
        taFull.setText(character.getDescriptionFull() == null ? "暂无完整描述。" : character.getDescriptionFull());
        JScrollPane spDesc = new JScrollPane(taFull);
        spDesc.setBorder(BorderFactory.createTitledBorder("人物全描述"));
        right.add(spDesc, BorderLayout.NORTH);

        // 武功列表
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (MartialArt ma : character.getMartialArts()) {
            listModel.addElement(ma.getArtName());
        }
        final JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spArts = new JScrollPane(list);
        spArts.setBorder(BorderFactory.createTitledBorder("关联武功（点击查看描述）"));
        right.add(spArts, BorderLayout.CENTER);

        // 武功描述
        taArtDesc.setEditable(false);
        taArtDesc.setLineWrap(true);
        taArtDesc.setWrapStyleWord(true);
        JScrollPane spArtDesc = new JScrollPane(taArtDesc);
        spArtDesc.setBorder(BorderFactory.createTitledBorder("武功描述"));
        right.add(spArtDesc, BorderLayout.SOUTH);

        // 使用匿名内部类替代 Lambda 表达式处理列表选择事件
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int idx = list.getSelectedIndex();
                    if (idx >= 0 && idx < character.getMartialArts().size()) {
                        MartialArt ma = character.getMartialArts().get(idx);
                        taArtDesc.setText(ma.getArtDescription());
                    } else {
                        taArtDesc.setText("");
                    }
                }
            }
        });

        center.add(right, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // 3. 底部：关闭按钮
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("关闭");
        // 使用匿名内部类替代 Lambda 表达式处理关闭按钮事件
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);
    }

    // 辅助方法：创建并加载缩放后的图片标签
    private JLabel createScaledImageLabel() {
        // ... (图片加载逻辑与之前一致，此处省略以节省空间)
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        lblImage.setVerticalAlignment(JLabel.CENTER);

        String imagePath = character.getImageUrl();
        ImageIcon icon = null;

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            try {
                // 尝试作为 ClassPath 资源加载
                java.net.URL imageUrl = DetailDialog.class.getClassLoader().getResource(imagePath);
                if (imageUrl != null) {
                    icon = new ImageIcon(imageUrl);
                } else {
                    // 备用方案：尝试作为文件系统路径加载
                    File f = new File(imagePath);
                    if (f.exists()) {
                        icon = new ImageIcon(imagePath);
                    }
                }
            } catch (Exception ex) {
                System.err.println("图片加载失败: " + imagePath + ", 错误: " + ex.getMessage());
            }
        }

        if (icon != null && icon.getIconWidth() > 0) {
            Image img = icon.getImage();
            Image scaled = img.getScaledInstance(220, 300, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(scaled));
        } else {
            lblImage.setText("<无图片>");
        }
        return lblImage;
    }
}