package org.study.os.memory;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

class GUI extends JFrame {

    public GUI() {
        JTabbedPane jtp = new JTabbedPane();
        add(jtp);
        setTitle("伙伴堆教学实验演示");
        setSize(1280, 750);                       // 设置窗口大小
        setLocationRelativeTo(null);             // 把窗口位置设置到屏幕中心
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // 当点击窗口的关闭按钮时退出程序（没有这一句，程序不会退出）
        setVisible(true);
        setResizable(false);

        JPanel teachPanel = new TeachPanel();
        JPanel showPanel = new ShowPanel();
        jtp.add("教学", teachPanel);
        jtp.add("手动演示", showPanel);

        jtp.setSelectedIndex(1); // 初始化显示哪个面板

    }
}

class TeachPanel extends JPanel {

    static class PicPanel extends JPanel {
        // TODO  Windows路径可能要用 \\
        String src = "images/img/";
        ImageIcon icon = new ImageIcon(src + "img01.png");
        int page = 1;
        public PicPanel() {
            setBounds(140, 30, 978, 550);
            //setBackground(Color.CYAN);
            setVisible(true);
        }
        public void changePic(boolean isNext) {
            page = isNext ? page + 1 : page - 1;
            icon = new ImageIcon(page >= 10 ? src + "img" + page +".png" : src + "img0" + page +".png");
            repaint();
        }
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(icon.getImage(), 0, 0, 978, 550, this);
        }

    }

    JButton lastPicBtn = new JButton("上一张");
    JButton nextPicBtn = new JButton("下一张");
    PicPanel picPanel = new PicPanel();

    public TeachPanel() {
        // System.out.println(System.getProperty("user.dir"));
        setLayout(null);

        lastPicBtn.setBounds(24, 600, 100, 50);
        add(lastPicBtn);

        nextPicBtn.setBounds(1136, 600, 100, 50);
        add(nextPicBtn);

        lastPicBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                picPanel.changePic(false);
            }
        });

        nextPicBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                picPanel.changePic(true);
            }
        });

        add(picPanel);

    }

}

class Request{
    public Boolean flag; // flag 为 true 表示一个释放的请求
    public Process process;
    Request(Boolean f, Process p) {
        process = p;
        flag = f;
    }
}

class ShowPanel extends JPanel {
    /** 算法相关变量 */
    private int memorySize;
    private int pageFrameSize;
    private int maxGroupNumber = 10;

    private Buddy mybuddy;
    private List<Request> request = new ArrayList<>();
    private List<Process> usingProcess = new ArrayList<>();


    /** ---------------------------------- 界面变量 ---------------------------------- */
    private JComboBox<String> memorySize_ComboBox = new JComboBox<>();      // 选择内存大小的下拉框（256M, 512M, 自定义）
    private JComboBox<String> pageSize_ComboBox = new JComboBox<>();        // 选择页框大小的下拉框（1K, 2K, 4K, 自定义）
    private int step = 0;

    private final JLabel k1_Label = new JLabel("M");                    // 自定义内存大小 单位
    private final JLabel k2_Label = new JLabel("K");                    // 自定义页框大小 单位
    private final JTextField memorySize_TextField = new JTextField();        // 自定义内存大小 文本框
    private final JTextField pageSize_TextField = new JTextField();          // 自定义页框大小 文本框

    JTextField mSize_TextField = new JTextField();                           // 进程大小的文本框，显示新建进程的大小
    JButton randomInt_Button = new JButton("随机数");                    // 「随机数」按钮，在mSize_TextField随机一个整数
    JButton addProcess_Button = new JButton("创建进程申请");              // 「创建进程申请」按钮，把进程放进申请队列
    JComboBox<String> releaseP_ComboBox = new JComboBox<>();                 // 「选择释放进程」下拉框
    JButton releaseP_Button = new JButton("释放进程申请");                // 「释放进程申请」按钮，把进程放进申请队列

    JButton nextStep_Button = new JButton("下一步");                     // 「下一步」按钮

    JTextPane mailBox = new JTextPane();                                     // 「MailBox」文本框
    Style styleRED,styleBLUE,styleGREEN,styleDefault;                        // MailBox文本样式


    JLabel step_Label = new JLabel();                                        // 显示 "Step: xx" 的文本框
    private final JPanel memory_Panel = new JPanel();                        // "内存使用情况" 外面的黑框


