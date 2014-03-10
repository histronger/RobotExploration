package robotExplorartion;

import java.awt.Point;

/**
 * A coordinate class, containing information on a point on a map (used by the A* path finding algorithm in the Simulation classes)
 * @author Paul Monk
 * @version 20/01/2014
 */
public class Coordinate
{
	//Coordinates of this point
	private Point point;
	//Coordinates of the point before this point
	private Point parentPoint;
	//The direction the robot is facing when point is reached
	private DirectionEnum facingDirection;
	//g score + h score
	private int fScore;
	//cost to get to this point from the start point
	private int gScore;
	//cost to get the the destination from this point
	private int hScore;

	/**
	 * The constructor, it makes a new coordinate object using the information provided
	 * @param pointIn The point the information refers to
	 * @param parentPointIn The parent point, the point the robot travels to before arriving at this point
	 * @param facingDirectionIn The direction the robot will be facing when it reached this point
	 * @param gScoreIn The cost to get to this point from the start point
	 * @param hScoreIn The cost to get the the destination point from this point
	 */
	public Coordinate(Point pointIn, Point parentPointIn, DirectionEnum facingDirectionIn, int gScoreIn, int hScoreIn)
	{
		point = pointIn;
		parentPoint = parentPointIn;
		facingDirection = facingDirectionIn;
		gScore = gScoreIn;
		hScore = hScoreIn;
		calculateFScore();
	}//Constructor
	
	/**
	 * Gets the point this coordinate refers to
	 * @return The Point
	 */
	public Point getPoint()
	{
		return point;
	}//getPoint
	
	/**
	 * Gets the X coordinate of the point this coordinate refers to
	 * @return The X Coordinate
	 */
	public int getX()
	{
		return point.x;
	}//getX
	
	/**
	 * Gets the Y coordinate of the point this coordinate refers to
	 * @return The Y Coordinate
	 */
	public int getY()
	{
		return point.y;
	}//getY
	
	/**
	 * Gets parent point of the point this coordinate refers to
	 * @return The parent point
	 */
	public Point getParentPoint()
	{
		return parentPoint;
	}//getParentPoint
	
	/**
	 * Gets the X coordinate of the parent point of the point this coordinate refers to
	 * @return The X Coordinate of the parent point
	 */
	public int getParentPointX()
	{
		return parentPoint.x;
	}//getParentPointX
	
	/**
	 * Gets the Y coordinate of the parent point of the point this coordinate refers to
	 * @return The Y Coordinate of the parent point
	 */
	public int getParentPointY()
	{
		return parentPoint.y;
	}//getParentPointY
	
	/**
	 * Returns the direction the robot will be facing when at this point
	 * @return The direction of the robot
	 */
	public DirectionEnum getFacingDirection()
	{
		return facingDirection;
	}//getFacingDirection
	
	/**
	 * Calculates the F score of this point (g score + h score)
	 */
	public void calculateFScore()
	{
		fScore = gScore + hScore;
	}//calculateFScore
	
	/**
	 * Gets the F score of this point
	 * @return The F Score
	 */
	public int getFScore()
	{
		return fScore;
	}//getFScore
	
	/**
	 * Sets the G score (the cost to get to this point from the start point)
	 * @param gScoreIn The G score of this point
	 */
	public void setGScore(int gScoreIn)
	{
		gScore = gScoreIn;
	}//setGScore
	
	/**
	 * Gets the G score (the cost to get to this point from the start point)
	 * @return The G score
	 */
	public int getGScore()
	{
		return gScore;
	}//getGScore
	
	/**
	 * Sets the H score (the cost to get the the destination from this point)
	 * @param hScoreIn The H score of this point
	 */
	public void setHScore(int hScoreIn)
	{
		hScore = hScoreIn;
	}//setHScore
	
	/**
	 * Gest the H score (the cost to get the the destination from this point)
	 * @return The H score
	 */
	public int getHScore()
	{
		return hScore;
	}//getHScore
}//end
