package robotExplorartion;

/**
 * A thread which is created to run a robot exploration simulation
 * @author Paul Monk
 * @version 12/03/2014
 */
public class SimulationThread implements Runnable
{	
	private Simulation simulation;
	
	/**
	 * Sets up a thread to run a simulation
	 * @param threadNumberIn The number of this thread (to distinguish between different simulations running at the same time)
	 * @param proprietaryAlgorithmIn which exploration algorithm should be used (true for proprietary, false for frontier)
	 * @param twoRobotsIn True if 2 robots are running in the simulation, false if just 1 robot is running
	 * @param coordinatedIn True if the robots will coordinate with each other in the simulation, false otherwise
	 * @param obstacleProbabilityIn The chance of obstacles occurring (0-100%)
	 */
	public SimulationThread(int threadNumberIn, boolean proprietaryAlgorithmIn, boolean twoRobotsIn,
			boolean coordinatedIn, int obstacleProbabilityIn)
	{
		simulation = new Simulation(threadNumberIn, proprietaryAlgorithmIn, twoRobotsIn,
				coordinatedIn, obstacleProbabilityIn);
	}//SimulationThread
	
	/**
	 * Runs the thread, starting the simulation
	 */
	@Override
	public void run()
	{
		simulation.startSimulation();
	}//run
}//simulationThread
