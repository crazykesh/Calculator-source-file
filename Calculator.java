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
	private String[] operators = {"(", "^", "r", "*", "/", "+", "-"};
	private int[] 	 priority =  { 3,   2,   2,   1,   1,   0,   0 };
	//						       1    2    3    4    5    6    7
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
		System.out.println("ECE 309 - Fall 2016 - Lab 11");
		System.out.println("Team Members: Jeremy Swafford, Keshav Patel, Jacquelynn Drahuse");
		new Calculator();

	}

	@Override
	public void actionPerformed(ActionEvent ae) {	
		try {
			String originalExpression = inputField.getText().trim();
			String expression = originalExpression;
			
			
			/* Checking validity of argument */
			expressionsNotEmpty(originalExpression);
			
			 // ***** Should also remove extra spaces within this function & throw invalid - unary exception ****
			// I feel like this makes sense because you're already checking for invalid spaces between the - unary and its number
			expression = addUnary(expression);
			
			checkForPositiveUnary(expression);
			parenthesesCheck(expression);
			expressionOperatorsValid(expression);
			
			
			/* Solving Expression */
			// Replaces Variable
			String variable = variableField.getText().trim();
			expression = variableSubstitution(expression, variable);
			
			System.out.println("Your expression: " + expression);
			
			// Calls Solve Function
			expression = complexSolve(expression);
			logAreaField.append(newLine + originalExpression + " = " + expression);
			
		} catch (Exception e) {
			String message = e.getMessage();
			errorField.setText(message);
		}
		
		
	}
	
	/*
	 * FUNCTIONS FOR SOLVING EXPRESSION
	 */
	
	private String complexSolve(String expression){
		while(true){
			if (countOperators(expression) > 1){
				String innerExpression = handleParentheses(expression);
				int theOp = getOperator(innerExpression);
				String tempExpression = splitExpression(innerExpression, operators[theOp]);
				String result = complexSolve(tempExpression);
				String replacement = expression.replace(tempExpression, result);
				expression = replacement;
				expression = removeParenthes(expression);
			} else{
				String result = simpleSolve(expression);
				String replacement = expression.replace(expression, result);
				return replacement;
			}
		}
	}
	
	
	private int getOperator(String expression){
		int theOperator = -1;
		boolean stop = false;
		int highestPriority = -1;
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
	
	
	/*
	 * SIMPLE MATH FUNCTIONS
	 */
	
	// Positive (+) Unary Check
	public void checkForPositiveUnary(String expression){
		char [] expressionArray = expression.toCharArray();
		List<Character> charList = new ArrayList<Character>();
		for (char c: expressionArray){
			charList.add(c);
		}
		
		// Checks for positive unary for first character
		if(charList.get(0) == '+'){
			if ((charList.get(1) != '*') || (charList.get(1) != '/') || (charList.get(1) != '+') ||
				(charList.get(1) != '-') || (charList.get(1) != 'r') || (charList.get(1) != '^') ||
				(charList.get(1) != ')')){
				throw new IllegalArgumentException("Illegal '+' unary operator.");
			}
		}
		
		int length = expressionArray.length;
		for(int i = 1; i<length-1; i++){
			if(charList.get(i) == '+'){
				/* Checks if it is of positive operator
				 * (if previous character is an operator and the following character isn't an operator 
				 * e.g. *+3, r+4, etc.)
				 */
				if (((charList.get(i-1) == '*') || (charList.get(i-1) == '/') || (charList.get(i-1) == '+') ||
					(charList.get(i-1) == '-') || (charList.get(i-1) == 'r') || (charList.get(i-1) == '^')) 
					&&
					((charList.get(i+1) != '*') || (charList.get(i+1) != '/') || (charList.get(i+1) != '+') ||
					(charList.get(i+1) != '-') || (charList.get(i+1) != 'r') || (charList.get(i+1) != '^'))) {
					
					throw new IllegalArgumentException("Illegal '+' unary operator.");
				} 
			}
		}
	}
	
	
	//	Adding (n) for Negative Unary
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
	
	
	//	Replacing 'n' for the Negative Unary
	public String[] replaceUnary(String [] expression){
		for (int i = 0; i < expression.length; i++){
			expression[i] = expression[i].replace('n', '-');
		}
		return expression;
	}

	
	//	Finding Inner most ()
	public String handleParentheses(String expression){
		if(expression.contains("(")){
			int innerParentheses = expression.lastIndexOf('(');
			String temp = expression.substring(innerParentheses);
			String innerExpression = temp.substring(1, temp.indexOf(')'));
			return innerExpression;
		}
		return expression;
	}
	
	
	// Removing () After Inner Expression is solved
	public String removeParenthes(String expression){
		String tempExpression = handleParentheses(expression);
		if(checkForOperators(tempExpression)){
			return expression;
		}
		
		if(expression.contains("(")){
			char [] expressionArray = expression.toCharArray();
			// remove )
			List<Character> charList1 = new ArrayList<Character>();
			for (char c: expressionArray){
				charList1.add(c);
			}
			
			int parenthesesLocation = expression.lastIndexOf("(");
			charList1.remove(parenthesesLocation);
			
		    StringBuilder builder1 = new StringBuilder(charList1.size());
		    for(Character ch: charList1) {
		        builder1.append(ch);
		    }
		    expression = builder1.toString();
		    
		    // removing (
			expressionArray = expression.toCharArray();
			List<Character> charList2 = new ArrayList<Character>();
			for (char c: expressionArray){
				charList2.add(c);
			}
			
			parenthesesLocation = expression.indexOf(')');
			charList2.remove(parenthesesLocation);
			
		    StringBuilder builder2 = new StringBuilder(charList2.size());
		    for(Character ch: charList2) {
		        builder2.append(ch);
		    }
		    expression = builder2.toString();
		}
		return expression;
	}
	
	// Validate parentheses: throws exception if invalid
	public void parenthesesCheck(String expression){
		
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
				throw new IllegalArgumentException("Invalid parentheses operator.");
			}
			if (expression.indexOf('(') > expression.indexOf(')')){
				throw new IllegalArgumentException("Invalid parentheses operator.");
			}
			if (expression.lastIndexOf('(') > expression.lastIndexOf(')')){
				throw new IllegalArgumentException("Invalid parentheses operator.");
			}
			
	}

	
	//	Solves exponential: ^
	public String exponential(String expression){
		String[] temp = expression.split("\\^");
		temp = replaceUnary(temp);
		double[] nums = new double[2];
		nums[0] = Double.parseDouble(temp[0]); // value to take root of
		nums[1] = Double.parseDouble(temp[1]); // nth root
		
		double rootValue = Math.pow(nums[0], nums[1]);
		
		return Double.toString(rootValue);
	}
	
	
	//	Solves root: r
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
	
	
	// Solves multiplication: *
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
		
		
	//	Solves division: /
	public String divide(String expression){
		String[] temp = expression.split("\\/");
		temp = replaceUnary(temp);
		double[] nums = new double[2];
		
		nums[0] = Double.parseDouble(temp[0]);
		nums[1] = Double.parseDouble(temp[1]);
		if(nums[1] == 0){
			throw new IllegalArgumentException("Error: Cannot devide by zero");
		}
		
		double dividend = nums[0] / nums[1];

		return Double.toString(dividend);
		
	}
	
		
	//	Solves addition: + 
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

	
	//	Solves subtraction:  -
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
	
	
	// Checks for operators, returns true if expression still needs evaluation
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
	
	// Checks expression and x value is not empty
	public void expressionsNotEmpty(String originalExpression){
		String expression = originalExpression.replace(" ", "");
		
		// Checks string is not empty
		if ((expression.length() == 0)){
			System.out.println("You didn't enter an expression.");
			throw new IllegalArgumentException("You didn't enter an expression.");
		}
		
		// Checks x has a value
		String variable = variableField.getText().trim();
		if (expression.contains("x")){
			if (variable.length() == 0){
				System.out.println("You didn't define your x value.");
				throw new IllegalArgumentException("You didn't define your x value.");
			}
		}		
	}
	
	// Checks that operators are valid (multiple operators are not next to each other i.e. +*/ ) 
	public void expressionOperatorsValid(String expression){
		
		
	}
	
	
	//	Removes blanks/spaces from expression
	public String removeBlanks(String expression){
		expression = expression.replaceAll("\\s", "");
		return expression;
	}
	

}
