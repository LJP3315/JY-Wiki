package com.jycms.view;

import com.jycms.model.Character;
import com.jycms.model.CharacterArt;
import com.jycms.model.CharacterRelation;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * 人物详细信息弹窗：显示图片、全描述、武功列表和人物关系。
 * 优化：缩减武功列表，增大关系描述区域。
 */
public class DetailDialog extends JDialog {
    private Character character;

    public DetailDialog(Frame owner, Character character) {
        super(owner, "人物详情 - " + character.getName(), true);
        this.character = character;
        initUI();
    }

    private void initUI() {
        // 维持合适的窗口尺寸
        setSize(750, 650);
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

        // 左侧：图片区域 (WEST)
        JPanel left = new JPanel(new BorderLayout());
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        lblImage.setVerticalAlignment(JLabel.CENTER);

        File imgFile = new File(character.getImageUrl());
        if (imgFile.exists()) {
            ImageIcon icon = new ImageIcon(character.getImageUrl());
            Image srcImg = icon.getImage();

            // 目标显示宽度（可调）
            int targetWidth = 240;

            // 按 3:4 比例计算高度（600:800）
            int targetHeight = targetWidth * 4 / 3;

            // 使用平滑缩放
            Image scaledImg = srcImg.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(scaledImg));
        } else {
            lblImage.setText("图片加载失败：" + character.getImageUrl());
        }


        left.add(lblImage, BorderLayout.CENTER);
        left.setPreferredSize(new Dimension(260, 0));
        center.add(left, BorderLayout.WEST);

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
        right.add(Box.createVerticalStrut(5));

        // --- 2. 武功列表区 (缩减高度) ---
        JPanel artsPanel = new JPanel(new BorderLayout(5, 5));

        // 武功列表
        List<CharacterArt> arts = character.getMartialArts();
        DefaultListModel<String> artsModel = new DefaultListModel<>();
        for (CharacterArt ca : arts) {
            artsModel.addElement(ca.getArtName());
        }
        JList<String> listArts = new JList<>(artsModel);
        listArts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spArts = new JScrollPane(listArts);
        spArts.setBorder(BorderFactory.createTitledBorder("关联武功"));
        artsPanel.add(spArts, BorderLayout.CENTER);

        // 武功描述 (1行高)
        JTextArea taArtDesc = new JTextArea(1, 40);
        taArtDesc.setEditable(false);
        taArtDesc.setLineWrap(true);
        taArtDesc.setWrapStyleWord(true);
        JScrollPane spArtDesc = new JScrollPane(taArtDesc);
        spArtDesc.setBorder(BorderFactory.createTitledBorder("武功描述"));
        spArtDesc.setPreferredSize(new Dimension(450, 45));
        artsPanel.add(spArtDesc, BorderLayout.SOUTH);

        // **【修改】缩减 artsPanel 的总高度至 130**
        artsPanel.setPreferredSize(new Dimension(500, 130));
        artsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        right.add(artsPanel);
        right.add(Box.createVerticalStrut(5));

        // --- 3. 角色关系区 (列表) ---
        List<CharacterRelation> relations = character.getRelations();
        DefaultListModel<String> relationsModel = new DefaultListModel<>();
        for (CharacterRelation cr : relations) {
            relationsModel.addElement(cr.getRelationType() + "：" + cr.getRelatedCharName());
        }
        JList<String> listRelations = new JList<>(relationsModel);
        listRelations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spRelations = new JScrollPane(listRelations);
        spRelations.setBorder(BorderFactory.createTitledBorder("人物关系"));
        spRelations.setPreferredSize(new Dimension(500, 100));
        spRelations.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        right.add(spRelations);
        right.add(Box.createVerticalStrut(5));

        // --- 4. 关系描述区域 (增大高度) ---
        JTextArea taRelationDesc = new JTextArea(4, 40); // 增大到 4 行高
        taRelationDesc.setEditable(false);
        taRelationDesc.setLineWrap(true);
        taRelationDesc.setWrapStyleWord(true);
        JScrollPane spRelationDesc = new JScrollPane(taRelationDesc);
        spRelationDesc.setBorder(BorderFactory.createTitledBorder("关系详情"));

        // **【修改】增大关系描述区域的高度至 100**
        spRelationDesc.setPreferredSize(new Dimension(500, 100));
        spRelationDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        right.add(spRelationDesc);

        // 武功列表监听器 (不变)
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

        // 关系列表监听器 (不变)
        listRelations.addListSelectionListener(e -> {
            int idx = listRelations.getSelectedIndex();
            if (idx >= 0 && idx < relations.size()) {
                CharacterRelation cr = relations.get(idx);
                taRelationDesc.setText(cr.getDescription());
                taRelationDesc.setCaretPosition(0);
            } else {
                taRelationDesc.setText("");
            }
        });

        center.add(right, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("关闭");
        btnClose.addActionListener(e -> dispose());
        bottom.add(btnClose);
        add(bottom, BorderLayout.SOUTH);
    }
}