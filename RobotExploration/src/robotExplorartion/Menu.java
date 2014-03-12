package robotExplorartion;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * A menu for the Multi-Robot Simulation program
 * @author Paul Monk
 * @version 12/03/2014
 */
public class Menu extends JFrame
{
	private JRadioButton frontierAlgorithmButton;
	private JRadioButton proprietaryAlgorithmButton;
	private JRadioButton singleRobotButton;
	private JRadioButton uncoordinatedRobotButton;
	private JRadioButton coordinatedRobotButton;
	private JRadioButton obstacle1Button;
	private JRadioButton obstacle2Button;
	private JRadioButton obstacle5Button;
	private JTextArea threadTextArea;
	
	/**
	 * Creates the menu, which allows a simulation to be started
	 */
	public Menu()
	{
		//Set up JFrame
		this.setSize(700, 480);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Simulation - Robot Exploration");
		this.setLocationRelativeTo(null);
		
		//Set up menu panel
		JPanel panel = new JPanel();
		panel.setSize(this.getSize());
		panel.setBackground(Color.white);
		this.add(panel);
		
		//Set layout for the menu panel
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		panel.setLayout(layout);
		
		//Make the title
		JLabel titleLabel = new JLabel("Robot Exploration Simulation");
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 0;
		panel.add(titleLabel, c);
		
		//Make info text under title
		JLabel infoLabel = new JLabel("<HTML><CENTER>This is a simulation to test Multi-Robot Exploration using" +
				" various algorithms. Please select an algorithm and basic settings to run a simulation...</CENTER></HTML>");
		infoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 0;
		panel.add(infoLabel, c);
		
		//make selection panel with gray background
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(layout);
		selectionPanel.setBackground(new Color(220,220,220));
		c.insets = new Insets(20, 5, 5, 5);
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		panel.add(selectionPanel, c);
		
		//Make panel for algorithm selection
		JPanel algorithmPanel = new JPanel();
		algorithmPanel.setLayout(layout);
		algorithmPanel.setOpaque(false);
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 0;
		selectionPanel.add(algorithmPanel, c);
		
		//Add info label for algorithm selection
		JLabel algorithmLabel = new JLabel("Please select an algorithm:");
		algorithmLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.weightx = 1;
		c.weighty = 1;
		algorithmPanel.add(algorithmLabel, c);
		
		//Add frontier algorithm radio button
		frontierAlgorithmButton = new JRadioButton("Frontier Algorithm");
		frontierAlgorithmButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 1;
		algorithmPanel.add(frontierAlgorithmButton, c);
		
		//Add proprietary algorithm button
		proprietaryAlgorithmButton = new JRadioButton("My Proprietary Algorithm");
		proprietaryAlgorithmButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		algorithmPanel.add(proprietaryAlgorithmButton, c);
		
		//Group 2 algorithm selection buttons together so only one can be selected at a time
		ButtonGroup algorithmButtonGroup = new ButtonGroup();
		algorithmButtonGroup.add(frontierAlgorithmButton);
		algorithmButtonGroup.add(proprietaryAlgorithmButton);
		
		//Add panel for robot type selection
		JPanel robotPanel = new JPanel();
		robotPanel.setLayout(layout);
		robotPanel.setOpaque(false);
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 0;
		selectionPanel.add(robotPanel, c);
		
		//Add info label for robot type selection
		JLabel robotLabel = new JLabel("Please select the robot type:");
		robotLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.weightx = 1;
		c.weighty = 1;
		robotPanel.add(robotLabel, c);
		
		//Add single robot radio button
		singleRobotButton = new JRadioButton("Single Robot");
		singleRobotButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 1;
		robotPanel.add(singleRobotButton, c);
		
		//Add uncoordinated robots radio button
		uncoordinatedRobotButton = new JRadioButton("2 Uncoordinated Robots");
		uncoordinatedRobotButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 1;
		robotPanel.add(uncoordinatedRobotButton, c);
		
		//Add uncoordinated robots radio button
		coordinatedRobotButton = new JRadioButton("2 Coordinated Robots");
		coordinatedRobotButton.setFont(new Font("SansSerif", Font.PLAIN, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		robotPanel.add(coordinatedRobotButton, c);
		
		//Group 3 robot selection buttons together so only one can be selected at once
		ButtonGroup robotButtonGroup = new ButtonGroup();
		robotButtonGroup.add(singleRobotButton);
		robotButtonGroup.add(uncoordinatedRobotButton);
		robotButtonGroup.add(coordinatedRobotButton);
		
		//Add panel for obstacle probability selection
		JPanel obstaclePanel = new JPanel();
		obstaclePanel.setLayout(layout);
		obstaclePanel.setOpaque(false);
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 0;
		selectionPanel.add(obstaclePanel, c);
		
		//Add info label for obstacle probability selection
		JLabel obstacleLabel = new JLabel("Please select the probablity of obstacles occuring:");
		obstacleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 3;
		c.weightx = 1;
		c.weighty = 1;
		obstaclePanel.add(obstacleLabel, c);
		
		//Add 1% obstacle probability button
		obstacle1Button = new JRadioButton("1% Obstacles");
		obstacle1Button.setFont(new Font("SansSerif", Font.PLAIN, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 1;
		obstaclePanel.add(obstacle1Button, c);
		
		//Add 2% obstacle probability button
		obstacle2Button = new JRadioButton("2% Obstacles");
		obstacle2Button.setFont(new Font("SansSerif", Font.PLAIN, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 1;
		obstaclePanel.add(obstacle2Button, c);
		
		//Add 5% obstacle probability button
		obstacle5Button = new JRadioButton("5% Obstacles");
		obstacle5Button.setFont(new Font("SansSerif", Font.PLAIN, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		obstaclePanel.add(obstacle5Button, c);
		
		//Groups obstacle buttons together so only one can be selected at a time
		ButtonGroup obstacleButtonGroup = new ButtonGroup();
		obstacleButtonGroup.add(obstacle1Button);
		obstacleButtonGroup.add(obstacle2Button);
		obstacleButtonGroup.add(obstacle5Button);
		
		//Add panel for the number of threads to start
		JPanel threadPanel = new JPanel();
		threadPanel.setLayout(layout);
		threadPanel.setOpaque(false);
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 0;
		c.gridy = 3;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 0;
		selectionPanel.add(threadPanel, c);
		
		//Add info label for obstacle probability selection
		JLabel threadLabel = new JLabel("Please choose the number of simulations to start:");
		threadLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 0;
		c.weighty = 1;
		threadPanel.add(threadLabel, c);
		
		threadTextArea = new JTextArea();
		threadTextArea.setFont(new Font("SansSerif", Font.BOLD, 16));
		threadTextArea.setText("1");
		c.insets = new Insets(10, 5, 0, 5);
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		threadPanel.add(threadTextArea, c);
		
		//Add panel for Start/Close buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(layout);
		buttonPanel.setOpaque(false);
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 0;
		c.gridy = 4;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		selectionPanel.add(buttonPanel, c);
		
		//Add start button, which starts gthe simulation using the selected variables
		JButton startButton = new JButton("Start");
		startButton.setFont(new Font("SansSerif", Font.BOLD, 16));
		startButton.setBackground(new Color(0,200,0));
		c.insets = new Insets(20, 5, 0, 5);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 0.2;
		c.weighty = 1;
		buttonPanel.add(startButton, c);
		
		//Add a close button, which closes the application
		JButton closeButton = new JButton("Close");
		closeButton.setFont(new Font("SansSerif", Font.BOLD, 16));
		closeButton.setBackground(new Color(235,0,0));
		c.insets = new Insets(20, 5, 0, 5);
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 0.2;
		c.weighty = 1;
		buttonPanel.add(closeButton, c);
		
		//Adds padding to make sure the Start/Close buttons don't become too wide
		JPanel paddingPanel = new JPanel();
		paddingPanel.setOpaque(false);
		c.insets = new Insets(20, 5, 0, 5);
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 1;
		buttonPanel.add(paddingPanel, c);
	
		//Add action listeners to Start/Close buttons
		MyActionListener actionListener = new MyActionListener();
		startButton.addActionListener(actionListener);
		closeButton.addActionListener(actionListener);
		
		//Make menu visible
		this.setVisible(true);
		this.requestFocus();
	}//constructor
	
	/**
	 * Shows a popup window displaying the message input
	 * @param messageIn The message to be displayed
	 */
	public void displayMessageDialog(String messageIn)
	{
		StringBuilder builder = new StringBuilder(messageIn);
		int index = -1;
		
		//Makes sure one line of the message isn't longer than 50 characters
		for(int a=50; messageIn.length() > a; a=a+50)
		{
			index = builder.indexOf(" ", a);
			
			if(index >= 0)
			{
				builder.replace(index, index+1, "\n");
			}//if
		}//for
		
		JOptionPane.showMessageDialog(new JFrame(), builder.toString());
	}//displayMessage
	
	/**
	 * A custom action listner for the buttons of the menu
	 * @author Paul Monk
	 * @version 12/03/2014
	 */
	public class MyActionListener implements ActionListener
	{
		/**
		 * Run when a button is clicked (either the Start or Close buttons)
		 * @param event The button event
		 */
		@Override
		public void actionPerformed(ActionEvent event)
		{
			if(event.getActionCommand() == "Start")
			{
				boolean proprietaryAlgorithm;
				boolean twoRobots;;
				boolean coordinated;
				int obstacleProbability;
				int noOfSimulations;
				
				//Check which algorithm has been selected
				if(frontierAlgorithmButton.isSelected())
				{
					proprietaryAlgorithm = false;
				}//if
				else if(proprietaryAlgorithmButton.isSelected())
				{
					proprietaryAlgorithm = true;
				}//else if
				else
				{
					displayMessageDialog("Please select an algorithm to be used");
					return;
				}//else
				
				//Check which robot selection has been made
				if(singleRobotButton.isSelected())
				{
					twoRobots = false;
					coordinated = false;
				}//if
				else if(uncoordinatedRobotButton.isSelected())
				{
					twoRobots = true;
					coordinated = false;
				}//else if
				else if(coordinatedRobotButton.isSelected())
				{
					twoRobots = true;
					coordinated = true;
				}//else if
				else
				{
					displayMessageDialog("Please select a robot type to be used");
					return;
				}//else
				
				//Check which obstacle probability selection has been made
				if(obstacle1Button.isSelected())
				{
					obstacleProbability = 1;
				}//if
				else if(obstacle2Button.isSelected())
				{
					obstacleProbability = 2;
				}//else if
				else if(obstacle5Button.isSelected())
				{
					obstacleProbability = 5;
				}//else if
				else
				{
					displayMessageDialog("Please select an obstacle probability to use");
					return;
				}//else
				
				//Checks how many simulations should be started
				if(threadTextArea.getText().matches("[0-9]*"))
				{
					noOfSimulations = Integer.parseInt(threadTextArea.getText());
				}//if
				else
				{
					displayMessageDialog("Please select a valid number of simulations to start");
					return;
				}//else
				
				//starts the simulations using selected settings
				for(int a=0; a<noOfSimulations; a++)
				{
					new Thread(new SimulationThread(a+1, proprietaryAlgorithm, 
							twoRobots, coordinated, obstacleProbability)).run();
				}//for
			}//if
			else
			{
				System.exit(0);
			}//else
		}//actionPerformend
	}//MyActionListener
}//end
