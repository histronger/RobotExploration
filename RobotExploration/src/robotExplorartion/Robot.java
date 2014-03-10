package robotExplorartion;

import java.awt.*;

/**
 * A simulated robot which takes part in an exploration simulation.
 * The robot is simulated to have 3 ultrasonic sensors (front, left and right hand sides) and
 * a wireless transceiver.
 * @author Paul Monk
 * @version 20/01/2014
 */
public class Robot
{
	private String name;
	private Point coordinates;
	private int size;
	private DirectionEnum direction;
	
	/**
	 * Constructor, sets up a robot object using the information provided
	 * @param nameIn The name of the robot
	 * @param coordinatesIn The coordinates the robot will start at (in pixels)
	 * @param sizeIn The size of the robot (in pixels)
	 * @param directionIn The direction the robot faces when it starts
	 */
	public Robot(String nameIn, Point coordinatesIn, int sizeIn, DirectionEnum directionIn)
	{
		name = nameIn;
		coordinates = coordinatesIn;
		size = sizeIn;
		direction = directionIn;
	}//constructor
	
	/**
	 * Gets the name of the robot
	 * @return The robot's name
	 */
	public String getName()
	{
		return name;
	}//getName
	
	/**
	 * Gets the current coordinates of the robot (in pixels)
	 * @return The current coordinates of the robot
	 */
	public Point getCoordinates()
	{
		return coordinates;
	}//getXCoord
	
	/**
	 * Sets the current coordinates of the robot (in pixels)
	 * @param coordinatesIn The coordinates of the robot
	 */
	public void setCoordinates(Point coordinatesIn)
	{
		coordinates = coordinatesIn;
	}//setCoordinates
	
	/**
	 * Gets the size of the robot (in pixels)
	 * @return The size of the robot
	 */
	public int getSize()
	{
		return size;
	}//getSize
	
	/**
	 * Sets the size of the robot (in pixels)
	 * @param sizeIn The size of the robot
	 */
	public void setSize(int sizeIn)
	{
		size = sizeIn;
	}//setSize
	
	/**
	 * Gets the direction the robot is facing
	 * @return The direction of the robot
	 */
	public DirectionEnum getDirection()
	{
		return direction;
	}//getDirection
	
	/**
	 * Sets the direction the robot is facing
	 * @param directionIn The direction of the robot
	 */
	public void setDirection(DirectionEnum directionIn)
	{
		direction = directionIn;
	}//setDirection
	
	/**
	 * Moves the robot forward 1 square in the direction it is currently facing (1 square = the size of the robot in pixels)
	 */
	public void moveForwards()
	{
		if(direction.equals(DirectionEnum.NORTH))
		{
			coordinates.y = coordinates.y - size;
		}//if
		else if(direction.equals(DirectionEnum.EAST))
		{
			coordinates.x = coordinates.x + size;
		}//else if
		else if(direction.equals(DirectionEnum.SOUTH))
		{
			coordinates.y = coordinates.y + size;
		}//else if
		else//facing west
		{
			coordinates.x = coordinates.x - size;
		}//else
	}//moveForwards
	
	/**
	 * Turns the robot right 90 degrees
	 */
	public void turnRight()
	{
		if(direction.equals(DirectionEnum.NORTH))
		{
			direction = DirectionEnum.EAST;
		}//if
		else if(direction.equals(DirectionEnum.EAST))
		{
			direction = DirectionEnum.SOUTH;
		}//else if
		else if(direction.equals(DirectionEnum.SOUTH))
		{
			direction = DirectionEnum.WEST;
		}//else if
		else//facing west
		{
			direction = DirectionEnum.NORTH;
		}//else
	}//turnRight
	
	/**
	 * Turns the robot left 90 degrees
	 */
	public void turnLeft()
	{
		if(direction.equals(DirectionEnum.NORTH))
		{
			direction = DirectionEnum.WEST;
		}//if
		else if(direction.equals(DirectionEnum.EAST))
		{
			direction = DirectionEnum.NORTH;
		}//else if
		else if(direction.equals(DirectionEnum.SOUTH))
		{
			direction = DirectionEnum.EAST;
		}//else if
		else//facing west
		{
			direction = DirectionEnum.SOUTH;
		}//else
	}//turnLeft
	
	/**
	 * Paints the robot as a triangle on the screen
	 * @param g The graphics object the robot will be painted on
	 */
	public void paint(Graphics g)
	{
		Graphics2D g2D = (Graphics2D) g;
		Point front = null;
		Point backRight = null;
		Point backLeft = null;
		
		if(direction.equals(DirectionEnum.NORTH))
		{
			front = new Point(coordinates.x+(size/2), coordinates.y);
			backRight = new Point(coordinates.x+size, coordinates.y+size);
			backLeft = new Point(coordinates.x, coordinates.y+size);
		}//if
		else if(direction.equals(DirectionEnum.EAST))
		{
			front = new Point(coordinates.x+size, coordinates.y+(size/2));
			backRight = new Point(coordinates.x, coordinates.y+size);
			backLeft = new Point(coordinates.x, coordinates.y);
		}//else if
		else if(direction.equals(DirectionEnum.SOUTH))
		{
			front = new Point(coordinates.x+(size/2), coordinates.y+size);
			backRight = new Point(coordinates.x, coordinates.y);
			backLeft = new Point(coordinates.x+size, coordinates.y);
		}//else if
		else//facing west
		{
			front = new Point(coordinates.x, coordinates.y+(size/2));
			backRight = new Point(coordinates.x+size, coordinates.y);
			backLeft = new Point(coordinates.x+size, coordinates.y+size);
		}//else
		
		int[] xPoints = {front.x, backRight.x, backLeft.x};
		int[] yPoints = {front.y, backRight.y, backLeft.y};
		
		g2D.setColor(Color.cyan);
		g2D.fillPolygon(xPoints, yPoints, 3);
	}//paint
}//end
