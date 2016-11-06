import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Calculator implements ActionListener {
	
	private JFrame calcWindow = new JFrame("Calculator");
	private String newLine = System.lineSeparator(); 
	private JTextField inputField = new JTextField();
	private JTextField errorField = new JTextField();
	private JTextField variableField = new JTextField();
	private JTextArea logAreaField = new JTextArea();
	private JLabel inputLabel = new JLabel("Input Expression:");
	private JLabel errorLabel = new JLabel("Error:");
	private JLabel variableLabel = new JLabel("For x:");
	private JLabel logAreaLabel = new JLabel("Log:");
	private JScrollPane logScrollPane = new JScrollPane(logAreaField);
	private String[] operators = {"n", "(", "^", "r", "*", "/", "+", "-"};
	private int[] 	 priority =  { 4,   3,   2,   2,   1,   1,   0,   0 };
	//							   0    1    2    3    4    5    6    7
	List<String> operatorList = Arrays.asList(operators);
	
	public Calculator() {
		calcWindow.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.weightx = 1.0;
		c.weighty = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		calcWindow.add(logAreaLabel, c);
		
		c.gridy = 1;
		c.ipady = 200;
		calcWindow.add(logScrollPane, c);
		
		c.gridy = 2;
		c.weighty = 0;
		c.ipady = 0;
		calcWindow.add(inputLabel, c);
		
		c.gridy = 3;
		calcWindow.add(inputField, c);
		
		c.gridy = 4;
		calcWindow.add(variableLabel, c);
		
		c.gridy = 5;
		calcWindow.add(variableField, c);
		
		c.gridy = 6;
		calcWindow.add(errorLabel, c);
		
		c.gridy = 7;
		c.ipady = 50;
		calcWindow.add(errorField, c);
		
		logAreaField.setEditable(false);
		errorField.setEditable(false);
		
		calcWindow.setSize(400, 450);
		calcWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		calcWindow.setVisible(true);
		
		inputField.addActionListener(this);
		variableField.addActionListener(this);
		
	}

	public static void main(String[] args) {
		
		new Calculator();

	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		
		String originalExpression = inputField.getText().trim();
		String expression = originalExpression.replace(" ", "");
		String variable = variableField.getText().trim();
		if ((expression.length() == 0)){
			System.out.println("You didn't enter an expression");
			errorField.setText("You didn't enter an expression");
			return;
		}
		if (expression.contains("x")){
			if (variable.length() == 0){
				System.out.println("You didn't define your x value.");
				errorField.setText("You didn't define your x value.");
				return;
			}
		}
		
		errorField.setText("");
		
		// Testing Simple Functions
		
		expression = variableSubstitution(expression, variable);
		expression = addUnary(expression);
		System.out.println("Your expression: " + expression);
		
		if (expression.contains("(") || expression.contains(")")){
			if(parenthesesCheck(expression)){
				logAreaField.append(handleParentheses(expression));
				expression = handleParentheses(expression);
			}
			else{
				errorField.setText("error");
			}
		}
		
		
		expression = complexSolve(expression);
		
		logAreaField.append(newLine + originalExpression + " = " + expression);
//		if(expression.contains("+")){
//			logAreaField.append(newLine + originalExpression + " = " + Add(expression));
//		}
//		else if(expression.contains("*")){
//			logAreaField.append(newLine + originalExpression + " = " + Multiply(expression));
//		}
//		else if(expression.contains("-")){
//			logAreaField.append(newLine + originalExpression + " = " + Minus(expression));
//		}
//		else if(expression.contains("/")){
//			logAreaField.append(newLine + originalExpression + " = " + divide(expression));
//		}
//		else if(expression.contains("r")){
//			logAreaField.append(newLine + originalExpression + " = " + root(expression));
//		}
//		else if(expression.contains("^")){
//			logAreaField.append(newLine + originalExpression + " = " + exponential(expression));
//		}
	}
	
	private String complexSolve(String expression){
		while(true){
			if (countOperators(expression) > 1){
				int theOp = getOperator(expression);
				String tempExpression = splitExpression(expression, operators[theOp]);
				String result = complexSolve(tempExpression);
				String replacement = expression.replace(tempExpression, result);
				expression = replacement;
			}
			else{
				String result = simpleSolve(expression);
				String replacement = expression.replace(expression, result);
				return replacement;
			}
		}
	}
	
	private int getOperator(String expression){
		int theOperator = -1;
		boolean stop = false;
		int highestPriority = 0;
		int count = 0;
		int opCount = countOperators(expression);
		Integer[] opIndexList = new Integer[opCount];
		Integer[] opPriorityList = new Integer[opCount];
		
		for(int i=0; i<expression.length(); i++){
			if(stop) break;
			for(int j=0; j<operators.length; j++){
				if(expression.charAt(i) == operators[j].charAt(0)){
					opIndexList[count] = j;
					opPriorityList[count++] = priority[j];
					break;
				}
			}
		}
		
		for(int i=0; i<opCount; i++){
			if(opPriorityList[i] > highestPriority){
				highestPriority = opPriorityList[i];
				theOperator = opIndexList[i];
			}
		}
		
		
		return theOperator;
	}
	
	private String splitExpression(String expression, String theOperator){
		int opPos, startPos = 0, endPos = 0;
		
		opPos = expression.indexOf(theOperator);
		
		//get starting position
		for(int i=0; i<operators.length; i++){
			startPos = expression.indexOf(operators[i]);
			if(startPos < opPos && startPos != -1){
				startPos++;	//add 1 here to set the position on the next character (not an operator)
				break;
			}
			else{
				startPos = -1;
			}
		}
		if(startPos == -1) startPos = 0;
		
		//get ending position
		for(int i=opPos+1; i<expression.length(); i++){
			if(i+1 > expression.length()) break;
			if(operatorList.contains(expression.substring(i, i+1))){
				endPos = i;
				break;
			}
			else{
				endPos = -1;
			}
		}
		if(endPos == -1) endPos = expression.length();
			
		return expression.substring(startPos, endPos);
	}
	
	private int countOperators(String expression){
		int opCount = 0;
		
		for(int i=0; i<operators.length; i++){
			opCount += expression.length() - expression.replace(operators[i], "").length();
		}
		
		return opCount;
	}
	
	private String variableSubstitution(String expression, String variable){
		
		if (expression.contains("pi")){
			String pi = Double.toString(Math.PI);
			expression = expression.replace("pi", pi);
		}
		if (expression.contains("e")){
			String e = Double.toString(Math.E);
			expression = expression.replace("e", e);
		}
		if (expression.contains("x")){
			expression = expression.replace("x", variable);
		}
		return expression;
	}
	
	private String simpleSolve(String expression){
		String temp = "";
		
		if(expression.contains("+")){
			temp = Add(expression);
		}
		else if(expression.contains("*")){
			temp = Multiply(expression);
		}
		else if(expression.contains("-")){
			temp = Minus(expression);
		}
		else if(expression.contains("/")){
			temp = divide(expression);
		}
		else if(expression.contains("r")){
			temp = root(expression);
		}
		else if(expression.contains("^")){
			temp = exponential(expression);
		}
		return temp;
	}
	
//	+ unary eliminator

	//	- unary -- add n
	public String addUnary(String expression){
		char [] expressionArray = expression.toCharArray();
		char temp = expressionArray[0];
		List<Integer> unaryLocation = new ArrayList<Integer>();
		
		if (temp == '-'){
			if ((expressionArray[1] != '*') || (expressionArray[1] != '/') || (expressionArray[1] != '+') ||
					(expressionArray[1] != '-') || (expressionArray[1] != 'r') || (expressionArray[1] != '^') ||
					(expressionArray[1] != '(') || (expressionArray[1] != ')')){
				unaryLocation.add(0);
			}
		}
		
		for(int i = 1; i < expressionArray.length; i++){
			if (expressionArray[i] == '-'){
				if ((temp == '*') || (temp == '/') || (temp == '+') || (temp == '-') || (temp == 'r') || (temp == '^')){
					unaryLocation.add(i);
				}
			}
			temp = expressionArray[i];
		}
		for(Integer j : unaryLocation){
			expression = expression.substring(0,j) + 'n' + expression.substring(j+1);
		}
		return expression;
	}
	
	//	- unary -- replace n
	public String[] replaceUnary(String [] expression){
		for (int i = 0; i < expression.length; i++){
			expression[i] = expression[i].replace('n', '-');
		}
		return expression;
	}

	//	find ()
	public String handleParentheses(String expression){
		
		int innerParentheses = expression.lastIndexOf('(');
		String temp = expression.substring(innerParentheses);
		String innerExpression = temp.substring(1, temp.indexOf(')'));
		
		return innerExpression;
	}
	
	// validate parentheses
	public boolean parenthesesCheck(String expression){
		
			char [] expressionArray = expression.toCharArray();
			int leftParenCount = 0, rightParenCount = 0;
			
			for(int i = 0; i < expressionArray.length; i++){
				if (expressionArray[i] == '('){
					leftParenCount++;
				}
				if (expressionArray[i] == ')'){
					rightParenCount++;
				}
			}
			
			if (leftParenCount!=rightParenCount){
				return false;
			}
			if (expression.indexOf('(') > expression.indexOf(')')){
				return false;
			}
			if (expression.lastIndexOf('(') > expression.lastIndexOf(')')){
				return false;
			}
			
			return true;
	}

	//	Solve ^
	public String exponential(String expression){
		String[] temp = expression.split("\\^");
		temp = replaceUnary(temp);
		double[] nums = new double[2];
		nums[0] = Double.parseDouble(temp[0]); // value to take root of
		nums[1] = Double.parseDouble(temp[1]); // nth root
		
		double rootValue = Math.pow(nums[0], nums[1]);
		
		return Double.toString(rootValue);
	}
	
	//	Solve r
	public String root(String expression){
		String[] temp = expression.split("r");
		temp = replaceUnary(temp);
		double[] nums = new double[2];
		nums[0] = Double.parseDouble(temp[0]); // value to take root of
		nums[1] = Double.parseDouble(temp[1]); // nth root
		
		double exponential = 1/nums[1];
		double rootValue = Math.pow(nums[0], exponential);
		
		return Double.toString(rootValue);
	}
	
	//	Solve *
		public String Multiply(String expression){
			String[] temp = expression.split("\\*");
			temp = replaceUnary(temp);
			String result;
			double[] nums = new double[2];
			double product;
			
			nums[0] = Double.parseDouble(temp[0]);
			nums[1] = Double.parseDouble(temp[1]);
			product = nums[0] * nums[1];
			result = Double.toString(product);
			
			return result;
			
		}
	//	Solve / 
	public String divide(String expression){
		String[] temp = expression.split("\\/");
		temp = replaceUnary(temp);
		double[] nums = new double[2];
		
		nums[0] = Double.parseDouble(temp[0]);
		nums[1] = Double.parseDouble(temp[1]);
		double dividend = nums[0] / nums[1];

		return Double.toString(dividend);
		
	}
		
	//	Solve + (Jeremy)
	public String Add(String expression){
		String[] temp = expression.split("\\+");
		temp = replaceUnary(temp);
		String result;
		double[] nums = new double[2];
		double sum;
		
		nums[0] = Double.parseDouble(temp[0]);
		nums[1] = Double.parseDouble(temp[1]);
		sum = nums[0] + nums[1];
		result = Double.toString(sum);
		
		return result;
	}

	//	Solve -
	public String Minus(String expression){
		String[] temp = expression.split("\\-");
		temp = replaceUnary(temp);
		String result;
		double[] nums = new double[2];
		double sum;
		
		nums[0] = Double.parseDouble(temp[0]);
		nums[1] = Double.parseDouble(temp[1]);
		sum = nums[0] - nums[1];
		result = Double.toString(sum);
		
		return result;
	}
	
	//	Return Boolean - Check there are no more operators

	public boolean checkForOperators(String expression){
		if(expression.contains("+") || expression.contains("-") || expression.contains("r") 
		|| expression.contains("^") || expression.contains("*") || expression.contains("/")
		|| expression.contains("n") || expression.contains("(") || expression.contains(")")
		|| expression.contains("e") || expression.contains("x") || expression.contains("pi")){
			return true;
			
		} else {
			return false;
		}
	}
	
	
	//	Space replacer

	public String removeBlanks(String expression){
		expression = expression.replaceAll("\\s", "");
		return expression;
	}
	
	//	Find end of expression

}