    JTextPane usingProcess_jtp = new JTextPane();                            // 「使用中的进程」文本框
    JTextPane requestQueue_jtp = new JTextPane();                            // 「请求队列」文本框
    JTextPane buddyFreeBlock_jtp = new JTextPane();                          // 「伙伴堆空闲块组链表」文本框

    /** ---------------------------------- -------- ---------------------------------- */

    public int getMemorySize() { // 获取内存大小
        String ss;
        if (memorySize_ComboBox.getSelectedIndex() < 2) {
            ss = memorySize_ComboBox.getSelectedItem().toString();
            ss = ss.substring(0, ss.length() - 1);
        } else ss = memorySize_TextField.getText(); // 自定义
        return Integer.parseInt(ss) * 1024;
    }
    public int getPageSize() { // 获取页框大小
        String ss;
        if (pageSize_ComboBox.getSelectedIndex() < 3) {
            ss = pageSize_ComboBox.getSelectedItem().toString();
            ss = ss.substring(0, ss.length() - 1);
        } else ss = pageSize_TextField.getText();
        return Integer.parseInt(ss);
    }
    void updatemSize() { // 更新「待创建进程大小」的TextField框中数字（根据页框大小随机决定）
        int mxS = pageFrameSize * (1 << (maxGroupNumber - 1));
        int mSize = (new Random().nextInt(mxS - 1) + 1) / pageFrameSize * pageFrameSize;
        mSize_TextField.setText(Integer.toString(mSize));
    }

