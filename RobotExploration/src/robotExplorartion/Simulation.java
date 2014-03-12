package robotExplorartion;

import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.*;

/**
 * A Simulation Program to test Multi-Robot Exploration of a Map, it is thread safe
 * @author Paul Monk
 * @version 12/03/2014
 */

public class Simulation extends JPanel
{
	//Size of the simulation frame in pixels
	private final int frameWidth = 750;
	private final int frameHeight = 525;
	//The number of pixels per square of the map
	private final int pixelsPerSquare = 15;
	//The coordinates of the map, worked out according to how many pixels there are per square
	private final int coordinatesX = (frameWidth/pixelsPerSquare);
	private final int coordinatesY = (frameHeight/pixelsPerSquare) - 2;
	//holds coordinate info, false means no obstacle, true means there is an obstacle present at that location
	private boolean[][] originalCoordinates = new boolean[coordinatesX][coordinatesY];
	//holds info on searched coordinates, false means no obstacle, true means there is an obstacle
	private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, PointStatusEnum>> searchedCoordinates = 
			new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, PointStatusEnum>>();
	//The robots
	private Robot robot1;
	private Robot robot2;
	//The number of the thread this program is running on (used when several versions are running at once)
	int threadNumber;
	//used for buffering the graphics
	private Image bufferImage; 
	private Graphics bufferGraphics;
	//Thread to run the simulation
	private Thread thread1;
	private Thread thread2;
	
	private boolean twoRobots;
	private boolean coordinated;
	private int obstacleProbability;
	
	/**
	 * Sets up and starts the Simulation, creating the map and positioning the robots 
	 * then making the threads for each robot and running them
	 * @param threadNumberIn The number of this thread (to distinguish between different simulations running at the same time)
	 * @param proprietaryAlgorithmIn which exploration algorithm should be used (true for proprietary, false for frontier)
	 * @param twoRobotsIn True if 2 robots are running in the simulation, false if just 1 robot is running
	 * @param coordinatedIn True if the robots will coordinate with each other in the simulation, false otherwise
	 * @param obstacleProbabilityIn The chance of obstacles occurring (0-100%)
	 */
	public Simulation(int threadNumberIn, boolean proprietaryAlgorithmIn, boolean twoRobotsIn,
			boolean coordinatedIn, int obstacleProbabilityIn)
	{	
		threadNumber = threadNumberIn;
		twoRobots = twoRobotsIn;
		coordinated = coordinatedIn;
		obstacleProbability = obstacleProbabilityIn;
		
		//Makes left and right of screen an obstacle
		for(int a=0; a<coordinatesX; a++)
		{
			originalCoordinates[a][0] = true;
			originalCoordinates[a][coordinatesY-1] = true;
		}//for
		
		//Makes top and bottom of screen an obstacle
		for(int a=0; a<coordinatesY; a++)
		{
			originalCoordinates[0][a] = true;
			originalCoordinates[coordinatesX-1][a] = true;
		}//for
		
		//Makes random obstacles spread over the rest of the map
		Random rnd = new Random();
		for(int a=2; a<coordinatesX-1; a++)
		{
			for(int b=1; b<coordinatesY-1; b++)
			{
				if(rnd.nextInt(100/obstacleProbability) == 0)
				{
					originalCoordinates[a][b] = true;
				}//if
			}//for
		}//for
		
		//creates the robots
			robot1 = new Robot("Robot 1", new Point(15, 15), pixelsPerSquare, DirectionEnum.SOUTH);
		if(twoRobots)
		{
			robot2 = new Robot("Robot 2", new Point(720, 465), pixelsPerSquare, DirectionEnum.NORTH);
		}//if
		
		JFrame frame = new JFrame();
		frame.setSize(frameWidth+250, frameHeight);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Simulation - Robot Exploration");
		frame.setLocationRelativeTo(null);
		
		JScrollPane scrollPane = new JScrollPane(this);
		scrollPane.setSize(frame.getHeight(), frame.getWidth());
		
		frame.add(scrollPane);
		frame.setVisible(true);
		frame.requestFocus();

		//creates the simulation threads
		thread1 = new Thread(new SimulationLoopThread(robot1, proprietaryAlgorithmIn));
		if(twoRobots)
		{
			thread2 = new Thread(new SimulationLoopThread(robot2, proprietaryAlgorithmIn));
		}//if
	}//constructor
	
	/**
	 * Starts the simulation
	 */
	public void startSimulation()
	{
		thread1.start();
		if(twoRobots)
		{
			thread2.start();
		}//if
	}//startSimulation
	
	/**
	 * Prints out the number of steps a robot has travelled, once it has finished exploring
	 * @param robotIn The robot that has finished exploring
	 * @param noOfSteps The number of steps that robot took to explore
	 */
	public void endSimulation(Robot robotIn, int noOfSteps)
	{
		System.out.println("**** Thread Number: " + threadNumber);
		System.out.println(robotIn.getName() + " steps = " + noOfSteps);
	}//endSimulation
	
