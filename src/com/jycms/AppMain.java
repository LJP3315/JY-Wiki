package com.jycms;

// 控制层，负责处理业务逻辑和数据交互
import com.jycms.controller.SystemController;
// 登录界面，用户看到的第一个界面
import com.jycms.view.LoginFrame;

import javax.swing.*;

public class AppMain {
    public static void main(String[] args) {
        // 确保所有创建和操作 UI 组件的代码都在“事件调度线程”
        // Swing 程序的规范，可以避免线程冲突，防止界面卡死
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 创建控制器，负责连接 View 和 Model
                SystemController controller = new SystemController();
                // 创建登录界面，传入 controller
                // 界面的按钮操作可以通过 Controller 调用 Model 层的方法
                LoginFrame login = new LoginFrame(controller);
                // 显示登录窗口，使用户可以开始交互
                login.setVisible(true);
            }
        });
    }
}