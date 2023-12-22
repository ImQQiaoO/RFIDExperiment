import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HammingCheck extends JPanel {
    private String input = "";
    private String correctHamming = "";
    private final JLabel checkingBitsField = new JLabel();
    private String errorHamming = "";
    private final JLabel errorDetectField = new JLabel();
    private int errorPos = 0;

    public HammingCheck() {
        this.setLayout(null);
        init();
        insertImage();
    }

    private void insertImage() {
        // 插入图片
        ImageIcon imageIcon = new ImageIcon("src/image.png");
        imageIcon = new ImageIcon(imageIcon.getImage().getScaledInstance(700, 200, java.awt.Image.SCALE_SMOOTH));
        JLabel imageLabel = new JLabel(imageIcon);
        // 缩放图片
        imageLabel.setBounds(20, 200, 700, 200);
        this.add(imageLabel);
    }

    private void init() {
        // 创建文本输入框
        JTextField textField = new JTextField();
        textField.setBounds(20, 35, 200, 18);
        this.add(textField);
        // 文本输入框的上方提示文字
        JLabel labelTextField = new JLabel("请输入要发送的内容：");
        labelTextField.setBounds(20, 10, 200, 18);
        this.add(labelTextField);
        // 创建文本输入框
        JTextField correctHammingField = new JTextField();
        correctHammingField.setBounds(20, 75, 200, 18);
        this.add(correctHammingField);
        // 文本输入框的上方提示文字
        JLabel labelCorrectHammingField = new JLabel("正确的汉明校验码为：");
        labelCorrectHammingField.setBounds(20, 52, 200, 18);
        this.add(labelCorrectHammingField);
        // 展示校验位
        JLabel checkingBitsField = new JLabel("其中，校验位为：");
        checkingBitsField.setBounds(20, 92, 200, 18);
        this.add(checkingBitsField);
        // 设置按钮 计算
        JButton sendButton = new JButton("计算校验位");
        sendButton.setBounds(280, 35, 120, 18);
        this.add(sendButton);
        sendButton.addActionListener(e -> {
            // 获取文本输入框的内容
            input = textField.getText();
            // 判断输入是否合法
            boolean valid = checkValid(input);
            if (!valid) {
                JOptionPane.showMessageDialog(null, "输入不合法，请重新输入！", "提示", JOptionPane.ERROR_MESSAGE);
                return;
            }
            System.out.println(input);
            // 计算海明码
            correctHamming = calculateHammingCode(input);
            System.out.println(correctHamming);
            // 将得到的海明码显示在文本输入框中
            correctHammingField.setText(correctHamming);
            showCheckingBits(correctHamming);
        });
    }

    private boolean checkValid(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) != '0' && input.charAt(i) != '1') {
                return false;
            }
        }
        return true;
    }

    private String calculateHammingCode(String binary) {
        // check if the input string is a valid binary string
        int r = 0, m = binary.length();
        // calculate number of parity bits needed using 2^r >= m + r + 1
        while (Math.pow(2, r) < m + r + 1) {
            ++r;
        }
        // create hamming code with extra parity bits
        StringBuilder hammingCode = new StringBuilder();
        hammingCode.append("0".repeat(Math.max(0, m + r)));
        int j = 0;
        for (int i = 0; i < hammingCode.length(); ++i) {
            if (i == Math.pow(2, j) - 1) {
                j++;
            } else {
                hammingCode.setCharAt(i, binary.charAt(i - j));
            }
        }
        // calculate parity bits
        for (int i = 0; i < r; ++i) {
            int pos = (int) Math.pow(2, i);     // pos为奇偶校验位的位置
            int count = 0;//
            int k = pos - 1;
            while (k < hammingCode.length()) {  // 从奇偶校验位开始，每隔2*pos个位置，统计pos个数中1的个数
                for (int l = k; l < k + pos && l < hammingCode.length(); l++) { // 统计pos个数中1的个数
                    if (hammingCode.charAt(l) == '1') { // 如果是1，计数器加1
                        ++count;
                    }
                }
                k += 2 * pos;   // 跳过pos个数，从下一个pos个数开始统计
            }
            // if count of 1s is odd, set parity bit to 1
            if (count % 2 == 1) {   // 如果1的个数是奇数，奇偶校验位为1
                hammingCode.setCharAt(pos - 1, '1');
            }
        }
        return hammingCode.toString();
    }

    private void showCheckingBits(String correctHamming) {
        if (correctHamming.isEmpty())
            return;
        StringBuilder checkingString = new StringBuilder();
        // 清除之前的校验位提示
        checkingBitsField.setText("");
        for (int i = 0; i < correctHamming.length() - input.length(); ++i) {
            checkingString.append("p").append((int) Math.pow(2, i)).append(": ").append(correctHamming.charAt(i)).append(" ");
        }
        checkingBitsField.setText(checkingString.toString());
        checkingBitsField.setBounds(20, 105, 200, 18);
        this.add(checkingBitsField);
        // 设置按钮 模拟发生错误
        JButton errorTriggerButton = makeErrorButton();
        this.add(errorTriggerButton);
        this.revalidate();
        this.repaint();
    }

    private JButton makeErrorButton() {
        JButton errorTriggerButton = new JButton("模拟发生错误");
        errorTriggerButton.setBounds(20, 125, 120, 18);
        final JLabel[] errorHammingField = {new JLabel()};
        errorTriggerButton.addActionListener(e -> {
            // 首先清除之前的错误提示
            errorHammingField[0].setText("");
            errorHamming = triggerError();
            System.out.println(errorHamming);  // TODO delete
            errorHammingField[0] = new JLabel("发生错误后的海明码为：" + errorHamming);
            errorHammingField[0].setBounds(20, 145, 300, 18);
            errorDetectButton();
            errorTriggerButton.setBounds(20, 125, 120, 18);
            this.add(errorHammingField[0]);
            this.revalidate();
            this.repaint();
        });
        return errorTriggerButton;
    }

    private void errorDetectButton() {
        JButton errorDetectButton = new JButton("检测错误");
        errorDetectButton.setBounds(280, 145, 120, 18);
        this.add(errorDetectButton);
        errorDetectButton.addActionListener(e -> {
            int r = 0, m = errorHamming.length();
            // calculate number of parity bits
            while (Math.pow(2, r) < m + 1) {
                r++;
            }
            errorPos = getErrorPos(errorHamming, r);
            // Create a label to show the result
            errorDetectField.setBounds(20, 165, 600, 18);
            this.add(errorDetectField);
            // calculate parity bits
            if (errorPos == 0) {
                System.out.println("No error detected.");
                errorDetectField.setText("没有检查到错误");
            } else if (errorPos > m) {
                System.out.println("Two errors detected.");
                errorDetectField.setText("检查到两个错误");
            } else {
                System.out.println("错误位置在：" + errorPos);
                // correct the error
                char[] hammingCodeChars = errorHamming.toCharArray();
                hammingCodeChars[errorPos - 1] = hammingCodeChars[errorPos - 1] == '0' ? '1' : '0';
                String correctedHammingCode = new String(hammingCodeChars);
                System.out.println("修正后的海明码是 " + correctedHammingCode);
                errorDetectField.setText("错误位置在：" + errorPos + ". 修正后的海明码是：" + correctedHammingCode);
            }
        });
    }

    private int getErrorPos(String hammingCode, int r) {
        int errorPos = 0;
        for (int i = 0; i < r; i++) {
            int pos = (int) Math.pow(2, i);
            int count = 0;
            int k = pos - 1;
            while (k < hammingCode.length()) {  // 从奇偶校验位开始，每隔2*pos个位置，统计pos个数中1的个数
                for (int l = k; l < k + pos && l < hammingCode.length(); l++) { // 统计pos个数中1的个数
                    if (hammingCode.charAt(l) == '1') {
                        count++;
                    }
                }
                k += 2 * pos;   // 跳过pos个数，从下一个pos个数开始统计
            }
            // if count of 1s is odd, set parity bit to 1
            if (count % 2 == 1) {   // 如果1的个数是奇数，奇偶校验位为1
                errorPos += pos;    // 记录错误位置
            }
        }
        return errorPos;
    }

    private String triggerError() {
        Random rand = new Random();
        int errDigits = rand.nextInt(3); // Generate number of errors
        System.out.println("Number of errors: " + errDigits);

        int len = correctHamming.length();
        List<Integer> errPos = new ArrayList<>();
        System.out.print("Error position: ");
        while (errPos.size() < errDigits) {
            int pos = rand.nextInt(len); // Generate error position
            // Prevent duplicates
            if (!errPos.contains(pos)) {
                System.out.print((pos + 1) + " ");
                errPos.add(pos);
            }
        }
        // Flip the binary bit at the error position
        char[] hammingCodeChars = correctHamming.toCharArray();
        for (int pos : errPos) {
            hammingCodeChars[pos] = hammingCodeChars[pos] == '0' ? '1' : '0';
        }
        errorHamming = new String(hammingCodeChars);
        System.out.println("Error hamming code string: " + errorHamming);
        return errorHamming;
    }

}
