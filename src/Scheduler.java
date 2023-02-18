import java.util.ArrayList;

/**
 * Manages and distributes commands between Floors and Elevators
 * @author user Ashwin Stoparczyk
 *
 */
public class Scheduler extends Thread {
	private ElevatorCommands commands;
	private CommandData currentCommand;

	/**
	 * Constructor
	 * @param commands List of elevator commands that the Scheduler will manage
	 */
	public Scheduler(ElevatorCommands commands) {
		this.commands = commands;
	}

	
	/**
	 * @Override default run method
	 */
	public void run() {
		sortCommands();
		sendCommand();
		sortCommands();
		sendCommand();
	}

	/**
	 * Sorts through pending commands and delegates to the proper elevator
	 * No sorting algorithm yet
	 */
	private void sortCommands() {
		synchronized (commands) {
			while (commands.getSize() == 0) { //Wait until commands list is populated
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			
			//Receive command and sort list
			//Sorting not yet implemented
			System.out.println("Server received command and sorting!");
			currentCommand = commands.getCommand(0); //Selects next command to be moved
			commands.notifyAll();
		}
	}

	/**
	 * Scheduler sends a command to either a FLoor or Elevator
	 */
	private void sendCommand() {
		synchronized (commands) {
			while (commands.getSize() > 0) { //Wait until elevator can accept new command
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			
			//Give command to Elevator
			if (currentCommand.getSource().equals("floor")){
				commands.addCommand(currentCommand.getTime(), currentCommand.getStartFloor(), currentCommand.getDestFloor(), currentCommand.getDir(), "scheduler", "elevator");
			} else if (currentCommand.getSource().equals("elevator")){
				commands.addCommand(currentCommand.getTime(), currentCommand.getStartFloor(), currentCommand.getDestFloor(), currentCommand.getDir(), "scheduler", "elevator");
			}
			System.out.println("Server sent command to elevator!");
			commands.notifyAll();
		}
	}


}