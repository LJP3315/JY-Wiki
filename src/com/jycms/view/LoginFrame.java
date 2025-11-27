package com.jycms.view;

import com.jycms.controller.SystemController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private final SystemController controller;

    // 用户名文本框
    private JTextField tfUser;
    // 密码文本框
    private JPasswordField pfPass;
    // 登录按钮
    private JButton btnLogin;

    // 构造函数，controller 用于通过窗口的按钮调用 Model 层的方法
    public LoginFrame(SystemController controller) {
        this.controller = controller;
        initUI();
    }

    private void initUI() {
        setTitle("欢迎使用 JY-Wiki");
        setSize(360, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(3, 1));

        // 用户名行
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblUser = new JLabel("用户名：");
        tfUser = new JTextField(18);
        userPanel.add(lblUser);
        userPanel.add(tfUser);

        // 密码行
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblPass = new JLabel("密  码：");
        pfPass = new JPasswordField(18);
        passwordPanel.add(lblPass);
        passwordPanel.add(pfPass);

        //按钮行
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnLogin = new JButton("登录");
        btnLogin.addActionListener(new LoginActionListener());
        buttonPanel.add(btnLogin);

        mainPanel.add(userPanel);
        mainPanel.add(passwordPanel);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
        // 回车登录
        getRootPane().setDefaultButton(btnLogin);
    }

    // 登录按钮的事件监听器
    private class LoginActionListener implements ActionListener {
        // actionPerformed 方法封装了点击按钮后执行的系列操作
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = tfUser.getText().trim();
            String password = new String(pfPass.getPassword());

            // 判断两个文本框是否为空
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "请输入用户名和密码",
                        "提示",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 通过 controller
            // 调用检测登录的方法
            boolean ok = controller.login(username, password);

            if (ok) {
                // 如果登录成功，打开一个主界面
                // 同时关闭登录界面
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        MainFrame main = new MainFrame(controller);
                        main.setVisible(true);
                    }
                });
                dispose();
            } else {
                // 登录失败，弹出警告弹出框
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "用户名或密码错误",
                        "登录失败",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}