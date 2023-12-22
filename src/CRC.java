import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class CRC extends JPanel {

    String[] columnNames;
    String[] columnNames1;
    JTable table;
    JTable table1;
    Object[][] data;
    Object[][] data1;
    int count1 = 0, count2 = 0;
    java.util.List<Integer> c2 = new Vector<>();
    List<Integer> c1 = new Vector<>();
    JLabel Red;
    JLabel Green;
    static JTextField CodeString;

    public CRC() {
        this.setLayout(null);
        init();
    }

    private void init() {
        JButton receive = new JButton("接受数据");
        JButton generate = new JButton("产生数据");
        this.add(generate);
        this.add(receive);

        generate.setBounds(40, 20, 100, 18);
        receive.setBounds(300, 20, 100, 18);

        generate.addActionListener(e -> generate());
        receive.addActionListener(e -> receive());

        //第一个表格
        columnNames = new String[]{"序号", "信息项", "校验码"};
        UIManager.put("Table.gridColor", new Color(0, 0, 0, 0));
        UIManager.put("Table.intercellSpacing", new Dimension(0, 0));

        Red = new JLabel();
        Green = new JLabel();
        Red.setBounds(90, 400, 100, 30);
        Green.setBounds(300, 400, 100, 30);


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
        columnNames1 = new String[]{"序号", "信息项", "计算"};
        table1 = new JTable(data, columnNames1);
        table1.setEnabled(false);
        table1.getColumnModel().getColumn(0).setPreferredWidth(30);
        table1.getColumnModel().getColumn(1).setPreferredWidth(170);
        table1.getColumnModel().getColumn(2).setPreferredWidth(50);

        JScrollPane scrollPane1 = new JScrollPane(table1);
        scrollPane1.setBounds(230, 40, 240, 300);
        this.add(scrollPane1);

        JLabel jLabel1 = new JLabel("出错检验到：");
        JLabel jLabel2 = new JLabel("出错未检验到：");

        jLabel1.setBounds(20, 400, 100, 30);
        jLabel2.setBounds(220, 400, 100, 30);

        JLabel jLabel3 = new JLabel("多项式:");
        jLabel3.setBounds(50, 350, 100, 30);
        this.add(jLabel3);
        CodeString = new JTextField();
        CodeString.setBounds(100, 350, 100, 30);

        this.add(jLabel1);
        this.add(jLabel2);
        this.add(Red);
        this.add(Green);
        this.add(CodeString);
    }


    private void generate() {
        c1.clear();
        c2.clear();
        count1 = 0;
        count2 = 0;
        String[] binaryNumbers = BinaryNumberGenerator.generateUniqueBinaryNumbers(100);
        data = new Object[binaryNumbers.length][columnNames.length];

        for (int i = 0; i < binaryNumbers.length; i++) {
            data[i][0] = i + 1;
            data[i][1] = binaryNumbers[i];
            data[i][2] = calculateCRC((String) data[i][1]);

        }
        DefaultTableModel newModel = new DefaultTableModel(data, columnNames);
        table.setModel(newModel); // 将新的TableModel设置给table对象
    }

    private void receive() {
        Red.setText("");
        Green.setText("");
        c1.clear();
        c2.clear();
        count1 = 0;
        count2 = 0;
        Random r = new Random();
        data1 = new Object[100][columnNames1.length];

        for (int i = 0; i < 100; i++) {
            data1[i][0] = data[i][0];
            data1[i][1] = (String) data[i][1];
        }

        c1 = new Vector<>();
        c2 = new Vector<>();

        for (int i = 0; i < 100; i++) {
            if (r.nextInt(100) < 10) {
                String temp = (String) data1[i][1];
                StringBuilder sb = new StringBuilder(temp);
                for (int j = 0; j < 8; j++) {
                    if (r.nextInt(10) < 2) {
                        sb.setCharAt(j, temp.charAt(j) == '0' ? '1' : '0');
                    }
                }
                data1[i][1] = sb.toString();

            }


            //计算是否有余数
            data1[i][2] = calculateCRC((String) data1[i][1] + data[i][2]);
            if (Integer.parseInt((String) data1[i][2]) == 0) data1[i][2] = "0";

            if (!data1[i][1].equals(data[i][1])) {
                if (data1[i][2].equals("0")) {
                    //改变第i行为绿色
                    c1.add(i);
                    count1++;
                } else {
                    //改变第i行为红色
                    c2.add(i);
                    count2++;
                }
            }
            data1[i][1] = (String) data1[i][1] + data[i][2];
        }

        // 创建自定义的单元格渲染器
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);


                if (c1.contains(row)) {
                    c.setForeground(Color.GREEN); // 行包含在 c1 中，设置为绿色
                } else if (c2.contains(row)) {
                    c.setForeground(Color.RED); // 行包含在 c2 中，设置为红色
                } else {
                    c.setForeground(Color.BLACK); // 其他行不变，设置为黑色
                }

                return c;
            }
        };

        // 设置表的默认渲染器
        table1.setDefaultRenderer(Object.class, renderer);
        table1.repaint();

        DefaultTableModel newModel = new DefaultTableModel(data1, columnNames1);

        table1.setModel(newModel); // 将新的TableModel设置给table对象
        Red.setText(String.valueOf(count2));
        Green.setText(String.valueOf(count1));
    }


    private static String calculateCRC(String data) {
        int[] dataArray = convertToBinaryArray(data);
        int[] divisor = convertToBinaryArray(CRC.CodeString.getText()); // G(x) = CRC-CCITT polynomial

        int[] remainder = divide(dataArray, divisor);

        StringBuilder crc = new StringBuilder();
        for (int bit : remainder) {
            crc.append(bit);
        }

        return crc.toString();
    }

    private static int[] convertToBinaryArray(String data) {
        int[] binaryArray = new int[data.length()];
        for (int i = 0; i < data.length(); i++) {
            binaryArray[i] = Character.getNumericValue(data.charAt(i));
        }
        return binaryArray;
    }

    private static int[] divide(int[] dividend, int[] divisor) {
        int[] quotient = new int[dividend.length + divisor.length - 1];
        System.arraycopy(dividend, 0, quotient, 0, dividend.length);

        for (int i = 0; i < dividend.length; i++) {
            if (quotient[i] == 1) {
                for (int j = 0; j < divisor.length; j++) {
                    quotient[i + j] ^= divisor[j];
                }
            }
        }

        int[] remainder = Arrays.copyOfRange(quotient, dividend.length, quotient.length);
        return remainder;
    }

}