import java.util.ArrayList;

/**
 * Creates Floor, Elevator, and Scheduler threads and starts them
 * @author Ashwin Stoparczyk
 *
 */
public class ElevatorSystem {
	
	public ArrayList<Floor> floors;
	public ArrayList<Elevator> elevators;
	public ElevatorCommands commands;
	
	/**
	 * Constructor
	 * Unused default
	 */
	public ElevatorSystem() {}
	
	/**
	 * Main system method, run this to set up the system
	 * @param args Default
	 */
	public static void main(String[] args) {
		
		//List of all pending commands
		ElevatorCommands commands = new ElevatorCommands();
		
		//Create Floor, Elevator, and Scheduler threads
		Thread floor1 = new Floor(commands);
		Scheduler scheduler = new Scheduler(commands);
		Thread elevator1 = new Elevator(commands);
		
		//Start threads
		floor1.start();
		elevator1.start();
		scheduler.start();
	}
}
