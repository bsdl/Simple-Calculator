import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Stack;
import java.awt.event.ActionEvent;

public class Calculator extends JFrame implements ActionListener{
	
	private static JTextField textField;
	private static boolean isFirstDigit = true;
	
	public static void main(String[] args) {
		Calculator gui = new Calculator();
		// Set frame visible
		gui.setVisible(true);
	}
	
	// Constructor, initial UI for the calculator 
	public Calculator() {		
		setTitle("Simple Calculator");
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   
	    setBounds(100, 100, 340, 270);    
	    setResizable(false);
	    JPanel calculatorPanel = new JPanel();   
	    calculatorPanel.setBackground(Color.darkGray);
	    calculatorPanel.setBorder(new EmptyBorder(5,5,5,5)); 
	    calculatorPanel.setLayout(new BorderLayout(0,0));  	    
	    setContentPane(calculatorPanel);   
	    
	    JPanel textPanel = new JPanel();    
	    calculatorPanel.add(textPanel, BorderLayout.NORTH);   
	    textField = new JTextField();    
	    textField.setHorizontalAlignment(SwingConstants.RIGHT);  
	    
	    textPanel.add(textField);       
	    textField.setColumns(16);
	    
	    JPanel btnPanel = new JPanel();
	    calculatorPanel.add(btnPanel, BorderLayout.CENTER);
	    btnPanel.setLayout(new GridLayout(4,4,5,5));
		// Button array
		String buttonText[] = {"7","8","9","+","4","5","6","-","1","2","3","*","C","0","DEL","/"};
		JButton btn[] = new JButton[16];		
		for(int i = 0; i < 16; i++) {
			btn[i] = new JButton(buttonText[i]);
			btn[i].setBackground(Color.white);
			btn[i].addActionListener(this); // Add listener for every button
			btnPanel.add(btn[i]);
		}
		// Add equal button
		JPanel equalBtnPanel = new JPanel();
		calculatorPanel.add(equalBtnPanel, BorderLayout.SOUTH);
		JButton equalBtn = new JButton("=");
		equalBtn.addActionListener(this); // Add listener for equal button
		equalBtn.setBackground(Color.GREEN);
		equalBtn.setPreferredSize(new Dimension(340, 30));
		equalBtnPanel.add(equalBtn);
	}
	
	// Event processing, add different function to each button
	public void actionPerformed(ActionEvent event) {		
		String btnText = event.getActionCommand(); // Get the certain button that was clicked
		if("0123456789+-*/".contains(btnText)) {
			getOnScreen(btnText);       // Add the button text into the text field               
		}
		else if(btnText.equals("C")) {
			clearTextField();    // Empty the text field
		}
		else if(btnText.equals("DEL")) {
			backSpace();         // Backspace in the text field
		}
		else {
			textField.setText(getResult());        // Click "=", get the result
		}
	}
	
	// Display numbers and operators in a text field
	private void getOnScreen(String btnText) {
		if(isFirstDigit) {
			if("+*/".contains(btnText)) {
				isFirstDigit = true;
			}else {				
				textField.setText(btnText);
				isFirstDigit = false;
			}
		}else if(!(textField.getText().endsWith("+") ||
				textField.getText().endsWith("-") ||
				textField.getText().endsWith("*") ||
				textField.getText().endsWith("/"))){
			textField.setText(textField.getText() + btnText);
			isFirstDigit = false;
		}else {
			if("+-*/".contains(btnText)) {
				textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
				textField.setText(textField.getText() + btnText);
			}else {
				textField.setText(textField.getText() + btnText);
			}
			isFirstDigit = false;
		}	
	}
	
	// Add backspace function
	private void backSpace() {
		try {
			textField.setText(textField.getText().substring(0, textField.getText().length() - 1));
			if(textField.getText().isEmpty()) {
				isFirstDigit = true;
			}
		}catch(Exception ex){			
		}
		
	}
	
	// clear text field
	private void clearTextField() {
		textField.setText("");
		isFirstDigit = true;		
	}

	// Get the result of the calculation
	private String getResult() {
		String result = "";
		String[] postfix = infixToPost();
		result = String.valueOf(getPostResult(postfix));
		return result;
	}
	
	// Calculate the value of the suffix expression
	private double getPostResult(String[] postfix) {
		Stack<Double> stack = new Stack<Double>();
		int len = 0;
		int i = 0;
		while(postfix[i] != null) {
			len++;
			i++;
		}
		// Traversing the suffix expression, encountering a number, pushing it into the stack.
		// Encountering an operator, pop the 2 numbers on the top of the stack and calculate them with the operator		
		for(int j = 0; j < len; j++) {
			if(checkNum(postfix[j])) {
				stack.push(Double.valueOf(postfix[j]));
			}else {
				double opNum2 = stack.pop();
				double opNum1 = stack.pop();
				stack.push(dealOperator(opNum1, opNum2, postfix[j]));
			}
		}
		return stack.pop();
		
	}

	// Deal with different operator
	private double dealOperator(double opNum1, double opNum2, String op) {
		double resultNum = 0.0;
		switch(op) {
		case "+":
			resultNum = opNum1 + opNum2;
			break;
		case "-":
			resultNum = opNum1 - opNum2;
			break;
		case "*":
			resultNum = opNum1 * opNum2;
			break;
		case "/":
			if(!(opNum2 == 0.0)) {
				resultNum = opNum1 / opNum2;
			}
			break;
		default:
		}
		return resultNum;
		
	}

	// Determine the priority of the operator
	private boolean checkPriority(String op1, String op2) {
		if((op1 == "+" || op1 == "-") && (op2 == "*" || op2 == "/")) {
			return false;
		}else if(op1 == op2) {
			return true;
		}else {
			return true;
		}	
	}
	
	// Determine if the character is a number
	private boolean checkNum(String s) {
		char[] ch = s.toCharArray();
		int i = 0;
		while(i < ch.length) {
			int num = ch[i];
			i++;
			if(!(num - 48 >= 0 && num - 48 <= 9)) {
				return false;
			}				
		}
		return true;
	}
	
	// Convert the infix expression to a postfix expression
	private String[] infixToPost() {
		char[] infix =  textField.getText().toCharArray();
		String[] postfix = new String[100]; 
		Stack<String> stack = new Stack<String>();
		String temp = "";
		int k = 0;
		int j = 0;
		for(int i = 0; i < infix.length; i++) {
			j = i;
			if(stack.empty()) {
				if(checkNum(String.valueOf(infix[i]))) {
					while(checkNum(String.valueOf(infix[j]))) {
						temp += infix[j];
						j++;
						i = j - 1;
					}
					postfix[k++] = temp;
					temp = "";
				}else {
					stack.push(String.valueOf(infix[i]));
				}	
			}else if(!checkNum(String.valueOf(infix[i]))){
				while(checkPriority(stack.peek(), String.valueOf(infix[i])) && !stack.empty()) {
					postfix[k++] = stack.pop();
				}
				stack.push(String.valueOf(infix[i]));
			}else {
				while(checkNum(String.valueOf(infix[j]))) {
					temp += infix[j];
					j++;
					i = j - 1;
					if(j >= infix.length) break;
				}
				postfix[k++] = temp;
				temp = "";
			}
		}
		while(!stack.empty()) {
			postfix[k++] = stack.pop();
		}
		return postfix;
	}
}
