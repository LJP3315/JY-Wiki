package com.jycms;

import com.jycms.controller.SystemController;
import com.jycms.view.LoginFrame;

import javax.swing.*;

public class AppMain {
    public static void main(String[] args) {
        // 推荐在 EDT 上启动 Swing UI
        SwingUtilities.invokeLater(() -> {
            SystemController controller = new SystemController();
            LoginFrame login = new LoginFrame(controller);
            login.setVisible(true);
        });
    }
}
