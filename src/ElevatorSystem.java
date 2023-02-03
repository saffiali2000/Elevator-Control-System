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
	/**
	 * @Constructor
	 * Unused default
	 */
	public ElevatorSystem() {}
	
	public static void main(String[] args) {
		
		//List of all pending commands
		List<CommandData> commands = Collections.synchronizedList(new ArrayList<CommandData>());
		
		//Create Floor, Elevator, and Scheduler threads
		Thread floor1 = new Floor(commands);
		Thread elevator1 = new Elevator();
		Thread scheduler = new Scheduler(commands);
		
		//Start threads
		floor1.start();
		elevator1.start();
		scheduler.start();
	}
}
