package com.jycms;

import com.jycms.controller.SystemController;
import com.jycms.view.LoginFrame;

import javax.swing.*;

public class AppMain {
    public static void main(String[] args) {
        // 确保在事件调度线程 (EDT) 上启动 UI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SystemController controller = new SystemController();
                LoginFrame login = new LoginFrame(controller);
                login.setVisible(true);
            }
        });
    }
}