    private void drawLeftInitControlUnit() {
        /** 内存大小和页框大小提示的JLabel组件 */
        JLabel memorySize_Label = new JLabel("内存大小：");
        memorySize_Label.setFont(new Font("宋体", Font.PLAIN, 16));
        memorySize_Label.setBounds(24, 14, 100, 24);
        add(memorySize_Label);

        JLabel pageSize_Label = new JLabel("页框大小：");
        pageSize_Label.setFont(new Font("宋体", Font.PLAIN, 16));
        pageSize_Label.setBounds(24, 44, 100, 24);
        add(pageSize_Label);

        /** 「选择"内存"大小的下拉框」布置与监听事件 */
        memorySize_ComboBox.addItem("256M");
        memorySize_ComboBox.addItem("512M");
        memorySize_ComboBox.addItem("自定义");
        memorySize_ComboBox.setBounds(104, 14, 100, 24);
        add(memorySize_ComboBox);
        memorySize_ComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (memorySize_ComboBox.getSelectedIndex() == 2) {
                    memorySize_TextField.setText("");
                    add(memorySize_TextField);
                    add(k1_Label);
                } else {
                    remove(memorySize_TextField);
                    remove(k1_Label);
                }
                repaint();
            }
        });

        /** 「选择"页框"大小的下拉框」布置与监听事件 */
        pageSize_ComboBox.addItem("1K");
        pageSize_ComboBox.addItem("2K");
        pageSize_ComboBox.addItem("4K");
        pageSize_ComboBox.addItem("自定义");
        pageSize_ComboBox.setBounds(104, 44, 100, 24);
        add(pageSize_ComboBox);
        pageSize_ComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (pageSize_ComboBox.getSelectedIndex() == 3) {
                    pageSize_TextField.setText("");
                    add(pageSize_TextField);
                    add(k2_Label);
                } else {
                    remove(pageSize_TextField);
                    remove(k2_Label);
                }
                repaint();
            }
        });

        /** 自定义「内存大小」和「页框大小」的TextField组件 */
        memorySize_TextField.setBounds(210, 14, 70, 24);
        pageSize_TextField.setBounds(210, 44, 70, 24);
        k1_Label.setBounds(280, 14, 100, 24);
        k2_Label.setBounds(280, 44, 100, 24);


        /** 按钮「开始模拟」的布置和监听事件 */
        JButton start_Button = new JButton("开始模拟");
        JButton reset_Button = new JButton("重置"); // 重置按钮
        start_Button.setBounds(24, 84, 120, 44);
        add(start_Button);
        start_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(!checkStart()) return;
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
                start_Button.setEnabled(false);memorySize_ComboBox.setEnabled(false);pageSize_ComboBox.setEnabled(false);
                reset_Button.setEnabled(true);nextStep_Button.setEnabled(true);randomInt_Button.setEnabled(true);addProcess_Button.setEnabled(true);releaseP_Button.setEnabled(true);
                mSize_TextField.setEnabled(true);releaseP_ComboBox.setEnabled(true);
                try {
                    run_start();
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        /** 按钮「重置」的布置和监听事件 */
        reset_Button.setBounds(160, 84, 120, 44);
        add(reset_Button);
        reset_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                start_Button.setEnabled(true);memorySize_ComboBox.setEnabled(true);pageSize_ComboBox.setEnabled(true);
                nextStep_Button.setEnabled(false);randomInt_Button.setEnabled(false);addProcess_Button.setEnabled(false);releaseP_Button.setEnabled(false);
                mSize_TextField.setEnabled(false);releaseP_ComboBox.setEnabled(false);

                step = 0;
                step_Label.setText("Step: " + step);
                run_reset();
            }
        });

    }

    private void drawLeftProcessControlUnit() {

        /** 按钮「下一步」的布置和监听事件 */
        nextStep_Button.setBounds(24, 320, 260, 44);
        nextStep_Button.setEnabled(false);
        add(nextStep_Button);
        nextStep_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                step_Label.setText("Step: " + step);
                try {
                    run_nextStep();
                    update_UI();
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        /** 创建进程、释放进程文字JLabel组件的布置 */
        JLabel sp = new JLabel("--------------------------");
        sp.setFont(new Font("宋体", Font.PLAIN, 18));
        sp.setBounds(24,125,300,20);
        add(sp);
        JLabel l1 = new JLabel("创建进程:                     k");
        l1.setFont(new Font("宋体", Font.PLAIN, 15));
        l1.setBounds(24,136,300,44);
        add(l1);
        JLabel l2 = new JLabel("释放进程:");
        l2.setFont(new Font("宋体", Font.PLAIN, 15));
        l2.setBounds(24,226,300,44);
        add(l2);

        /** 随机进程大小文本框的布置 */
        mSize_TextField.setBounds(24+70,143,100,30);
        mSize_TextField.setEnabled(false);
        add(mSize_TextField);

        /** 按钮「随机数」的布置和监听事件 */
        randomInt_Button.setBounds(24,178,120,44);
        randomInt_Button.setEnabled(false);
        add(randomInt_Button);
        randomInt_Button.addActionListener(new ActionListener() { // 在 mSize_TextField 生成随机数（作为创建进程的大小）
            @Override
            public void actionPerformed(ActionEvent e) {
                updatemSize();
            }
        });

        /** 按钮「创建进程申请」的布置和监听事件 */
        addProcess_Button.setBounds(160, 178, 120,44);
        addProcess_Button.setEnabled(false);
        add(addProcess_Button);
        addProcess_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int rSize = Integer.parseInt(mSize_TextField.getText());
                Process tmp = Process.createProcess(pageFrameSize, maxGroupNumber, rSize);
                try {
                    String ss = "-----创建了新的进程-----\n" +
                            "进程ID：" + tmp.ID + "\n" +
                            "进程实际大小：" + tmp.requestSize + "K\n" +
                            "请求内存空间：" + tmp.size + "K\n" +
                            "----------------------";
                    addMail(ss,styleDefault);
                } catch (BadLocationException ex) {
                    throw new RuntimeException(ex);
                }
                releaseP_ComboBox.addItem("ID: " + tmp.ID);
                request.add(new Request(false, tmp));
                mSize_TextField.setText("");
                //updatemSize();
                update_UI();
            }
        });

        /** 释放进程下拉框的布置 */
        releaseP_ComboBox.setBounds(24+70,228, 120,44);
        releaseP_ComboBox.addItem("未选择");
        releaseP_ComboBox.setEnabled(false);
        add(releaseP_ComboBox);

        /** 按钮「释放进程申请」的布置和监听事件 */
        releaseP_Button.setBounds(160, 228+40, 120,44);
        add(releaseP_Button);
        releaseP_Button.setEnabled(false);
        releaseP_Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(releaseP_ComboBox.getSelectedIndex() == 0)return;
                String ss = releaseP_ComboBox.getSelectedItem().toString();
                releaseP_ComboBox.removeItemAt(releaseP_ComboBox.getSelectedIndex());
                releaseP_ComboBox.setSelectedIndex(0);
                int id = Integer.parseInt(ss.substring(4));
                for (int i = 0; i < request.size(); i++) {
                    Request tmp = request.get(i);
                    if (!tmp.flag && tmp.process.ID == id) {
                        String s = "进程ID: " + id + "没有申请内存\n直接删除进程";
                        request.remove(i);
                        nowRequest_jtp.setText("");
                        try {
                            buddyAddMail(s, buddyStyleGreen);
                        } catch (BadLocationException ex) {
                            throw new RuntimeException(ex);
                        }
                        update_UI();
                        return;
                    }
                }
                for (int i =0; i < usingProcess.size(); i++) { // 进程正在使用
                    if (usingProcess.get(i).ID == id) {
                        request.add(0, new Request(true, usingProcess.get(i)));
                    }
                }
                update_UI();
            }
        });

    }

    private void drawMonitorUnit() { // Step、内存使用情况、memory_Panel黑框的绘制
        step_Label.setText("Step: " + step);
        step_Label.setFont(new Font("宋体", Font.PLAIN, 17));
        step_Label.setBounds(310, 14, 100, 30);
        add(step_Label);

        JLabel memoryUse_Label = new JLabel("内存使用情况：");
        memoryUse_Label.setFont(new Font("宋体", Font.PLAIN, 17));
        memoryUse_Label.setBounds(310, 44, 200, 30);
        add(memoryUse_Label);

        memory_Panel.setLayout(null);
        memory_Panel.setBounds(310, 70, 900, 40);
        memory_Panel.setBorder(BorderFactory.createLineBorder(Color.black));
        add(memory_Panel);
    }

    public void addMail(String info, Style style) throws BadLocationException { // style选项："RED", "BLUE", "GREEN", "default"
        mailBox.getStyledDocument().insertString(mailBox.getStyledDocument().getLength(),info+"\n", style);
        mailBox.repaint();
    }
    private void drawMailBox() {
        JLabel l1 = new JLabel("提示框：");
        l1.setFont(new Font("宋体", Font.PLAIN, 15));
        l1.setBounds(24,365,300,44);
        add(l1);
        JScrollPane jsp = new JScrollPane(mailBox,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jsp.setBounds(24, 405, 255, 250);
        mailBox.setEditable(false);
        add(jsp);


        /** 样式初始化 */
        Style style = mailBox.getStyledDocument().addStyle(null, null); // 获取组件空样式
        StyleConstants.setFontFamily(style, "宋体");// 为style样式设置字体属性
        StyleConstants.setFontSize(style, 15);// 为style样式设置字体大小

        Style normal = mailBox.addStyle("normal", style);// 将style样式添加到组件，并命名为normal，返回一个样式由normal变量接收

        // 这个时候，组件编辑器关联的模型中就添加了一个样式normal，这个样式是最基本的一个样式，其他样式可以根据他进行修改

        styleRED = mailBox.addStyle("styleRED", normal);// 基于normal样式，在添加三次
        styleBLUE = mailBox.addStyle("styleBLUE", normal);// 此时，三个样式和normal样式是一模一样的
        styleGREEN = mailBox.addStyle("styleGREEN", normal);// 如果修改，可以对每个变量单独修改，具体修改方式如下
        styleDefault = mailBox.addStyle("default", normal);

        StyleConstants.setForeground(styleRED, Color.RED); // 将styleRED的颜色设置为红色，下面同理
        StyleConstants.setForeground(styleBLUE, Color.BLUE);
        StyleConstants.setForeground(styleGREEN, Color.GREEN);
        StyleConstants.setForeground(styleDefault, Color.black);
        StyleConstants.setFontSize(styleDefault, 14); // 将styleDefault的大小设置为14

    }


    private void drawProcessColumnUnit() { // 绘制「使用中的进程」、「请求队列」、「伙伴堆空闲块组链表」、
        int x = 32,y = 330;

        JLabel usingProcess_Label = new JLabel("使用中的进程：");
        usingProcess_Label.setBounds(310, 120,200,30);
        usingProcess_Label.setFont(new Font("宋体", Font.PLAIN, 17));
        add(usingProcess_Label);
        // 给 usingProcess_jtp 添加可滚动的JScrollPane
        JScrollPane usingProcess_ScrollPane = new JScrollPane(usingProcess_jtp,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        usingProcess_ScrollPane.setBounds(310, 120+30, 250, 200+x); // y+26
        add(usingProcess_ScrollPane);

        JLabel requestQueue_Label = new JLabel("请求队列：");
        requestQueue_Label.setBounds(310, 360+x,200,30);
        requestQueue_Label.setFont(new Font("宋体", Font.PLAIN, 17));
        add(requestQueue_Label);
        // 给 requestQueue_jtp 添加可滚动的JScrollPane
        JScrollPane requestQueue_ScrollPane = new JScrollPane(requestQueue_jtp,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        requestQueue_ScrollPane.setBounds(310,360+30+x,250,200+x);
        add(requestQueue_ScrollPane);

        JLabel buddyFreeBlock_Label = new JLabel("伙伴堆空闲块组链表：");
        buddyFreeBlock_Label.setBounds(590+y, 120,200,30);
        buddyFreeBlock_Label.setFont(new Font("宋体", Font.PLAIN, 17));
        add(buddyFreeBlock_Label);
        // 给 buddyFreeBlock_jtp 添加可滚动的JScrollPane
        JScrollPane buddyFreeBlock_ScrollPane = new JScrollPane(buddyFreeBlock_jtp,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        buddyFreeBlock_ScrollPane.setBounds(590+y, 150, 300, 505); // y+height = 655
        add(buddyFreeBlock_ScrollPane);

        usingProcess_jtp.setEditable(false);
        requestQueue_jtp.setEditable(false);
        buddyFreeBlock_jtp.setEditable(false);

    }

    JTextPane nowRequest_jtp = new JTextPane();


    public void buddyAddMail(String info, Style style) throws BadLocationException {
        nowRequest_jtp.getStyledDocument().insertString(nowRequest_jtp.getStyledDocument().getLength(),info+"\n", style);
        nowRequest_jtp.repaint();
    }

    Style buddyStyleBig,buddyStyleGreen,buddyStyleRed,buddyStyleDefault;
    public void drawNowRequestMailBox() { // 绘制「正在处理的请求」任务
        JLabel lb = new JLabel("处理请求：");
        lb.setBounds(590, 120,200,30);
        lb.setFont(new Font("宋体", Font.PLAIN, 17));
        add(lb);
        // 给 nowRequest_jtp 添加可滚动的JScrollPane
        JScrollPane nowRequest_SP = new JScrollPane(nowRequest_jtp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        nowRequest_SP.setBounds(590,150,300,505);
        add(nowRequest_SP);


        /** 样式初始化 */
        Style style = nowRequest_jtp.getStyledDocument().addStyle(null, null); // 获取组件空样式
        StyleConstants.setFontFamily(style, "宋体");// 为style样式设置字体属性
        StyleConstants.setFontSize(style, 15);// 为style样式设置字体大小

        Style normal = nowRequest_jtp.addStyle("normal", style);// 将style样式添加到组件，并命名为normal，返回一个样式由normal变量接收

        // 这个时候，组件编辑器关联的模型中就添加了一个样式normal，这个样式是最基本的一个样式，其他样式可以根据他进行修改

        buddyStyleBig = nowRequest_jtp.addStyle("buddyStyleBig", normal);// 基于normal样式，在添加三次
        buddyStyleGreen = nowRequest_jtp.addStyle("buddyStyleGreen", normal);// 此时，三个样式和normal样式是一模一样的
        buddyStyleDefault = nowRequest_jtp.addStyle("buddyStyleDefault", normal);// 如果修改，可以对每个变量单独修改，具体修改方式如下
        buddyStyleRed = nowRequest_jtp.addStyle("buddyStyleRed", normal);

        StyleConstants.setForeground(buddyStyleBig, new Color(6,111,255)); // 将styleRED的颜色设置为红色，下面同理
        StyleConstants.setForeground(buddyStyleGreen, new Color(	6,181,8));
        StyleConstants.setForeground(buddyStyleRed, Color.RED);
        StyleConstants.setFontSize(buddyStyleBig, 17);
        StyleConstants.setFontSize(buddyStyleGreen, 16);
        StyleConstants.setFontSize(buddyStyleRed, 16);
        StyleConstants.setFontSize(buddyStyleDefault, 14); // 将styleDefault的大小设置为14


    }


    public void updateUsingProcess_UI(List<Process> usingProcess) { // 更新「使用中的进程」界面（usingProcess_jtp）
        StringBuilder inf = new StringBuilder();
        for (int i = 0; i < usingProcess.size(); i++) {
            Process tmp = usingProcess.get(i);
            inf.append(String.format("进程ID: %d\t申请内存大小: %d K\n\tfrom: %d  to: %d\n", tmp.ID, tmp.size, tmp.address[0], tmp.address[1]));
        }
        usingProcess_jtp.setText(inf.toString());
        usingProcess_jtp.repaint();
    }
    public void updateRequestQueue_UI(List<Request> request) { // 更新「请求队列」界面（requestQueue_jtp）
        StringBuilder inf = new StringBuilder();
        for (int i = 0; i < request.size(); i++) {
            Request tmp = request.get(i);
            inf.append("进程 " + tmp.process.ID + " (size: " + tmp.process.size + ")\t");
            inf.append(tmp.flag ? "释放内存\n" : "申请内存\n");
        }
        requestQueue_jtp.setText(inf.toString());
        requestQueue_jtp.repaint();
    }
    public List<JLabel> labelList = new ArrayList<>(); // 所有进程对应的Label
    public void updateMemoryPanel_UI() { // 更新「内心使用情况」显示框中的隔线和进程
        while(!labelList.isEmpty()) { // 移走之前的
            JLabel tmp = labelList.remove(0);
            tmp.setVisible(false);
            tmp = null;
        }
        for (int i = 0; i < usingProcess.size(); i++) { // 绘制已使用进程
            JLabel tmp = addPanel(usingProcess.get(i), true);
            labelList.add(tmp);
        }
        // 绘制分割结果
        for (int i = 0; i < maxGroupNumber; i++) {
            List<int[]> temp = mybuddy.freeBlock.get((1 << i) * mybuddy.pageSize);
            for (int j = 0; j < temp.size(); j++) {
                JLabel tmp = addPanel(memory_Panel, mybuddy.freeMemorySize, temp.get(j)[0], temp.get(j)[1], "", false, null);
                labelList.add(tmp);
            }
        }

    }
    private void appendInt(StringBuilder inf, int val) { // updateBuddyFreeBlock_UI专用的格式化函数
        if (val >= 10000) inf.append("  ");
        else if (val >= 1000) inf.append("    ");
        else if (val >= 100) inf.append("      ");
        else if (val >= 10) inf.append("        ");
        else inf.append("          ");
        inf.append(val);
    }
    public void updateBuddyFreeBlock_UI() {
        StringBuilder inf = new StringBuilder();
        inf.append("伙伴堆空闲块组链表：\n");
        for (int i = 0; i < maxGroupNumber; i++) {
            List<int[]> temp = mybuddy.freeBlock.get((1 << i) * mybuddy.pageSize);
            inf.append("大小为" + (1 << i) * mybuddy.pageSize + "的地址:\n");
            for (int j = 0; j < temp.size(); j++) {
                inf.append("   - 第 "+(j+1) +" 块为");
                appendInt(inf, temp.get(j)[0]);
                inf.append(" 到");
                appendInt(inf, temp.get(j)[1]);
                inf.append("\n");
            }
        }
        buddyFreeBlock_jtp.setText(inf.toString());
        buddyFreeBlock_jtp.repaint();
    }
    private void update_UI() { // 更新界面
        updateRequestQueue_UI(request);
        updateUsingProcess_UI(usingProcess);
        updateMemoryPanel_UI();
        updateBuddyFreeBlock_UI();
        nextStep_Button.setEnabled(!(requestComplete && request.isEmpty()));
    }



    private boolean checkStart() throws BadLocationException { // 检查内存大小和页框大小

        if (getMemorySize() < getPageSize()*1024*3) {
            String s = "内存大小的设置太小了，无法完成空闲块的分配！";
            addMail(s, styleRED);
            return false;
        }
        return true;
    }
    public void run_start() throws BadLocationException {
        memorySize = getMemorySize();
        pageFrameSize = getPageSize();
        mybuddy = new Buddy(pageFrameSize, memorySize);
        mybuddy.init();

        ///run_nextStep();
        update_UI();
        //updatemSize();

        String inf = "-----伙伴堆演示开始-----\n" +
                "设定内存大小：" + memorySize/1024 + " M\n" +
                "设定页框大小：" + pageFrameSize + " K\n" +
                "自由内存：3 块最大页框\n" +
                "----------------------\n";
        addMail(inf,styleDefault);

    }
    public void run_reset() {
        requestComplete = true;
        nowRequest = null;
        isdivide = false;

        usingProcess_jtp.setText("");
        requestQueue_jtp.setText("");
        buddyFreeBlock_jtp.setText("");
        pageSize_TextField.setText("");
        memorySize_TextField.setText("");
        nowRequest_jtp.setText("");
        memorySize_ComboBox.setSelectedIndex(0);
        pageSize_ComboBox.setSelectedIndex(0);
        mailBox.setText("");
        Process.init();

        //  把所有在 memory_Panel 中的物件都拿走
        while(!labelList.isEmpty()) {
            JLabel tmp = labelList.remove(0);
            tmp.setVisible(false);
        }
        while (!request.isEmpty()) request.remove(0);
        while (!usingProcess.isEmpty()) usingProcess.remove(0);
        while (releaseP_ComboBox.getItemCount() > 1) releaseP_ComboBox.removeItemAt(1);
        mSize_TextField.setText("");
    }

    public boolean requestComplete = true;
    public Request nowRequest = null;
    public boolean isdivide = false;
    public int siz = 0;
    public int l = 0;
    public int r = 0;
    public void run_nextStep() throws BadLocationException {
        if(requestComplete == true) {
            if(request.isEmpty()) {
                addMail("请求队列为空",styleRED);
                return;
            }
            nowRequest = request.remove(0);
            requestComplete = false;

            nowRequest_jtp.setText("");
            StringBuilder sb = new StringBuilder();
            sb.append("===== 正在处理的请求： =====\n");
            if (nowRequest.flag) sb.append("进程ID: " + nowRequest.process.ID + "   释放内存 从 " + nowRequest.process.address[0] + " 到 " + nowRequest.process.address[1] + "\n");
            else sb.append("进程ID: " + nowRequest.process.ID + "   申请内存\n\t申请内存大小：" + nowRequest.process.size + " K\n");
            //sb.append("==================");
            buddyAddMail(sb.toString(), buddyStyleBig);

            return;
        }
        if(!nowRequest.flag) { // request 是申请内存请求
            if(!isdivide) {
                siz = nowRequest.process.size;
                while(siz <= mybuddy.maxBlockSize && mybuddy.freeBlock.get(siz).size() == 0)siz *= 2;
                isdivide = true;
            }
            if(siz > mybuddy.maxBlockSize) {
                buddyAddMail("进程申请失败，因为process.size过大", buddyStyleRed);
                requestComplete = true;isdivide = false;
                request.add(nowRequest);
                return;
            }
            if(siz == nowRequest.process.size) {
                mybuddy.useBlock(nowRequest.process.size, nowRequest.process);
                usingProcess.add(nowRequest.process);
                // releaseP_ComboBox.addItem("ID: " + nowRequest.process.ID);

                String s = "内存已分配！\n给进程(ID: " + nowRequest.process.ID + ") 分配了from " + nowRequest.process.address[0] + " to " +  nowRequest.process.address[1] + " 的空闲块\n";
                buddyAddMail(s, buddyStyleGreen);

                requestComplete = true;isdivide = false;return;
            }
            List<int[]> temp = mybuddy.freeBlock.get(siz);
            int addr[] = temp.remove(0);
            mybuddy.freeBlock.put(siz,temp);

            int[] leftSon = new int[2];
            int[] rightSon = new int[2];
            leftSon[0] = addr[0];
            leftSon[1] = addr[0] + siz / 2 - 1;
            rightSon[0] = addr[0] + siz / 2;
            rightSon[1] = addr[1];
            List<int[]> temp1 = mybuddy.freeBlock.get(siz / 2);
            temp1.add(leftSon);
            temp1.add(rightSon);

            String s = "分裂空闲块： size: " + siz + "( from " + addr[0] + " to " + addr[1] + ")\n" +
                    "子块1：" + (siz/2) + "K  from " + leftSon[0] + " to " + leftSon[1] + "\n" +
                    "子块2：" + (siz/2) + "K  from " + rightSon[0] + " to " + rightSon[1] + "\n";
            buddyAddMail(s, styleDefault);


            mybuddy.freeBlock.put(siz / 2, temp1);
            siz /= 2;
        } else {// request 是释放内存请求
            if(!isdivide) {
                usingProcess.remove(nowRequest.process);
                siz = nowRequest.process.size;
                l = nowRequest.process.address[0];
                r = nowRequest.process.address[1];
                isdivide = true;

                buddyAddMail("已经释放该进程所占内存空间，从"+l +"到"+r +"，接下来进行空闲块合并\n",styleDefault);

                return;
            }
            List<int[]> temp = mybuddy.freeBlock.get(siz);
            boolean ff = false;
            if(siz == mybuddy.maxBlockSize) {
                isdivide = false;requestComplete = true;
                temp = mybuddy.freeBlock.get(siz);
                temp.add(new int[] {l, r});
                buddyAddMail("进程已释放！（空闲块大小为最大块尺寸，合并内存结束）",buddyStyleGreen);
                mybuddy.freeBlock.put(siz, temp);
                return;
            }
            for (int i = 0; i < temp.size(); i++) {
                int[] block = temp.get(i);
                if (block[1] == l - 1) {
                    buddyAddMail("将" + block[0] + "~" + block[1] + "和" + l + "~" + r + "合并", styleDefault);
                    l = block[0];
                    buddyAddMail("合并为 " + l + "~" + r + "\n合并后的大小：" + siz*2 + "K\n", styleDefault);
                    temp.remove(i);
                    mybuddy.freeBlock.put(siz, temp);
                    siz *= 2;
                    ff = true;
                    break;
                } else if (block[0] == r + 1) {
                    buddyAddMail("将" + l + "~" + r + "和" + block[0] + "~" + block[1] + "合并", styleDefault);
                    r = block[1];
                    buddyAddMail("合并到 " + l + "到" + r+ "\n合并后的大小：" + siz*2 + "K\n", styleDefault);
                    temp.remove(i);
                    mybuddy.freeBlock.put(siz, temp);
                    siz *= 2;
                    ff = true;
                    break;
                }
            }
            if (ff == false) {
                isdivide = false;requestComplete = true;
                temp = mybuddy.freeBlock.get(siz);
                temp.add(new int[] {l, r});
                buddyAddMail("进程已释放！（合并内存到" + l + " ---- " + r, buddyStyleGreen);
                mybuddy.freeBlock.put(siz, temp);
            }
        }
    }



    // 将panel划分totalSplit份，再从L到R的范围上绘制
    JLabel addPanel(Process process, boolean setBackground) {
        return addPanel(memory_Panel, mybuddy.freeMemorySize, process.address[0], process.address[1], "ID:"+process.ID, setBackground, process.color);
    }
    JLabel addPanel(JPanel panel, double totalSplit, double L, double R, String panelText, boolean setBackground, Color color) {
        double x = L * (panel.getWidth() / totalSplit);
        double width = (R - L + 1) * (panel.getWidth() / totalSplit);
        JLabel newLabel = new JLabel(panelText);
        newLabel.setBounds((int)x, 0, (int)(width+0.5), panel.getHeight());

        newLabel.setForeground(Color.BLACK);
        newLabel.setOpaque(true);  // 设置控件透明
        newLabel.setBorder(BorderFactory.createLineBorder(Color.black));  // 设置边框
        if (setBackground)
            newLabel.setBackground(color);
        panel.add(newLabel);
        repaint();
        return newLabel;
    }


    public ShowPanel() { // 构造函数
        setLayout(null);
        drawLeftInitControlUnit();
        drawLeftProcessControlUnit();
        drawMonitorUnit();
        drawMailBox();
        drawNowRequestMailBox();
        drawProcessColumnUnit();
    }
}


public class UI {
    /**
     * 伙伴堆算法内存分配释放的模拟实现
     * 这个好像是大二下学期操作系统实践课的一个小课设
     * 虽然代码写的不算很简洁，不过还是很有纪念意义的，把他放到这里了
     */
    public static void main(String[] args) {
        new GUI();
    }
}


// 每个使用Swing组件的Java程序都必须至少有一个顶层容器，别的组件都必须放在这个顶层容器上才能显现出来
// 顶层容器：
//  JFrame（单个主窗口），JDialog（二级窗口 对话框），JApplet
//
// 中间层容器：
//  JPanel
//  JScrollPane
//  JSplitPane
//  JTabbedPane 给窗口设置标签
//  JToolBar
//
// 原子组件：
//  显示不可编辑信息的
//   JLabel、JProgressBar、JToolTip
//  有控制功能、可以用来输入信息的
//   JButton、JCheckBox、JRadioButton、JComboBox、JList、
//   JMenu、JSlider、JSpinner、JTexComponent等
//  能提供格式化的信息并允许用户选择的
//   JColorChooser、JFileChooser、JTable、JTree