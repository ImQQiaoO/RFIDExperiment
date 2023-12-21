import javax.swing.*;

public class MainPage extends JFrame {

    private final JTabbedPane tabbedPane =
            new JTabbedPane(SwingConstants.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

    public MainPage() {
        this.setTitle("数据安全性实验");
        this.setVisible(true);
        init();
        this.setBounds(500, 200, 500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void init() {
        JPanel ParityCheckPanel = new ParityCheck();
        JPanel HammingCheckPanel = new HammingCheck();
        JPanel panel3 = new JPanel();
        tabbedPane.addTab("奇偶校验", ParityCheckPanel);
        tabbedPane.addTab("海明码校验", HammingCheckPanel);
        tabbedPane.addTab("CRC校验", panel3);
        tabbedPane.setTabPlacement(JTabbedPane.TOP); // 设置选项卡位置为上方
        this.add(tabbedPane);
    }
}
