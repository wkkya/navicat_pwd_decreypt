package com.github.wkkya.npd.action;

import com.github.wkkya.npd.ui.NavicatDecryptDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.scale.JBUIScale;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class NavicatToolAction extends AnAction implements DumbAware {

    private static final String ICON_PATH = "/icons/icon2.png"; // 使用 PNG 图标
    private static final String ACTION_TEXT = "Navicat 密码解密工具";
    private static final String ACTION_DESCRIPTION = "打开 Navicat 密码解密工具";

    public NavicatToolAction() {
        super(ACTION_TEXT, ACTION_DESCRIPTION, loadIcon());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformCoreDataKeys.PROJECT);
        if (project == null) {
            Messages.showMessageDialog("当前没有打开项目", "提示", Messages.getInformationIcon());
            return;
        }

        // 打开 Navicat 密码解密工具弹窗
        SwingUtilities.invokeLater(() -> {
            NavicatDecryptDialog dialog = NavicatDecryptDialog.getInstance();

            dialog.setVisible(true);//替换为JDK17以上用法
//            dialog.show(); // 使用 IntelliJ 的 show() 方法显示对话框
        });
    }

    private static Icon loadIcon() {
        try {
            // 加载 PNG 图标
            Image image = Toolkit.getDefaultToolkit().getImage(NavicatToolAction.class.getResource(ICON_PATH));
            MediaTracker tracker = new MediaTracker(new Container());
            tracker.addImage(image, 0);
            tracker.waitForID(0);

            // 获取标准图标大小
            int size = JBUIScale.scale(18); // 标准图标大小为 16x16 像素

            // 缩放图标
            Image scaledImage = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (InterruptedException ex) {
            // 如果加载图标失败，返回默认图标或 null
            ex.printStackTrace();
            return null;
        }
    }
}
