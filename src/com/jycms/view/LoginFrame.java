package com.jycms.view;

import com.jycms.controller.SystemController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 登录窗口
 */
public class LoginFrame extends JFrame {
    private SystemController controller;

    private JTextField tfUser;
    private JPasswordField pfPass;
    private JButton btnLogin;

    public LoginFrame(SystemController controller) {
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        setTitle("JY-CMS 登录");
        setSize(360, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblUser = new JLabel("用户名:");
        JLabel lblPass = new JLabel("密码:");

        tfUser = new JTextField(18);
        pfPass = new JPasswordField(18);

        btnLogin = new JButton("登录");

        gbc.insets = new Insets(6,6,6,6);
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblUser, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(tfUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblPass, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(pfPass, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnLogin, gbc);

        add(panel, BorderLayout.CENTER);

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin();
            }
        });

        // 回车登录
        getRootPane().setDefaultButton(btnLogin);
    }

    private void onLogin() {
        String username = tfUser.getText().trim();
        String password = new String(pfPass.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名和密码", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean ok = controller.login(username, password);
        if (ok) {
            // 登录成功
            SwingUtilities.invokeLater(() -> {
                MainFrame main = new MainFrame(controller);
                main.setVisible(true);
            });
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
        }
    }
}
