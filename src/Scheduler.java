import java.util.ArrayList;

/**
 * Manages and distributes commands between Floors and Elevators
 * @author user Ashwin Stoparczyk
 *
 */
public class Scheduler extends Thread {
	private ElevatorCommands commands; //Shared command list
	private CommandData currentCommand; //Currently-managed command

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
		while (true) {
			sortCommands();
		}
	}

	/**
	 * Sorts through pending commands and delegates to the proper elevator
	 * No sorting algorithm yet
	 */
	private void sortCommands() {
		synchronized (commands) {
			while (commands.getSize() < 1) { //Wait until commands list is populated
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			
			//Sort list here
			System.out.println("Server received command and sorting!");
			currentCommand = commands.getCommand(0); //Selects next command to be moved

			//Decide if command is valid needs to be refined
			if (currentCommand.getDir() != "up" || currentCommand.getDir() != "down" || currentCommand.getDest() != "floor" || currentCommand.getDest() != "server" || currentCommand.getDest() != "elevator" ||
					currentCommand.getSource() != "floor" || currentCommand.getSource() != "server" || currentCommand.getSource() != "elevator") {
				System.out.println("Command invalid. Removing");
				currentCommand = null;
			} else {sendCommand();}
			commands.notifyAll();
		}
	}

	/**
	 * Scheduler sends a command to either a FLoor or Elevator
	 */
	private void sendCommand() {
		synchronized (commands) {
			while (commands.getSize() > 10) { //Wait until commands list is not overflowing (temporary)
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			//Change command source to scheduler to mark that it has been processed and add it back to commands list
			//Command is already checked for validity in sortCommands()
			commands.addCommand(currentCommand.getTime(), currentCommand.getStartFloor(), currentCommand.getDestFloor(), currentCommand.getDir(), "scheduler", currentCommand.getDest());
			System.out.println("Server sent command to elevator!");
			commands.notifyAll();
			}
		}
	}