	/**
	 * Paints the graphics for the simulation
	 */
	@Override
	public void paint(Graphics g)
	{
		Graphics2D g2D = (Graphics2D) g;
		
		g2D.setColor(Color.white);
		g2D.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		//Paints the map
		for(int x=0; x<coordinatesX; x++)
		{
			for(int y=0; y<coordinatesY; y++)
			{
				if(originalCoordinates[x][y])//if there is an obstacle paint it red
				{
					g2D.setColor(Color.red);
					g2D.fillRect(x*pixelsPerSquare, y*pixelsPerSquare, pixelsPerSquare, pixelsPerSquare);
				}//if
				else//if the area is empty paint it black
				{
					g2D.setColor(Color.black);
					g2D.fillRect(x*pixelsPerSquare, y*pixelsPerSquare, pixelsPerSquare, pixelsPerSquare);
				}//else
			}//for
		}//for
		
		//paints the explored areas of the map
		for (Map.Entry<Integer, ConcurrentHashMap<Integer, PointStatusEnum>> xCoordEntry : searchedCoordinates.entrySet())
		{
			for (Map.Entry<Integer, PointStatusEnum> yCoordEntry : xCoordEntry.getValue().entrySet())
			{
				if(yCoordEntry.getValue() == PointStatusEnum.UNEXPLORED)//unexplored points painted gray
				{
					g2D.setColor(Color.gray);
					g2D.fillRect(xCoordEntry.getKey()*pixelsPerSquare, yCoordEntry.getKey()*pixelsPerSquare, 
								pixelsPerSquare, pixelsPerSquare);
				}//if
				else if(yCoordEntry.getValue() == PointStatusEnum.OBSTACLE)//obstacles painted pink
				{
					g2D.setColor(Color.blue);
					g2D.fillRect(xCoordEntry.getKey()*pixelsPerSquare, yCoordEntry.getKey()*pixelsPerSquare, 
							pixelsPerSquare, pixelsPerSquare);
				}//else if
				else//open space painted white
				{
					g2D.setColor(Color.white);
					g2D.fillRect(xCoordEntry.getKey()*pixelsPerSquare, yCoordEntry.getKey()*pixelsPerSquare, 
							pixelsPerSquare, pixelsPerSquare);
				}//else
			}//for
		}//for
		
		robot1.paint(g);
		if(twoRobots)
		{
			robot2.paint(g);
		}//if
		
		//writes information at the right hand side of the window
		g2D.setFont(new Font("SansSerif", Font.BOLD, 16));
		g2D.setColor(Color.red);
		g2D.fillRect(760, 13, 20, 20);
		g2D.setColor(Color.black);
		g2D.drawString("= Unexplored Obstacle", 790, 30);
		
		g2D.setColor(Color.black);
		g2D.fillRect(760, 43, 20, 20);
		g2D.setColor(Color.black);
		g2D.drawString("= Unexplored Open Space", 790, 60);
		
		g2D.setColor(Color.blue);
		g2D.fillRect(760, 73, 20, 20);
		g2D.setColor(Color.black);
		g2D.drawString("= Explored Obstacle", 790, 90);
		
		g2D.setColor(Color.black);
		g2D.drawRect(760, 103, 20, 20);
		g2D.setColor(Color.black);
		g2D.drawString("= Explored Open Space", 790, 120);
		
		g2D.setColor(Color.gray);
		g2D.fillRect(760, 133, 20, 20);
		g2D.setColor(Color.black);
		g2D.drawString("= Frontier Point", 790, 150);
		
		int[] xPoints = {780, 760, 760};
		int[] yPoints = {173, 183, 163};
		g2D.setColor(Color.cyan);
		g2D.fillPolygon(xPoints, yPoints, 3);
		g2D.setColor(Color.black);
		g2D.drawString("= Robot", 790, 180);
		
		if(twoRobots)
		{
			g2D.setColor(Color.black);
			g2D.drawString("Total Steps = " + (robot1.noOfSteps + robot2.noOfSteps), 760, 210);
			g2D.setColor(Color.black);
			g2D.drawString("Robot 1 Steps = " + robot1.noOfSteps, 760, 240);
			g2D.setColor(Color.black);
			g2D.drawString("Robot 2 Steps = " + robot2.noOfSteps, 760, 270);
		}//if
		else
		{
			g2D.setColor(Color.black);
			g2D.drawString("Total Steps = " + (robot1.noOfSteps), 760, 210);
			g2D.setColor(Color.black);
			g2D.drawString("Robot 1 Steps = " + robot1.noOfSteps, 760, 240);
		}//else
	}//paint
	
	/**
	 *  This updates the graphics when repaint is called, implements double buffering 
	 */ 
	@Override
	public void update(Graphics g) 
	{
		if(bufferImage == null)
		{
			bufferImage = createImage(this.getWidth(), this.getHeight()); 
			bufferGraphics = bufferImage.getGraphics(); 
		}//if
		
		//clear screen in background 
		bufferGraphics.setColor(getBackground()); 
		bufferGraphics.fillRect(0, 0, this.getWidth(), this.getHeight()); 

		//draw elements to the background image
		paint(bufferGraphics); 

		//draw image on the screen 
		g.drawImage(bufferImage, 0, 0, this); 
	}//update 
	
