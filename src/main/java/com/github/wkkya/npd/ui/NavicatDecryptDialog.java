
package com.github.wkkya.npd.ui;

import com.github.wkkya.npd.enums.VersionEnum;
import com.github.wkkya.npd.util.DecodeNcx;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.JBColor;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NavicatDecryptDialog extends JFrame {

    private static final double NAVICAT11 = 1.1D;

    private static NavicatDecryptDialog instance;

    private JRadioButton navicat11RadioButton;
    private JRadioButton navicat12PlusRadioButton;
    private PlaceholderTextField passwordInputField;
    private JButton decryptButton;
    private JButton chooseFileButton;
    private JTextArea resultTextArea;
    private File selectedFile;
    private JLabel fileLinkLabel;

    /**
     * 解密工具类
     */
    private static DecodeNcx decodeNcx;


    public static NavicatDecryptDialog getInstance() {
        if (instance == null) {
            instance = new NavicatDecryptDialog();
        }
        return instance;
    }


    private NavicatDecryptDialog() {

        // 设置窗口标题
        setTitle("Navicat 密码查看工具");

        // 设置窗口布局
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 设置窗口的最小和最大尺寸
        setMinimumSize(new Dimension(500, 400));
        setMaximumSize(new Dimension(800, 600));

        // 初始化组件并添加到窗口
        initComponents(gbc);

        // 设置窗口初始大小
        setSize(800, 600);

        // 设置关闭操作
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 居中显示窗口
        setLocationRelativeTo(null);

        // 禁用窗口调整大小
        setResizable(false);

    }

    private void initComponents(GridBagConstraints gbc) {
        // 顶部面板：选择 Navicat 版本和输入密码
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navicat11RadioButton = new JRadioButton("navicat11");
        navicat12PlusRadioButton = new JRadioButton("navicat12+");
        ButtonGroup versionGroup = new ButtonGroup();
        versionGroup.add(navicat11RadioButton);
        versionGroup.add(navicat12PlusRadioButton);
        navicat12PlusRadioButton.setSelected(true); // 默认选中 navicat12+

        passwordInputField = new PlaceholderTextField(PlaceholderTextField.FIX_PLACEHOLDER, isDarkTheme());

        decryptButton = new JButton("查看密码");

        topPanel.add(navicat11RadioButton);
        topPanel.add(navicat12PlusRadioButton);
        topPanel.add(passwordInputField);
        topPanel.add(decryptButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.01; // 占据 5% 的高度
        add(topPanel, gbc);

        // 中间面板：操作说明、文件选择
        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chooseFileButton = new JButton("选择文件");
        middlePanel.add(new JLabel("导入 ncx 文件，请选择："));
        middlePanel.add(chooseFileButton);

        // 创建文件链接和清除按钮
        fileLinkLabel = new JLabel("", SwingConstants.LEFT);
        fileLinkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fileLinkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (selectedFile != null) {
                    JOptionPane.showMessageDialog(NavicatDecryptDialog.this, "文件路径: " + selectedFile.getAbsolutePath(), "文件信息", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        middlePanel.add(fileLinkLabel);
        gbc.gridy = 1;
        gbc.weighty = 0.01; // 占据 5% 的高度
        add(middlePanel, gbc);

        // 底部面板：结果显示区域
        resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        JBScrollPane scrollPane = new JBScrollPane(resultTextArea);

        gbc.gridy = 2;
        gbc.weighty = 0.98; // 占据 90% 的高度（因为顶部和中间各占 10%）
        add(scrollPane, gbc);

        // 绑定事件
        bindEvents();
    }

    private void bindEvents() {
        // 文件选择按钮事件
        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Navicat NCR 文件 (*.ncx)", "ncx"));
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop")); // 默认目录为桌面
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                if (selectedFile == null) {
                    JOptionPane.showMessageDialog(this, "请选择一个 ncx 文件！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                updateFileLinkLabel(selectedFile.getName());
                String version = navicat11RadioButton.isSelected() ? "Navicat 11" : "Navicat 12+";
                resultTextArea.append("版本: " + version + "\n");
                resultTextArea.append("解密文件: " + selectedFile.getAbsolutePath() + "\n");
                resultTextArea.append("--------------开始解密--------------" + "\n");
                //todo 解密逻辑
                String s = eventOnImport(selectedFile);
                resultTextArea.append(s);
                resultTextArea.append("--------------解密结束--------------" + "\n");
            }
        });

        // 查看密码按钮事件
        decryptButton.addActionListener(e -> {
            if (passwordInputField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入加密密码！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // 单个解密逻辑
            String version = navicat11RadioButton.isSelected() ? "Navicat 11" : "Navicat 12+";
            decodeNcx  = navicat11RadioButton.isSelected() ? new DecodeNcx(VersionEnum.native11.name()) : new DecodeNcx(VersionEnum.navicat12more.name());
            String decode = decodeNcx.decode(passwordInputField.getText().trim());
            resultTextArea.append("版本: " + version + "\n");
            resultTextArea.append("--------------开始解密--------------" + "\n");
            resultTextArea.append("加密密码: " + passwordInputField.getText() + "\n");
            resultTextArea.append("解密结果: " + decode + "\n");
            resultTextArea.append("--------------解密结束--------------" + "\n");
        });
    }

    private void updateFileLinkLabel(String fileName) {
        String theme = isDarkTheme() ? "dark" : "light";
        System.out.println("当前主题 - " + theme);
        Color linkColor = JBColor.BLUE;
        if (!fileName.isEmpty()) {
            fileLinkLabel.setText("<html><u>" + fileName + "</u></html>");
            fileLinkLabel.setForeground(linkColor);
            fileLinkLabel.setVisible(true);
        } else {
            fileLinkLabel.setText("");
            fileLinkLabel.setVisible(false);
        }
    }

    private boolean isDarkTheme() {
        Color backgroundColor = getBackground();
        return backgroundColor != null && backgroundColor.getRed() < 128 && backgroundColor.getGreen() < 128 && backgroundColor.getBlue() < 128;
    }


    /**
     * 文件解密
     *
     */
    public static String eventOnImport(File file) {
        /** 得到选择的文件* */
        if (file == null) {
            return "";
        }
        try {
            // List<Map <连接名，Map<属性名，值>>> 要导入的连接
            java.util.List<Map<String, Map<String, String>>> configMap = new ArrayList<>();
            //1、创建一个DocumentBuilderFactory的对象
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //2、创建一个DocumentBuilder的对象
            //创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();
            //3、通过DocumentBuilder对象的parser方法加载xml文件到当前项目下
            Document document = db.parse(file);
            //获取所有Connections节点的集合
            NodeList connectList = document.getElementsByTagName("Connection");

            NodeList nodeList = document.getElementsByTagName("Connections");
            NamedNodeMap verMap = nodeList.item(0).getAttributes();
            double version = Double.parseDouble((verMap.getNamedItem("Ver").getNodeValue()));
            if (version <= NAVICAT11) {
                decodeNcx = new DecodeNcx(VersionEnum.native11.name());
            } else {
                decodeNcx = new DecodeNcx(VersionEnum.navicat12more.name());
            }
            //配置map
            Map<String, Map<String, String>> connectionMap = new HashMap<>();
            //遍历每一个Connections节点
            for (int i = 0; i < connectList.getLength(); i++) {
                //通过 item(i)方法 获取一个Connection节点，nodelist的索引值从0开始
                Node connect = connectList.item(i);
                //获取Connection节点的所有属性集合
                NamedNodeMap attrs = connect.getAttributes();
                //遍历Connection的属性
                Map<String, String> map = new HashMap<>(0);
                for (int j = 0; j < attrs.getLength(); j++) {
                    //通过item(index)方法获取connect节点的某一个属性
                    Node attr = attrs.item(j);
                    map.put(attr.getNodeName(), attr.getNodeValue());
                }
                connectionMap.put(map.get("ConnectionName") + map.get("ConnType"), map);
            }
            configMap.add(connectionMap);
            return writeConfigFile(configMap);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "导入失败！请导入正确的ncx文件！", "提示",
                    JOptionPane.ERROR_MESSAGE);
            return "";
        }
    }

    /**
     * 写入配置文件
     *
     * @param list 读取ncx文件的数据
     */
    private static String writeConfigFile(List<Map<String, Map<String, String>>> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map<String, Map<String, String>> map : list) {
            for (Map.Entry<String, Map<String, String>> valueMap : map.entrySet()) {
                Map<String, String> resultMap = valueMap.getValue();
                String password = decodeNcx.decode(resultMap.getOrDefault("Password", ""));
                String host = resultMap.get("Host");
                String port = resultMap.get("Port");
                String username = resultMap.get("UserName");
                String connType = resultMap.get("ConnType");
                stringBuilder.append("【主机】:").append(host).append("\n").append("【端口】:").append(port).append("\n")
                        .append("【用户名】:").append(username).append("\n").append("【密码】:").append(password).append("\n")
                        .append("【连接类型】:").append(connType).append("\n").append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NavicatDecryptDialog dialog = new NavicatDecryptDialog();
            dialog.setVisible(true);
        });
    }
}