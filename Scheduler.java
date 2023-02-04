import java.util.List;

/**
 * Manages and distributes commands between Floors and Elevators
 * @author user Ashwin Stoparczyk
 *
 */
public class Scheduler extends Thread {
	private ElevatorCommands commands;

	public Scheduler(ElevatorCommands commands) {
		this.commands = commands;
	};
	

	/**
	 * @Override default run method
	 */
	public void run() {
		while (true) sortCommands();		
	}
	
	/**
	 * Sorts through pending commands and delegates to the proper elevator
	 * No sorting algorithm yet
	 */
	private void sortCommands() {
		synchronized(this) {
			while (commands.getSize() > 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				//Sort through commands and give them out to different elevators; not in this implementation
				
				//Let elevators know a command has arrived
				notifyAll();
			}
		}
	}
	
	public CommandData sendCommand() {
		
		return commands.getCommand();
	}
	
}

//Assumption
//Schedular is modify the list by a certain algorithm




