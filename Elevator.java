import java.util.*;

/**
 * 
 * Elevator that will consumer information from the command list to eventually move 
 * @author Walid Baitul Islam
 *
 */


public class Elevator extends Thread{
	
	
	/**
	 * Constructor
	 * 
	 * @param commands ArrayList<CommandData> list of information that will be consumed
	 */
	private ElevatorCommands commands;
	private CommandData currentCommand;
	private Scheduler scheduler;
	
	 
	
	public Elevator(ElevatorCommands commands, Scheduler scheduler  ) {
		this.commands = commands;
		currentCommand = null;
		this.scheduler = scheduler;
	}
	
	/**
	 * @Override default run method
	 */
	@Override
	public void run() {
		
		while(true) {
			waitForCommand();
			
		
		}
	}
	
	
	private void waitForCommand() {
		synchronized(this) {
			while (commands.getSize() <= 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				//Sort through commands and give them out to different elevators; not in this implementation
				scheduler.sendCommand();
				//Let elevators know a command has arrived
				notifyAll();
			}
		}
	}
	
	
}

//Elevator asks the schedular if theres a command ready
//if yes
//takes from the list 
//schedular gives index to elevator 
