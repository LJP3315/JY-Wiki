package com.jycms.view;

import com.jycms.model.Character;
import com.jycms.model.CharacterArt;
import com.jycms.model.CharacterRelation; // 【新增】导入关系实体

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * 人物详细信息弹窗：显示图片、全描述、武功列表和人物关系。
 */
public class DetailDialog extends JDialog {
    private Character character;

    public DetailDialog(Frame owner, Character character) {
        super(owner, "人物详情 - " + character.getName(), true);
        this.character = character;
        initUI();
    }

    private void initUI() {
        // 增大对话框尺寸，为关系列表腾出空间
        setSize(750, 600);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        // 顶部：姓名 & 小说
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblTitle = new JLabel(character.getName() + "  —  " + character.getNovelName());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        topPanel.add(lblTitle);
        add(topPanel, BorderLayout.NORTH);

        // 中间：左侧图片，右侧详情 (BorderLayout)
        JPanel center = new JPanel(new BorderLayout());

        // ----------------------------------------------------
        // 左侧：图片区域 (WEST)
        // ----------------------------------------------------
        JPanel left = new JPanel(new BorderLayout());
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        lblImage.setVerticalAlignment(JLabel.CENTER);

        File imgFile = new File(character.getImageUrl());
        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(character.getImageUrl());
            Image image = icon.getImage().getScaledInstance(200, 250, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(image));
        } else {
            lblImage.setText("图片加载失败：" + character.getImageUrl());
        }

        left.add(lblImage, BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(220, 0));
        center.add(left, BorderLayout.WEST);

        // ----------------------------------------------------
        // 右侧：详情区域 (CENTER)
        // ----------------------------------------------------
        // 使用 BoxLayout 垂直堆叠 人物描述、武功区、关系区
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // --- 1. 人物全描述 ---
        JTextArea taDesc = new JTextArea(character.getDescriptionFull());
        taDesc.setEditable(false);
        taDesc.setLineWrap(true);
        taDesc.setWrapStyleWord(true);
        JScrollPane spDesc = new JScrollPane(taDesc);
        spDesc.setBorder(BorderFactory.createTitledBorder("人物详细描述"));
        spDesc.setPreferredSize(new Dimension(500, 150));
        spDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        right.add(spDesc);
        right.add(Box.createVerticalStrut(5)); // 垂直间距

        // --- 2. 武功列表区 (包含武功列表和武功描述) ---
        JPanel artsPanel = new JPanel(new BorderLayout(5, 5));

        // 武功列表 (CENTER)
        List<CharacterArt> arts = character.getMartialArts();
        DefaultListModel<String> artsModel = new DefaultListModel<>();
        for (CharacterArt ca : arts) {
            // 列表显示：[武功名称]
            artsModel.addElement(ca.getArtName());
        }
        JList<String> listArts = new JList<>(artsModel);
        listArts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spArts = new JScrollPane(listArts);
        spArts.setBorder(BorderFactory.createTitledBorder("关联武功"));
        artsPanel.add(spArts, BorderLayout.CENTER);

        // 武功描述 (SOUTH)
        JTextArea taArtDesc = new JTextArea(1, 40); // 1行高
        taArtDesc.setEditable(false);
        taArtDesc.setLineWrap(true);
        taArtDesc.setWrapStyleWord(true);
        JScrollPane spArtDesc = new JScrollPane(taArtDesc);
        spArtDesc.setBorder(BorderFactory.createTitledBorder("武功描述"));
        spArtDesc.setPreferredSize(new Dimension(450, 45));
        artsPanel.add(spArtDesc, BorderLayout.SOUTH);

        // 武功列表监听器 (用于显示武功描述)
        listArts.addListSelectionListener(e -> {
            int idx = listArts.getSelectedIndex();
            if (idx >= 0 && idx < arts.size()) {
                CharacterArt ca = arts.get(idx);
                taArtDesc.setText(ca.getArtDescription());
                taArtDesc.setCaretPosition(0);
            } else {
                taArtDesc.setText("");
            }
        });

        // 固定武功区尺寸
        artsPanel.setPreferredSize(new Dimension(500, 200));
        artsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        right.add(artsPanel);
        right.add(Box.createVerticalStrut(5)); // 垂直间距

        // --- 3. 【新增】角色关系区 ---
        List<CharacterRelation> relations = character.getRelations();
        DefaultListModel<String> relationsModel = new DefaultListModel<>();
        for (CharacterRelation cr : relations) {
            // 列表显示：[关系类型]：[关联人物姓名]
            relationsModel.addElement(cr.getRelationType() + "：" + cr.getRelatedCharName());
        }
        JList<String> listRelations = new JList<>(relationsModel);
        listRelations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spRelations = new JScrollPane(listRelations);
        spRelations.setBorder(BorderFactory.createTitledBorder("人物关系"));
        spRelations.setPreferredSize(new Dimension(500, 100));
        spRelations.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        right.add(spRelations);

        // 关系描述 (可选：可以像武功描述一样，在选中时显示 description 字段，但这里为了紧凑暂时不实现)

        center.add(right, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

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