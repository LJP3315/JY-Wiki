package com.jycms.view.table;

import com.jycms.controller.SystemController;
import com.jycms.model.Character;
import com.jycms.view.DetailDialog;
import com.jycms.view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private String label;
    private int selectedRow;
    private final JTable table;
    private final SystemController controller;
    private final JFrame ownerFrame;

    public ButtonEditor(JTable table, SystemController controller, JFrame ownerFrame) {
        super(new JCheckBox());
        this.table = table;
        this.controller = controller;
        this.ownerFrame = ownerFrame;

        button = new JButton();
        button.setOpaque(true);

        // 使用匿名内部类替代 Lambda 表达式
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 停止编辑
                fireEditingStopped();
                // 执行按钮点击逻辑
                handleButtonClick();
            }
        });
    }

    // 按钮点击后执行的逻辑
    private void handleButtonClick() {
        CharacterTableModel model = (CharacterTableModel) table.getModel();
        Character c = model.getCharacterAt(selectedRow);

        if (c != null) {
            showDetail(c.getId());
        }
    }

    // 显示详情的逻辑
    private void showDetail(int charId) {
        Character full = controller.getCharacterDetails(charId);
        if (full != null) {
            DetailDialog dialog = new DetailDialog(ownerFrame, full);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(ownerFrame, "未找到该人物的详细信息", "提示", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        selectedRow = row;
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }
}