import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Random;

public class ParityCheck extends JPanel {

    private String[] columnNames;
    private String[] columnNames1;
    private JTable table;
    private JTable table1;
    private Object[][] data;
    private Object[][] data1;

    public ParityCheck() {
        this.setLayout(null);
        init();
    }

    private void init() {

        JButton receiveData = new JButton("接受数据");
        JButton generateData = new JButton("产生数据");
        this.add(generateData);
        this.add(receiveData);

        generateData.setBounds(40, 20, 100, 18);
        receiveData.setBounds(300, 20, 100, 18);

        generateData.addActionListener(e -> generate());
        receiveData.addActionListener(e -> receive());

        //第一个表格
        columnNames = new String[]{"序号", "信息项", "校验码"};
        UIManager.put("Table.gridColor", new Color(0, 0, 0, 0));
        UIManager.put("Table.intercellSpacing", new Dimension(0, 0));

        Object[][] data = new Object[0][0];

        table = new JTable(data, columnNames);
        table.setEnabled(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 40, 200, 300);
        this.add(scrollPane);


        //第二个表格
        columnNames1 = new String[]{"序号", "信息项", "校验码", "计算"};
        table1 = new JTable(data, columnNames1);
        table1.setEnabled(false);
        table1.getColumnModel().getColumn(0).setPreferredWidth(30);
        table1.getColumnModel().getColumn(1).setPreferredWidth(90);
        table1.getColumnModel().getColumn(2).setPreferredWidth(50);
        table1.getColumnModel().getColumn(3).setPreferredWidth(50);

        JScrollPane scrollPane1 = new JScrollPane(table1);
        scrollPane1.setBounds(230, 40, 220, 300);
        this.add(scrollPane1);
    }


    private void generate() {
        String[] binaryNumbers = BinaryNumberGenerator.generateUniqueBinaryNumbers(100);
        data = new Object[binaryNumbers.length][columnNames.length];
        for (int i = 0; i < binaryNumbers.length; i++) {
            data[i][0] = i + 1;
            data[i][1] = binaryNumbers[i];
            data[i][2] = generateParityBit(binaryNumbers[i]);
        }
        DefaultTableModel newModel = new DefaultTableModel(data, columnNames);
        table.setModel(newModel); // 将新的TableModel设置给table对象

    }

    private void receive() {

        Random r = new Random();
        data1 = new Object[100][columnNames1.length];

        for (int i = 0; i < 100; i++) {
            data1[i][0] = data[i][0];
            data1[i][1] = data[i][1];
            data1[i][2] = data[i][2];
        }

        for (int i = 0; i < 100; i++) {
            if (r.nextInt(100) < 10) {  // 十分之一
                StringBuilder sb = new StringBuilder((String) data1[i][1]);
                for (int j = 0; j < 8; j++) {
                    if (r.nextInt(10) < 2) {    // 五分之一
                        String temp = (String) data1[i][1];
                        sb.setCharAt(j, temp.charAt(j) == '0' ? '1' : '0');
                    }
                    data1[i][1] = sb.toString();
                    System.out.println(data1[i][1]);
                }
            }
            data1[i][3] = generateParityBit((String) data1[i][1]);
        }

        // 创建自定义的单元格渲染器
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (data1[row][2] != data1[row][3]) {
                    c.setForeground(Color.RED);
                } else if (data[row][1] != data1[row][1]) {
                    c.setForeground(Color.GREEN);
                } else {
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        };

        // 设置表的默认渲染器
        table1.setDefaultRenderer(Object.class, renderer);

        // 刷新表格
        table.repaint();
        table1.repaint();

        DefaultTableModel newModel = new DefaultTableModel(data1, columnNames1);
        table1.setModel(newModel); // 将新的TableModel设置给table对象
    }


    //计算校验位
    private String generateParityBit(String binaryString) {
        if (binaryString.length() != 8) {
            throw new IllegalArgumentException("输入的二进制字符串长度必须为8位");
        }

        int countOnes = 0;
        for (char digit : binaryString.toCharArray()) {
            if (digit != '0' && digit != '1') {
                throw new IllegalArgumentException("输入的字符串必须只包含0和1");
            }
            if (digit == '1') {
                countOnes++;
            }
        }

        // 计算奇偶校验位
        if (countOnes % 2 == 0) {
            return "0";  // 偶校验，校验位为0
        } else {
            return "1";  // 奇校验，校验位为1
        }
    }
}
