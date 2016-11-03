import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class Calculator implements ActionListener {
	
	private JFrame calcWindow = new JFrame("Calculator");
	private JTextField inputField = new JTextField();
	private JTextField errorField = new JTextField();
	private JTextField variableField = new JTextField();
	private JTextField logAreaField = new JTextField();
	private JLabel inputLabel = new JLabel("Input Expression:");
	private JLabel errorLabel = new JLabel("Error:");
	private JLabel variableLabel = new JLabel("For x:");
	private JLabel logAreaLabel = new JLabel("Log:");
	private JScrollPane logScrollPane = new JScrollPane(logAreaField);

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
		
		String expression = inputField.getText().trim();
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
		
		expression = variableSubstitution(expression, variable);
		System.out.println("Your expression: " + expression);
		
		if(expression.contains("+")){
			logAreaField.setText(expression + " = " + Add(expression));
		}
		else if(expression.contains("*")){
			logAreaField.setText(expression + " = " + Multiply(expression));
		}
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
	
//	+ unary eliminator

	//	- unary -- add n

	//	- unary -- replace n

	//	Replace x variable

	//	find ()

	//	Solve ^

	//	Solve r

	//	Solve *
		public String Multiply(String expression){
			String[] temp = expression.split("\\*");
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

	//	Solve + (Jeremy)
	public static String Add(String expression){
		String[] temp = expression.split("\\+");
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

	//	Return Boolean - Check there are no more operators

	//	Space replacer

	//	Find end of expression

}
