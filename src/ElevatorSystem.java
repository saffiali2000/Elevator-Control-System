import java.util.ArrayList;

/**
 * Creates Floor, Elevator, and Scheduler threads and starts them
 * @author Ashwin Stoparczyk
 *
 */
public class ElevatorSystem {
	
	public ArrayList<Floor> floors; //List of floors in the system
	public ArrayList<Elevator> elevators; //List of elevators in the system
	public ElevatorCommands commands; //Shared list of commands
	
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
		Thread floor1 = new Floor(commands,23);
		Scheduler scheduler = new Scheduler(commands,23);
		Thread elevator1 = new Elevator(commands,69);
		Thread elevator2 = new Elevator(commands,70);
		Thread elevator3 = new Elevator(commands,71);
		Thread elevator4 = new Elevator(commands,72);

		
		//Start threads
		floor1.start();
		elevator1.start();
		elevator2.start();
		elevator3.start();
		elevator4.start();
		scheduler.start();
	}
}
