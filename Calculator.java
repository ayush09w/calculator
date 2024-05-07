import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class Calculator extends JFrame {

    private JTextField display;
    private StringBuilder currentInput;
    private MathCalc mathCalc; // Add this line

    public Calculator() {
        setTitle("Swing Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);

        currentInput = new StringBuilder();
        mathCalc = new MathCalc(); // Add this line

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createDisplayPanel(), BorderLayout.NORTH);
        mainPanel.add(createButtonPanel(), BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createDisplayPanel() {
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        display = new JTextField(20);
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.PLAIN, 20));

        displayPanel.add(display);
        return displayPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4, 5, 5));

        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", "(", ")", "+",
                "C", "="
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.PLAIN, 18));
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        return buttonPanel;
    }

    private class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();

            if (command.equals("=")) {
                int result = evaluateExpression(currentInput.toString());
                display.setText(String.valueOf(result));
                currentInput.setLength(0);
                currentInput.append(result);
            } else if (command.equals("C")) {
                currentInput.setLength(0);
                display.setText("");
            } else {
                currentInput.append(command);
                display.setText(currentInput.toString());
            }
        }

        private int evaluateExpression(String expression) {
            Stack<Integer> valueStack = new Stack<>();
            Stack<Character> operatorStack = new Stack<>();
            String buff = "";

            for (char ch : expression.toCharArray()) {
                if (ch == '(') {
                    operatorStack.push(ch);
                } else if (ch == ')') {
                    if (!buff.isEmpty()) {
                        valueStack.push(Integer.parseInt(buff));
                        buff = "";
                    }
                    while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                        valueStack.push(mathCalc.compute(valueStack.pop(), valueStack.pop(), operatorStack.pop()));
                    }
                    operatorStack.pop(); // Pop '('
                } else if (mathCalc.isOperator(ch)) {
                    if (!buff.isEmpty()) {
                        valueStack.push(Integer.parseInt(buff));
                        buff = "";
                    }

                    while (!operatorStack.isEmpty() && mathCalc.level(ch) <= mathCalc.level(operatorStack.peek())) {
                        valueStack.push(mathCalc.compute(valueStack.pop(), valueStack.pop(), operatorStack.pop()));
                    }

                    operatorStack.push(ch);
                } else {
                    buff += ch;
                }
            }

            if (!buff.isEmpty()) {
                valueStack.push(Integer.parseInt(buff));
            }

            while (!operatorStack.isEmpty()) {
                valueStack.push(mathCalc.compute(valueStack.pop(), valueStack.pop(), operatorStack.pop()));
            }

            return valueStack.pop();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Calculator calculator = new Calculator();
            calculator.setVisible(true);
        });
    }
}

class MathCalc {
    public int compute(int operand2, int operand1, char operator) {
        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                if (operand2 != 0) {
                    return operand1 / operand2;
                } else {
                    throw new ArithmeticException("Cannot divide by zero");
                }
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

    public boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    public int level(char ch) {
        switch (ch) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 0;
        }
    }
}