	/**
	 * Controls the robot using the frontier based algorithm
	 * @param robotIn the robot to be controlled
	 */
	public void frontierAlgorithmLoop(Robot robotIn)
	{
		boolean loop = true;
		int robotStartXCoord = (robotIn.getCoordinates().x)/pixelsPerSquare;
		int robotStartYCoord = (robotIn.getCoordinates().y)/pixelsPerSquare;
		searchedCoordinates.put(robotStartXCoord, new ConcurrentHashMap<Integer, PointStatusEnum>());
		searchedCoordinates.get(robotStartXCoord).put(robotStartYCoord, PointStatusEnum.OPEN);
		boolean frontObstacle;
		boolean leftObstacle;
		boolean rightObstacle;
		boolean previouslySearchedFront;
		boolean previouslySearchedLeft;
		boolean previouslySearchedRight;
		ArrayList<Point> currentPath = null;
		Point nextPoint = null;
		
		while(loop)
		{
			int robotXCoord = (robotIn.getCoordinates().x)/pixelsPerSquare;
			int robotYCoord = (robotIn.getCoordinates().y)/pixelsPerSquare;
			
			//Search area surrounding the robot (simulated ultrasonic sensors)
			previouslySearchedFront = previouslySearchedFront(robotIn);
			previouslySearchedLeft = previouslySearchedLeft(robotIn);
			previouslySearchedRight = previouslySearchedRight(robotIn);
			frontObstacle = obstacleInFront(robotIn);
			leftObstacle = obstacleToLeft(robotIn);
			rightObstacle = obstacleToRight(robotIn);
			
			if(nextPoint != null)//if robot has a next point to go to
			{
				if(nextStepToPoint(robotIn, nextPoint))//move a step towards the next point
				{
					//if point is reached then remove it
					nextPoint = null;
				}//if
				
				robotIn.noOfSteps ++;
				
				this.repaint();
				
				try
				{
					Thread.sleep(100);
				}//try
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}//catch
			}//if
			else if(currentPath != null)//if robot has a current mapped path to go along
			{
				//get the next point along that path
				nextPoint = currentPath.get(currentPath.size()-1);
				//remove that point from the path
				currentPath.remove(currentPath.size()-1);
				if(currentPath.isEmpty())
				{
					//if the path is now empty (has no more points) then remove it
					currentPath = null;
				}//if
			}//else if
			else//finds next closest unexplored point
			{
				Point closestUnexploredPoint = getClosestUnexploredPoint(new Point(robotXCoord, robotYCoord));
				
				if(closestUnexploredPoint == null)//if all points have been explored
				{
					loop = false;
				}//if
				else
				{
					currentPath = mapRouteToPoint(robotIn, closestUnexploredPoint);
				}//else
			}//else
		}//while
	}//frontierAlgorithmLoop
	
	/**
	 * Controls a robot using My Proprietary Algorithm
	 * @param robotIn the robot to be controlled
	 */
	public void proprietaryAlgorithmLoop(Robot robotIn)
	{
		boolean loop = true;
		int robotStartXCoord = (robotIn.getCoordinates().x)/pixelsPerSquare;
		int robotStartYCoord = (robotIn.getCoordinates().y)/pixelsPerSquare;
		searchedCoordinates.put(robotStartXCoord, new ConcurrentHashMap<Integer, PointStatusEnum>());
		searchedCoordinates.get(robotStartXCoord).put(robotStartYCoord, PointStatusEnum.OPEN);
		boolean frontObstacle;
		boolean leftObstacle;
		boolean rightObstacle;
		boolean previouslySearchedFront;
		boolean previouslySearchedLeft;
		boolean previouslySearchedRight;
		ArrayList<Point> currentPath = null;
		Point nextPoint = null;
		
		while(loop)
		{
			//Search area surrounding the robot (simulated ultrasonic sensors)
			previouslySearchedFront = previouslySearchedFront(robotIn);
			previouslySearchedLeft = previouslySearchedLeft(robotIn);
			previouslySearchedRight = previouslySearchedRight(robotIn);
			frontObstacle = obstacleInFront(robotIn);
			leftObstacle = obstacleToLeft(robotIn);
			rightObstacle = obstacleToRight(robotIn);
			
			int robotXCoord = (robotIn.getCoordinates().x)/pixelsPerSquare;
			int robotYCoord = (robotIn.getCoordinates().y)/pixelsPerSquare;
			
			if(nextPoint != null)//if robot has a next point to go to
			{
				if(nextStepToPoint(robotIn, nextPoint))//move a step towards the next point
				{
					//if point is reached then remove it
					nextPoint = null;
				}//if
				
				robotIn.noOfSteps ++;
				
				this.repaint();
				
				try
				{
					Thread.sleep(100);
				}//try
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}//catch
			}//if
			else if(currentPath != null)//if robot has a current mapped path to go along
			{
				//get the next point along that path
				nextPoint = currentPath.get(currentPath.size()-1);
				//remove that point from the path
				currentPath.remove(currentPath.size()-1);
				if(currentPath.isEmpty())
				{
					//if the path is now empty (has no more points) then remove it
					currentPath = null;
				}//if
			}//else if
			else if(!leftObstacle && !previouslySearchedLeft)//if robot can turn left then it does
			{
				if(robotIn.getDirection().equals(DirectionEnum.NORTH))
				{
					nextPoint = new Point(robotXCoord-1, robotYCoord);
				}//if
				else if(robotIn.getDirection().equals(DirectionEnum.EAST))
				{
					nextPoint = new Point(robotXCoord, robotYCoord-1);
				}//else if
				else if(robotIn.getDirection().equals(DirectionEnum.SOUTH))
				{
					nextPoint = new Point(robotXCoord+1, robotYCoord);
				}//else if
				else//robot facing west
				{
					nextPoint = new Point(robotXCoord, robotYCoord+1);
				}//else
			}//if
			else if(!frontObstacle && !previouslySearchedFront)//if robot can go forwards then it does
			{
				if(robotIn.getDirection().equals(DirectionEnum.NORTH))
				{
					nextPoint = new Point(robotXCoord, robotYCoord-1);
				}//if
				else if(robotIn.getDirection().equals(DirectionEnum.EAST))
				{
					nextPoint = new Point(robotXCoord+1, robotYCoord);
				}//else if
				else if(robotIn.getDirection().equals(DirectionEnum.SOUTH))
				{
					nextPoint = new Point(robotXCoord, robotYCoord+1);
				}//else if
				else//robot facing west
				{
					nextPoint = new Point(robotXCoord-1, robotYCoord);
				}//else
			}//else if
			else if(!rightObstacle && !previouslySearchedRight)//if robot can turn right then it does
			{
				if(robotIn.getDirection().equals(DirectionEnum.NORTH))
				{
					nextPoint = new Point(robotXCoord+1, robotYCoord);
				}//if
				else if(robotIn.getDirection().equals(DirectionEnum.EAST))
				{
					nextPoint = new Point(robotXCoord, robotYCoord+1);
				}//else if
				else if(robotIn.getDirection().equals(DirectionEnum.SOUTH))
				{
					nextPoint = new Point(robotXCoord-1, robotYCoord);
				}//else if
				else//robot facing west
				{
					nextPoint = new Point(robotXCoord, robotYCoord-1);
				}//else
			}//else if
			else//finds next closest unexplored point
			{
				Point closestUnexploredPoint = getClosestUnexploredPoint(new Point(robotXCoord, robotYCoord));
				
				if(closestUnexploredPoint == null)//if all points have been explored
				{
					loop = false;
				}//if
				else
				{
					currentPath = mapRouteToPoint(robotIn, closestUnexploredPoint);
				}//else
			}//else
		}//while
	}//proprietaryAlgorithmLoop
	
	/**
	 * Checks if the point in front of the robot has been searched previously
	 * @param robotIn The robot doing the exploration
	 * @return True if the point has been searched, false otherwise
	 */
	public boolean previouslySearchedFront(Robot robotIn)
	{
		int robotXCoord = (robotIn.getCoordinates().x)/pixelsPerSquare;
		int robotYCoord = (robotIn.getCoordinates().y)/pixelsPerSquare;
		
		if(robotIn.getDirection().equals(DirectionEnum.NORTH))
		{
			if(!searchedCoordinates.get(robotXCoord).containsKey(robotYCoord-1))
			{
				//point doesn't exist yet
			}//if
			else if(searchedCoordinates.get(robotXCoord).get(robotYCoord-1) != PointStatusEnum.UNEXPLORED)//previously searched coordinate
			{
				return true;
			}//else if
		}//if
		else if(robotIn.getDirection().equals(DirectionEnum.EAST))
		{
			if(searchedCoordinates.containsKey(robotXCoord+1))//previously searched column
			{
				if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord))
				{
					//point doesn't exist yet
				}//if
				else if(searchedCoordinates.get(robotXCoord+1).get(robotYCoord) != PointStatusEnum.UNEXPLORED)//previously searched coordinate
				{
					return true;
				}//else if
			}//if
			else//column hasn't been searched
			{
				//add a column to the coordinates list
				searchedCoordinates.put(robotXCoord+1, new ConcurrentHashMap<Integer, PointStatusEnum>());
			}//else
		}//else if
		else if(robotIn.getDirection().equals(DirectionEnum.SOUTH))
		{
			if(!searchedCoordinates.get(robotXCoord).containsKey(robotYCoord+1))
			{
				//point doesn't exist yet
			}//if
			else if(searchedCoordinates.get(robotXCoord).get(robotYCoord+1) != PointStatusEnum.UNEXPLORED)//previously searched coordinate
			{
				return true;
			}//else if
		}//else if
		else//facing west
		{
			if(searchedCoordinates.containsKey(robotXCoord-1))//previously searched column
			{
				if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord))
				{
					//point doesn't exist yet
				}//if
				else if(searchedCoordinates.get(robotXCoord-1).get(robotYCoord) != PointStatusEnum.UNEXPLORED)//previously searched coordinate
				{
					return true;
				}//if
			}//if
			else//column hasn't been searched
			{
				//add a column to the coordinates list
				searchedCoordinates.put(robotXCoord-1, new ConcurrentHashMap<Integer, PointStatusEnum>());
			}//else
		}//else
		
		//coordinate hasn't been previously searched
		return false;
	}//previouslySearchedFront
	
	/**
	 * Checks if there is an obstacle present in front of the robot (simulates front ultrasonic sensor)
	 * and if there isn't an obstacle adds the points around the searched point to the list of frontier points
	 * @param robotIn The robot doing the exploration
	 * @return True if there is an obstacle, false otherwise
	 */
	public boolean obstacleInFront(Robot robotIn)
	{
		int robotXCoord = (robotIn.getCoordinates().x)/pixelsPerSquare;
		int robotYCoord = (robotIn.getCoordinates().y)/pixelsPerSquare;
		
		if(robotIn.getDirection().equals(DirectionEnum.NORTH))
		{
			if(originalCoordinates[robotXCoord][robotYCoord-1])//there is an obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord).put(robotYCoord-1, PointStatusEnum.OBSTACLE);
				return true;
			}//if
			else//there is no obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord).put(robotYCoord-1, PointStatusEnum.OPEN);
				
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord).containsKey(robotYCoord-2))//check if point above exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord).put(robotYCoord-2, PointStatusEnum.UNEXPLORED);
				}//if
				if(!searchedCoordinates.containsKey(robotXCoord-1))//check if column to the left exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord-1, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord-1))//check if point to the left exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//else if
				if(!searchedCoordinates.containsKey(robotXCoord+1))//check if column to the right exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord+1, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord-1))//check if point to the right exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//else if
			}//else
		}//if
		else if(robotIn.getDirection().equals(DirectionEnum.EAST))
		{
			if(originalCoordinates[robotXCoord+1][robotYCoord])//there is an obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord+1).put(robotYCoord, PointStatusEnum.OBSTACLE);
				return true;
			}//if
			else//there is no obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord+1).put(robotYCoord, PointStatusEnum.OPEN);

				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord-1))//check if point above exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//if
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord+1))//check if point below exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//if
				if(!searchedCoordinates.containsKey(robotXCoord+2))//check if column to the right exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord+2, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord+2).put(robotYCoord, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord+2).containsKey(robotYCoord))//check if point to the right exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+2).put(robotYCoord, PointStatusEnum.UNEXPLORED);
				}//else if
			}//else
		}//else if
		else if(robotIn.getDirection().equals(DirectionEnum.SOUTH))
		{
			if(originalCoordinates[robotXCoord][robotYCoord+1])//there is an obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord).put(robotYCoord+1, PointStatusEnum.OBSTACLE);
				return true;
			}//if
			else//there is no obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord).put(robotYCoord+1, PointStatusEnum.OPEN);
				
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord).containsKey(robotYCoord+2))//check if point below exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord).put(robotYCoord+2, PointStatusEnum.UNEXPLORED);
				}//if
				if(!searchedCoordinates.containsKey(robotXCoord-1))//check if column to the left exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord-1, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord+1))//check if point to the left exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//else if
				if(!searchedCoordinates.containsKey(robotXCoord+1))//check if column to the right exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord+1, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord+1))//check if point to the right exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//else if
			}//else
		}//else if
		else//facing west
		{
			if(originalCoordinates[robotXCoord-1][robotYCoord])//there is an obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord-1).put(robotYCoord, PointStatusEnum.OBSTACLE);
				return true;
			}//if
			else//there is no obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord-1).put(robotYCoord, PointStatusEnum.OPEN);
				
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord-1))//check if point above exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//if
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord+1))//check if point below exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//if
				if(!searchedCoordinates.containsKey(robotXCoord-2))//check if column to the left exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord-2, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord-2).put(robotYCoord, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord-2).containsKey(robotYCoord))//check if point to the left exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-2).put(robotYCoord, PointStatusEnum.UNEXPLORED);
				}//else if
			}//else
		}//else
		
		//no obstacle found
		return false;
	}//obstacleInFront
	
	/**
	 * Checks if the point to the left of the robot has been searched previously
	 * @param robotIn The robot doing the exploration
	 * @return True if the point has been searched, false otherwise
	 */
	public boolean previouslySearchedLeft(Robot robotIn)
	{
		int robotXCoord = (robotIn.getCoordinates().x)/pixelsPerSquare;
		int robotYCoord = (robotIn.getCoordinates().y)/pixelsPerSquare;
		
		if(robotIn.getDirection().equals(DirectionEnum.NORTH))
		{
			int xCoord = robotXCoord-1;
			if(searchedCoordinates.containsKey(xCoord))//previously searched column
			{
				if(!searchedCoordinates.get(xCoord).containsKey(robotYCoord))
				{
					//point doesn't exist yet
				}//if
				else if(searchedCoordinates.get(xCoord).get(robotYCoord)!= PointStatusEnum.UNEXPLORED)//previously searched coordinate
				{
					return true;
				}//else if
			}//if
			else//column hasn't been searched
			{
				//add a column to the coordinates list
				searchedCoordinates.put(xCoord, new ConcurrentHashMap<Integer, PointStatusEnum>());
			}//else
		}//if
		else if(robotIn.getDirection().equals(DirectionEnum.EAST))
		{	
			int yCoord = robotYCoord-1;
			if(!searchedCoordinates.get(robotXCoord).containsKey(yCoord))
			{
				//point doesn't exist yet
			}//if
			else if(searchedCoordinates.get(robotXCoord).get(yCoord) != PointStatusEnum.UNEXPLORED)//previously searched coordinate
			{
				return true;
			}//else if
		}//else if
		else if(robotIn.getDirection().equals(DirectionEnum.SOUTH))
		{	
			int xCoord = robotXCoord+1;
			if(searchedCoordinates.containsKey(xCoord))//previously searched column
			{
				if(!searchedCoordinates.get(xCoord).containsKey(robotYCoord))
				{
					///point doesn't exist yet
				}//if
				else if(searchedCoordinates.get(xCoord).get(robotYCoord) != PointStatusEnum.UNEXPLORED)//previously searched coordinate
				{
					return true;
				}//else if
			}//if
			else//column hasn't been searched
			{
				//add a column to the coordinates list
				searchedCoordinates.put(xCoord, new ConcurrentHashMap<Integer, PointStatusEnum>());
			}//else
		}//else if
		else//facing west
		{	
			int yCoord = robotYCoord+1;
			if(!searchedCoordinates.get(robotXCoord).containsKey(yCoord))
			{
				//point doesn't exist yet
			}//if
			else if(searchedCoordinates.get(robotXCoord).get(yCoord) != PointStatusEnum.UNEXPLORED)//previously searched coordinate
			{
				return true;
			}//else if
		}//else
		
		//coordinate hasn't bee searched previously
		return false;
	}//previouslySearchedLeft
	
	/**
	 * Checks if there is an obstacle present to the left of the robot (simulates front ultrasonic sensor)
	 * and if there isn't an obstacle adds the points around the searched point to the list of frontier points
	 * @param robotIn The robot doing the exploration
	 * @return True if there is an obstacle, false otherwise
	 */
	public boolean obstacleToLeft(Robot robotIn)
	{
		int robotXCoord = (robotIn.getCoordinates().x)/pixelsPerSquare;
		int robotYCoord = (robotIn.getCoordinates().y)/pixelsPerSquare;
		
		if(robotIn.getDirection().equals(DirectionEnum.NORTH))
		{	
			if(originalCoordinates[robotXCoord-1][robotYCoord])//there is an obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord-1).put(robotYCoord, PointStatusEnum.OBSTACLE);
				return true;
			}//if
			else//there is no obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord-1).put(robotYCoord, PointStatusEnum.OPEN);
				
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord-1))//check if point above exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//if
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord+1))//check if point below exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//if
				if(!searchedCoordinates.containsKey(robotXCoord-2))//check if column to the left exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord-2, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord-2).put(robotYCoord, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord-2).containsKey(robotYCoord))//check if point to the left exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-2).put(robotYCoord, PointStatusEnum.UNEXPLORED);
				}//else if
			}//else
		}//if
		else if(robotIn.getDirection().equals(DirectionEnum.EAST))
		{
			if(originalCoordinates[robotXCoord][robotYCoord-1])//there is an obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord).put(robotYCoord-1, PointStatusEnum.OBSTACLE);
				return true;
			}//if
			else//there is no obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord).put(robotYCoord-1, PointStatusEnum.OPEN);
				
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord).containsKey(robotYCoord-2))//check if point above exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord).put(robotYCoord-2, PointStatusEnum.UNEXPLORED);
				}//if
				if(!searchedCoordinates.containsKey(robotXCoord-1))//check if column to the left exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord-1, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord-1))//check if point to the left exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//else if
				if(!searchedCoordinates.containsKey(robotXCoord+1))//check if column to the right exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord+1, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord-1))//check if point to the right exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//else if
			}//else
		}//else if
		else if(robotIn.getDirection().equals(DirectionEnum.SOUTH))
		{
			if(originalCoordinates[robotXCoord+1][robotYCoord])//there is an obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord+1).put(robotYCoord, PointStatusEnum.OBSTACLE);
				return true;
			}//if
			else//there is no obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord+1).put(robotYCoord, PointStatusEnum.OPEN);
				
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord-1))//check if point above exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//if
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord+1))//check if point below exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//if
				if(!searchedCoordinates.containsKey(robotXCoord+2))//check if column to the right exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord+2, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord+2).put(robotYCoord, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord+2).containsKey(robotYCoord))//check if point to the right exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+2).put(robotYCoord, PointStatusEnum.UNEXPLORED);
				}//else if
			}//else
		}//else if
		else//facing west
		{
			if(originalCoordinates[robotXCoord][robotYCoord+1])//there is an obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord).put(robotYCoord+1, PointStatusEnum.OBSTACLE);
				return true;
			}//if
			else//there is no obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord).put(robotYCoord+1, PointStatusEnum.OPEN);
				
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord).containsKey(robotYCoord+2))//check if point below exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord).put(robotYCoord+2, PointStatusEnum.UNEXPLORED);
				}//if
				if(!searchedCoordinates.containsKey(robotXCoord-1))//check if column to the left exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord-1, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord+1))//check if point to the left exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//else if
				if(!searchedCoordinates.containsKey(robotXCoord+1))//check if column to the right exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord+1, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord+1))//check if point to the right exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//else if
			}//else
		}//else
		
		//no obstacle found
		return false;
	}//obstacleToLeft
	
	/**
	 * Checks if the point to the right of the robot has been searched previously
	 * @param robotIn The robot doing the exploration
	 * @return True if the point has been searched, false otherwise
	 */
	public boolean previouslySearchedRight(Robot robotIn)
	{
		int robotXCoord = (robotIn.getCoordinates().x)/pixelsPerSquare;
		int robotYCoord = (robotIn.getCoordinates().y)/pixelsPerSquare;
		
		if(robotIn.getDirection().equals(DirectionEnum.NORTH))
		{
			if(searchedCoordinates.containsKey(robotXCoord+1))//previously searched column
			{
				if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord))
				{
					//point doesn't exist yet
				}//if
				else if(searchedCoordinates.get(robotXCoord+1).get(robotYCoord) != PointStatusEnum.UNEXPLORED)//previously searched coordinate
				{
					return true;
				}//else if
			}//if
			else//column hasn't been searched
			{
				//add a column to the coordinates list
				searchedCoordinates.put(robotXCoord+1, new ConcurrentHashMap<Integer, PointStatusEnum>());
			}//else
		}//if
		else if(robotIn.getDirection().equals(DirectionEnum.EAST))
		{	
			if(!searchedCoordinates.get(robotXCoord).containsKey(robotYCoord+1))
			{
				//point doesn't exist yet
			}//if
			else if(searchedCoordinates.get(robotXCoord).get(robotYCoord+1) != PointStatusEnum.UNEXPLORED)//previously searched coordinate
			{
				return true;
			}//else if
		}//else if
		else if(robotIn.getDirection().equals(DirectionEnum.SOUTH))
		{	
			if(searchedCoordinates.containsKey(robotXCoord-1))//previously searched column
			{
				if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord))
				{
					//point doesn't exist yet
				}//if
				else if(searchedCoordinates.get(robotXCoord-1).get(robotYCoord) != PointStatusEnum.UNEXPLORED)//previously searched coordinate
				{
					return true;
				}//else if
			}//if
			else//column hasn't been searched
			{
				//add a column to the coordinates list
				searchedCoordinates.put(robotXCoord-1, new ConcurrentHashMap<Integer, PointStatusEnum>());
			}//else
		}//else if
		else//facing west
		{	
			if(!searchedCoordinates.get(robotXCoord).containsKey(robotYCoord-1))
			{
				//point doesn't exist yet
			}//if
			else if(searchedCoordinates.get(robotXCoord).get(robotYCoord-1) != PointStatusEnum.UNEXPLORED)//previously searched coordinate
			{
				return true;
			}//else if
		}//else
		
		//coordinate not previously searched
		return false;
	}//previouslySearchedRight

	/**
	 * Checks if there is an obstacle present to the right of the robot (simulates front ultrasonic sensor)
	 * and if there isn't an obstacle adds the points around the searched point to the list of frontier points
	 * @param robotIn The robot doing the exploration
	 * @return True if there is an obstacle, false otherwise
	 */
	public boolean obstacleToRight(Robot robotIn)
	{
		int robotXCoord = (robotIn.getCoordinates().x)/pixelsPerSquare;
		int robotYCoord = (robotIn.getCoordinates().y)/pixelsPerSquare;
		
		if(robotIn.getDirection().equals(DirectionEnum.NORTH))
		{
			if(originalCoordinates[robotXCoord+1][robotYCoord])//there is an obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord+1).put(robotYCoord, PointStatusEnum.OBSTACLE);
				return true;
			}//if
			else//there is no obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord+1).put(robotYCoord, PointStatusEnum.OPEN);
				
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord-1))//check if point above exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//if
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord+1))//check if point below exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//if
				if(!searchedCoordinates.containsKey(robotXCoord+2))//check if column to the right exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord+2, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord+2).put(robotYCoord, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord+2).containsKey(robotYCoord))//check if point to the right exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+2).put(robotYCoord, PointStatusEnum.UNEXPLORED);
				}//else if
			}//else
		}//if
		else if(robotIn.getDirection().equals(DirectionEnum.EAST))
		{	
			if(originalCoordinates[robotXCoord][robotYCoord+1])//there is an obstacle
			{
				//add to searched coordinates list;
				searchedCoordinates.get(robotXCoord).put(robotYCoord+1, PointStatusEnum.OBSTACLE);
				return true;
			}//if
			else//there is no obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord).put(robotYCoord+1, PointStatusEnum.OPEN);
				
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord).containsKey(robotYCoord+2))//check if point below exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord).put(robotYCoord+2, PointStatusEnum.UNEXPLORED);
				}//if
				if(!searchedCoordinates.containsKey(robotXCoord-1))//check if column to the left exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord-1, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord+1))//check if point to the left exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//else if
				if(!searchedCoordinates.containsKey(robotXCoord+1))//check if column to the right exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord+1, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord+1))//check if point to the right exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//else if
			}//else
		}//else if
		else if(robotIn.getDirection().equals(DirectionEnum.SOUTH))
		{	
			
			if(originalCoordinates[robotXCoord-1][robotYCoord])//there is an obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord-1).put(robotYCoord, PointStatusEnum.OBSTACLE);
				return true;
			}//if
			else//there is no obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord-1).put(robotYCoord, PointStatusEnum.OPEN);
				
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord-1))//check if point above exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//if
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord+1))//check if point below exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord+1, PointStatusEnum.UNEXPLORED);
				}//if
				if(!searchedCoordinates.containsKey(robotXCoord-2))//check if column to the left exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord-2, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord-2).put(robotYCoord, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord-2).containsKey(robotYCoord))//check if point to the left exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-2).put(robotYCoord, PointStatusEnum.UNEXPLORED);
				}//else if
			}//else
		}//else if
		else//facing west
		{	
			if(originalCoordinates[robotXCoord][robotYCoord-1])//there is an obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord).put(robotYCoord-1, PointStatusEnum.OBSTACLE);
				return true;
			}//if
			else//there is no obstacle
			{
				//add to searched coordinates list
				searchedCoordinates.get(robotXCoord).put(robotYCoord-1, PointStatusEnum.OPEN);
				
				//add next unknown points if the don't already exist
				if(!searchedCoordinates.get(robotXCoord).containsKey(robotYCoord-2))//check if point above exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord).put(robotYCoord-2, PointStatusEnum.UNEXPLORED);
				}//if
				if(!searchedCoordinates.containsKey(robotXCoord-1))//check if column to the left exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord-1, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord-1).containsKey(robotYCoord-1))//check if point to the left exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord-1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//else if
				if(!searchedCoordinates.containsKey(robotXCoord+1))//check if column to the right exists
				{
					//add new column
					searchedCoordinates.put(robotXCoord+1, new ConcurrentHashMap<Integer, PointStatusEnum>());
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//if
				else if(!searchedCoordinates.get(robotXCoord+1).containsKey(robotYCoord-1))//check if point to the right exists
				{
					//add new point
					searchedCoordinates.get(robotXCoord+1).put(robotYCoord-1, PointStatusEnum.UNEXPLORED);
				}//else if
			}//else
		}//else
		
		//no obstacle found
		return false;
	}//obstacleToRight
	
	/**
	 * Makes the robot take the next step towards a point (only used when the point is one square away 
	 * from the robots current position). A step can be a move forwards, or a turn to the left/right of 90 degrees
	 * @param robotIn The robot doing the exploration
	 * @param targetPointIn The point the robot needs to get to (can only be one square away from the robots current position)
	 * @return True if the point has been reached, false otherwise
	 */
	public boolean nextStepToPoint(Robot robotIn, Point targetPointIn)
	{
		int robotXCoord = (robotIn.getCoordinates().x)/pixelsPerSquare;
		int robotYCoord = (robotIn.getCoordinates().y)/pixelsPerSquare;
		
		if(robotIn.getDirection().equals(DirectionEnum.NORTH))
		{
			if(targetPointIn.x > robotXCoord)//robot needs to head east
			{
				robotIn.turnRight();
				return false;
			}//if
			else if(targetPointIn.x < robotXCoord)//robot needs to head west
			{
				robotIn.turnLeft();
				return false;
			}//else if
			else if(targetPointIn.y > robotYCoord)//robot needs to head south
			{
				robotIn.turnLeft();
				return false;
			}//else if
			else if(targetPointIn.y < robotYCoord)//robot needs to head north
			{
				robotIn.moveForwards();
				//robot has reached the point
				return true;
			}//else if
			else//robot is already at the point
			{
				return true;
			}//else
		}//if
		else if(robotIn.getDirection().equals(DirectionEnum.EAST))
		{
			if(targetPointIn.x > robotXCoord)//robot needs to head east
			{
				robotIn.moveForwards();
				//robot has reached the point
				return true;
			}//if
			else if(targetPointIn.x < robotXCoord)//robot needs to head west
			{
				robotIn.turnLeft();
				return false;
			}//else if
			else if(targetPointIn.y > robotYCoord)//robot needs to head south
			{
				robotIn.turnRight();
				return false;
			}//else if
			else if(targetPointIn.y < robotYCoord)//robot needs to head north
			{
				robotIn.turnLeft();
				return false;
			}//else if
			else//robot is already at the point
			{
				return true;
			}//else
		}//else if
		else if(robotIn.getDirection().equals(DirectionEnum.SOUTH))
		{
			if(targetPointIn.x > robotXCoord)//robot needs to head east
			{
				robotIn.turnLeft();
				return false;
			}//if
			else if(targetPointIn.x < robotXCoord)//robot needs to head west
			{
				robotIn.turnRight();
				return false;
			}//else if
			else if(targetPointIn.y > robotYCoord)//robot needs to head south
			{
				robotIn.moveForwards();
				//robot has reached the point
				return true;
			}//else if
			else if(targetPointIn.y < robotYCoord)//robot needs to head north
			{
				robotIn.turnLeft();
				return false;
			}//else if
			else//robot is already at the point
			{
				return true;
			}//else
		}//else if
		else//robot facing west
		{
			if(targetPointIn.x > robotXCoord)//robot needs to head east
			{
				robotIn.turnLeft();
				return false;
			}//if
			else if(targetPointIn.x < robotXCoord)//robot needs to head west
			{
				robotIn.moveForwards();
				//robot has reached the point
				return true;
			}//else if
			else if(targetPointIn.y > robotYCoord)//robot needs to head south
			{
				robotIn.turnLeft();
				return false;
			}//else if
			else if(targetPointIn.y < robotYCoord)//robot needs to head north
			{
				robotIn.turnRight();
				return false;
			}//else if
			else//robot is already at the point
			{
				return true;
			}//else
		}//else
	}//nextStepToPoint
	
	/**
	 * Finds the closest unexplored (frontier) point to the robot
	 * @param currentPositionIn The robots current position
	 * @return The coordinates of the closest unexplored point
	 */
	public Point getClosestUnexploredPoint(Point currentPositionIn)
	{
		Point closestUnexploredPoint = null;
		int distance = Integer.MAX_VALUE;
		int xCoordKey = -1;
		int yCoordKey = -1;
		
		for (Map.Entry<Integer, ConcurrentHashMap<Integer, PointStatusEnum>> xCoordEntry : searchedCoordinates.entrySet())
		{
			for (Map.Entry<Integer, PointStatusEnum> yCoordEntry : xCoordEntry.getValue().entrySet())
			{
				if(yCoordEntry.getValue() == PointStatusEnum.UNEXPLORED)//if point hasn't been explored
				{
					int currentDistance = Math.abs(currentPositionIn.x - xCoordEntry.getKey()) + 
										Math.abs(currentPositionIn.y - yCoordEntry.getKey());
					//Test if point is closer than current closest point
					if(distance > currentDistance)
					{
						//make point the current closest
						distance = currentDistance;
						closestUnexploredPoint = new Point(xCoordEntry.getKey(), yCoordEntry.getKey());
						xCoordKey = xCoordEntry.getKey();
						yCoordKey = yCoordEntry.getKey();
					}//if
				}//if
			}//for
		}//for
		
		//Allows the robots to tell each other which square they are going to next
		//by setting it as an obstacle it makes sure the other robot won't try and search it as well
		if(xCoordKey != -1 && yCoordKey != -1 && coordinated)
		{
			searchedCoordinates.get(xCoordKey).put(yCoordKey, PointStatusEnum.OBSTACLE);
		}//if
		
		return closestUnexploredPoint;
	}//getClosestUnexploredPoint
	
	/**
	 * Estimates the distance between points using the Manhattan Block Heuristic
	 * @param currentPointIn The starting point
	 * @param targetPointIn The target point
	 * @return The distance between the points
	 */
	public int getDistanceToPoint(Point currentPointIn, Point targetPointIn)
	{
		return Math.abs(currentPointIn.x - targetPointIn.x) + 
				Math.abs(currentPointIn.y - targetPointIn.y);
	}//getDIstanceToPoint
	
	/**
	 * Maps a route from the robots current position to the target point (using A* path finding algorithm)
	 * @param robotIn The robot doing the exploration
	 * @param targetPointIn The target point
	 * @return An array of points which map the route to the target point (the route starts from the end of the array)
	 */
	public ArrayList<Point> mapRouteToPoint(Robot robotIn, Point targetPointIn)
	{
		//A* pathfinding algorithm
		ArrayList<Coordinate> openCoordinates = new ArrayList<Coordinate>();
		ArrayList<Coordinate> closedCoordinates = new ArrayList<Coordinate>();
		Coordinate currentPoint = null;
		//robot starting point
		Point startCoord = new Point((robotIn.getCoordinates().x)/pixelsPerSquare, (robotIn.getCoordinates().y)/pixelsPerSquare);
		//add start point to open list
		openCoordinates.add(new Coordinate(startCoord, null, robotIn.getDirection(), 0, 0));
		
		while(true)
		{
			//no path found to target point
			if(openCoordinates.isEmpty())
			{
				//set target point as an obstacle (so it won't be searched for again)
				searchedCoordinates.get(targetPointIn.x).put(targetPointIn.y, PointStatusEnum.OBSTACLE);
				return null;
			}//if
			
			//gets open coordinate with the lowest f score (list is ordered by a coordinates f score, lowest first)
			currentPoint = openCoordinates.get(0);
			if(currentPoint.getPoint().equals(targetPointIn))
			{
				//finished
				break;
			}//if
			//removes it from the open list
			openCoordinates.remove(0);
			//puts it into the closed list
			closedCoordinates.add(currentPoint);
			
			if(currentPoint.getFacingDirection().equals(DirectionEnum.NORTH))
			{
				//check point to the north
				checkPoint(currentPoint, new Point(currentPoint.getX(), currentPoint.getY()-1), targetPointIn, 1, 
						DirectionEnum.NORTH, openCoordinates, closedCoordinates);
				//check point to the east
				checkPoint(currentPoint, new Point(currentPoint.getX()+1, currentPoint.getY()), targetPointIn, 2, 
						DirectionEnum.EAST, openCoordinates, closedCoordinates);
				//check point to the south
				checkPoint(currentPoint, new Point(currentPoint.getX(), currentPoint.getY()+1), targetPointIn, 3, 
						DirectionEnum.SOUTH, openCoordinates, closedCoordinates);
				//check point to the west
				checkPoint(currentPoint, new Point(currentPoint.getX()-1, currentPoint.getY()), targetPointIn, 2, 
						DirectionEnum.WEST, openCoordinates, closedCoordinates);
			}//if
			else if(currentPoint.getFacingDirection().equals(DirectionEnum.EAST))
			{
				//check point to the north
				checkPoint(currentPoint, new Point(currentPoint.getX(), currentPoint.getY()-1), targetPointIn, 2, 
						DirectionEnum.NORTH, openCoordinates, closedCoordinates);
				//check point to the east
				checkPoint(currentPoint, new Point(currentPoint.getX()+1, currentPoint.getY()), targetPointIn, 1, 
						DirectionEnum.EAST, openCoordinates, closedCoordinates);
				//check point to the south
				checkPoint(currentPoint, new Point(currentPoint.getX(), currentPoint.getY()+1), targetPointIn, 2, 
						DirectionEnum.SOUTH, openCoordinates, closedCoordinates);
				//check point to the west
				checkPoint(currentPoint, new Point(currentPoint.getX()-1, currentPoint.getY()), targetPointIn, 3, 
						DirectionEnum.WEST, openCoordinates, closedCoordinates);
			}//else if
			else if(currentPoint.getFacingDirection().equals(DirectionEnum.SOUTH))
			{
				//check point to the north
				checkPoint(currentPoint, new Point(currentPoint.getX(), currentPoint.getY()-1), targetPointIn, 3, 
						DirectionEnum.NORTH, openCoordinates, closedCoordinates);
				//check point to the east
				checkPoint(currentPoint, new Point(currentPoint.getX()+1, currentPoint.getY()), targetPointIn, 2, 
						DirectionEnum.EAST, openCoordinates, closedCoordinates);
				//check point to the south
				checkPoint(currentPoint, new Point(currentPoint.getX(), currentPoint.getY()+1), targetPointIn, 1, 
						DirectionEnum.SOUTH, openCoordinates, closedCoordinates);
				//check point to the west
				checkPoint(currentPoint, new Point(currentPoint.getX()-1, currentPoint.getY()), targetPointIn, 2, 
						DirectionEnum.WEST, openCoordinates, closedCoordinates);
			}//else if
			else//facing west
			{
				//check point to the north
				checkPoint(currentPoint, new Point(currentPoint.getX(), currentPoint.getY()-1), targetPointIn, 2, 
						DirectionEnum.NORTH, openCoordinates, closedCoordinates);
				//check point to the east
				checkPoint(currentPoint, new Point(currentPoint.getX()+1, currentPoint.getY()), targetPointIn, 3, 
						DirectionEnum.EAST, openCoordinates, closedCoordinates);
				//check point to the south
				checkPoint(currentPoint, new Point(currentPoint.getX(), currentPoint.getY()+1), targetPointIn, 2, 
						DirectionEnum.SOUTH, openCoordinates, closedCoordinates);
				//check point to the west
				checkPoint(currentPoint, new Point(currentPoint.getX()-1, currentPoint.getY()), targetPointIn,1, 
						DirectionEnum.WEST, openCoordinates, closedCoordinates);
			}//else
		}//while
		
		//Map out shortest path found
		ArrayList<Point> path = new ArrayList<Point>();
		while(true)
		{
			if(currentPoint.getParentPoint() == null)//if no more parent points
			{
				//path is complete
				break;
			}//if
			else
			{
				//add next step to path
				Point parentPoint = currentPoint.getParentPoint();
				path.add(parentPoint);
				for(int a=0; a<closedCoordinates.size(); a++)
				{
					if(closedCoordinates.get(a).getPoint().equals(parentPoint))
					{
						currentPoint = closedCoordinates.get(a);
					}//if
				}//for
			}//else
		}//while
		
		//return the path
		return path;
	}//mapRouteToPoint
	
	/**
	 * Used by the A* path finding algorithm. This method checks a point to measure its heuristic scores,
	 * to decide if it is along the optimal path to the target point
	 * @param currentPointIn The parent point of the point to be searched
	 * @param pointToCheckIn The point to be searched
	 * @param targetPointIn The target point that the robot is heading to
	 * @param gScoreIn The penalty score of travelling to this point (1 = a move forwards, 2 = a 90 degree turn and a move forward, 3 = two 90 degree turns and a move forwards)
	 * @param travelingDirectionIn The direction the robot would be facing before travelling to this point
	 * @param openCoordinatesIn The open coordinates list (from A* algorithm)
	 * @param closedCoordinatesIn The closed coordinates list (from A* algorithm)
	 */
	public void checkPoint(Coordinate currentPointIn, Point pointToCheckIn, Point targetPointIn, int gScoreIn, 
			DirectionEnum travelingDirectionIn, ArrayList<Coordinate> openCoordinatesIn, ArrayList<Coordinate> closedCoordinatesIn)
	{	
		boolean closedPoint = false;
		int openCoordIndex = -1;
		
		if(targetPointIn.equals(pointToCheckIn))//target point found
		{
			//add target point to first position in open list, so algorithm will end as soon as it picks up the target point
			Coordinate coord = new Coordinate(pointToCheckIn, currentPointIn.getPoint(), travelingDirectionIn,
					currentPointIn.getGScore()+gScoreIn, 0);
			openCoordinatesIn.add(0, coord);
		}//if
		else if(!searchedCoordinates.containsKey(pointToCheckIn.x))
		{
			//column hasn't been searched so point doesn't exist yet, ignore it
		}//if
		else if(!searchedCoordinates.get(pointToCheckIn.x).containsKey(pointToCheckIn.y))
		{
			//no point there so ignore it
		}//if
		else if(searchedCoordinates.get(pointToCheckIn.x).get(pointToCheckIn.y) == PointStatusEnum.UNEXPLORED)
		{
			//unexplored point so ignore it
		}//if
		else if(searchedCoordinates.get(pointToCheckIn.x).get(pointToCheckIn.y) == PointStatusEnum.OPEN)//no obstacle at that point
		{
			//check if point is in the closed list
			for(int a=0; a<closedCoordinatesIn.size(); a++)
			{
				if(closedCoordinatesIn.get(a).getPoint().equals(pointToCheckIn))
				{
					closedPoint = true;
				}//if
			}//for
			
			if(!closedPoint)//if point isn't in the closed list
			{
				//check if point is in the open list
				for(int a=0; a<openCoordinatesIn.size(); a++)
				{
					if(openCoordinatesIn.get(a).getPoint().equals(pointToCheckIn))
					{
						openCoordIndex = a;
					}//if
				}//for
				
				if(openCoordIndex != -1)//if point is in the open list
				{
					Coordinate openCoord = openCoordinatesIn.get(openCoordIndex);

					//if a better path to the existing open point has been found
					if(openCoord.getGScore() >= currentPointIn.getGScore()+1)
					{
						//replace that coordinate with new coordinate info.
						openCoordinatesIn.remove(openCoordIndex);
								
						Coordinate coord = new Coordinate(pointToCheckIn, currentPointIn.getPoint(), travelingDirectionIn,
								currentPointIn.getGScore()+gScoreIn, getDistanceToPoint(pointToCheckIn, targetPointIn));
						//add new point to open list (which is ordered by a coordinates f score, lowest first)
						for(int a=0; a<openCoordinatesIn.size(); a++)
						{
							//gives priority to the more recently searched path
							if(openCoordinatesIn.get(a).getFScore() >= coord.getFScore())
							{
								openCoordinatesIn.add(a, coord);
								break;
							}//if
							else if(a+1 == openCoordinatesIn.size())
							{
								openCoordinatesIn.add(coord);
							}//else if
						}//for
					}//if
				}//if
				else//point isn't in the open list
				{
					Coordinate coord = new Coordinate(pointToCheckIn, currentPointIn.getPoint(), travelingDirectionIn,
							currentPointIn.getGScore()+gScoreIn, getDistanceToPoint(pointToCheckIn, targetPointIn));
					boolean coordinateAdded = false;
					//add point to open list (which is ordered by a coordinates f score, lowest first)
					for(int a=0; a<openCoordinatesIn.size(); a++)
					{
						//gives priority to the more recently searched point
						if(openCoordinatesIn.get(a).getFScore() >= coord.getFScore())
						{
							openCoordinatesIn.add(a, coord);
							coordinateAdded = true;
							break;
						}//if
					}//for
					if(!coordinateAdded)
					{
						openCoordinatesIn.add(coord);
					}//if
				}//else
			}//if
		}//else if
	}//checkPoint
	
	/**
	 * A thread that loops through the simulation algorithm, 1 instance of this thread is required per robot
	 * @author Paul Monk
	 * @version 12/03/2014
	 */
	public class SimulationLoopThread implements Runnable
	{
		private Robot robot;
		boolean proprietaryAlgorithm;
		public int noOfSteps;
		
		/**
		 * Sets up the thread
		 * @param robotIn The robot this thread will control
		 * @param proprietaryAlgorithmIn which algorithm the thread will run (true for proprietary, false for frontier)
		 */
		public SimulationLoopThread(Robot robotIn, boolean proprietaryAlgorithmIn)
		{
			robot = robotIn;
			proprietaryAlgorithm = proprietaryAlgorithmIn;
		}//simulationLoopThread
		
		/**
		 * Runs the thread
		 */
		@Override
		public void run()
		{
			if(proprietaryAlgorithm)
			{
				proprietaryAlgorithmLoop(robot);
			}//if
			else
			{
				frontierAlgorithmLoop(robot);
			}//else
			
			noOfSteps = robot.noOfSteps;
			endSimulation(robot, noOfSteps);
		}//run
	}//simulationLoopThread
}//end
