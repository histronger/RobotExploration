package robotExplorartion;

/**
 * A thread which is created to run a robot exploration simulation
 * @author Paul Monk
 * @version 20/01/2014
 */
public class SimulationThread implements Runnable
{
	int threadNumber;
	
	public SimulationThread(int threadNumberIn)
	{
		threadNumber = threadNumberIn;
	}//SimulationThread
	
	@Override
	public void run()
	{
		new Simulation(threadNumber);
	}//run
}//simulationThread
