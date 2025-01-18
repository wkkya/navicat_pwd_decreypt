package com.github.wkkya.npd.ui;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;

import javax.swing.*;

public class ThemeListener implements LafManagerListener {
    @Override
    public void lookAndFeelChanged(LafManager source) {
        SwingUtilities.invokeLater(() -> {
            // 当用户切换主题时，刷新相关 UI
            System.out.println("主题已切换，刷新插件界面...");
        });
    }
}


