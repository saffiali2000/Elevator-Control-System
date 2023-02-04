import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
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
	 * @Constructor
	 * Unused default
	 */
	public ElevatorSystem() {}
	
	public synchronized void addNewCommand(CommandData command) {
		
		
	}
	
	public synchronized CommandData getCommand() {
		
		while()
		
		
		return null;
		
	}
	
	public static void main(String[] args) {
		
		//List of all pending commands
		//List<CommandData> commands = Collections.synchronizedList(new ArrayList<CommandData>());
		ElevatorCommands commands = new ElevatorCommands();
		
		//Create Floor, Elevator, and Scheduler threads
		Thread floor1 = new Floor(commands);
		Thread floor2 = new Floor(commands);
		Thread floor3 = new Floor(commands);
		Scheduler scheduler = new Scheduler(commands);
		Thread elevator1 = new Elevator(commands, scheduler);
		
		//Start threads
		floor1.start();
		floor2.start();
		floor3.start();
		elevator1.start();
		scheduler.start();
	}
}
