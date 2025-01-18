package com.github.wkkya.npd.ui;

import javax.swing.*;
import java.awt.*;

public class PlaceholderTextField extends JTextField {

    public static final String FIX_PLACEHOLDER = "请输入加密后的密码...";
    private final String placeholder;
    private boolean isEmpty = true;
    private boolean isDarkTheme = false; // 假设默认是浅色主题

    public PlaceholderTextField(String placeholder, Boolean isDarkTheme) {
        this.placeholder = placeholder;
        setForeground(Color.GRAY);
        setText(placeholder);
        setPreferredSize(new Dimension(300, getPreferredSize().height));
        setCaretColor(Color.GRAY);

        // 设置主题监听器
        setThemeListener(isDarkTheme);

        this.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (isEmpty) {
                    setText("");
                    updateColors();
                    setCaretColor(getForeground());
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (getText().trim().isEmpty()) {
                    setText(placeholder);
                    setForeground(Color.GRAY);
                    setCaretColor(Color.GRAY);
                    isEmpty = true;
                } else {
                    isEmpty = false;
                    updateColors();
                }
            }
        });

        this.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                checkForEmpty();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                checkForEmpty();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                checkForEmpty();
            }

            private void checkForEmpty() {
                if (getText().trim().isEmpty()) {
                    if (!hasFocus()) {
                        setText(placeholder);
                        setForeground(Color.GRAY);
                        setCaretColor(Color.GRAY);
                        isEmpty = true;
                    }
                } else {
                    isEmpty = false;
                    updateColors();
                }
            }
        });
    }

    // 更新颜色的方法
    private void updateColors() {
        if (!FIX_PLACEHOLDER.equals(getText())){
            if (isDarkTheme) {
                setForeground(Color.WHITE);
            } else {
                setForeground(Color.BLACK);
            }
        }
    }

    // 设置主题监听器（假设有一个方法可以获取当前主题）
    private void setThemeListener(Boolean isDarkTheme) {
        this.isDarkTheme = isDarkTheme;
        updateColors();
    }
}
