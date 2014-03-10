package robotExplorartion;

/**
 * Runs the robot exploration simulations
 * @author Paul Monk
 * @version 20/01/2014
 */
public class Run
{
	public static void main(String [] args)
	{
		//number of threads of the simulations to start
		int noOfThreads = 1;
		
		for(int a=0; a<noOfThreads; a++)
		{
			//starts the simulations
			new Thread(new SimulationThread(a+1)).run();
		}//for
	}//main
}//end
