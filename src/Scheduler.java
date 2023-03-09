import java.util.ArrayList;

/**
 * Manages and distributes commands between Floors and Elevators
 * @author user Ashwin Stoparczyk
 *
 */
public class Scheduler extends Thread {
	private ElevatorCommands commands; //Shared command list
	private CommandData currentCommand; //Currently-managed command
	private ArrayList<Elevator> elevatorList;

	/**
	 * Constructor
	 * @param commands List of elevator commands that the Scheduler will manage
	 */
	public Scheduler(ElevatorCommands commands) {
		this.commands = commands;
		this.elevatorList = new ArrayList<Elevator>;
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

			System.out.println("Server received command and sorting!");
			currentCommand = commands.getCommand(0); //Selects next command to be moved

			//Decide if command is valid needs to be refined
			if (currentCommand.getDir() != "up" || currentCommand.getDir() != "down" || currentCommand.getDest() != "floor" || currentCommand.getDest() != "server" || currentCommand.getDest() != "elevator" ||
					currentCommand.getSource() != "floor" || currentCommand.getSource() != "server" || currentCommand.getSource() != "elevator") {
				System.out.println("Command invalid. Removing");
				currentCommand = null;
			}

			//Sort list here

			Elevator closestElevator = determineClosestElevator();

			if (currentCommand.getDest().equals("elevator") ){
				sendCommandElevator(closestElevator);
			} else {sendCommandFloor()};

			commands.notifyAll();
		}
	}

	/**
	 * Scheduler sends a command to either an Elevator
	 * Shell, needs to be updated with UDP
	 */
	private void sendCommandElevator(Elevator elevator) {
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

	/**
	 * Scheduler sends a command to a Floor
	 * Shell, needs to be updated with UDP
	 */
	private void sendCommandFloor() {
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

	/**
	 * Determines the Elevator closest to the current command's destination
	 * Algorithm:
	 * Scheduler first searches for any elevators that are moving towards the destination floor. If at least one exists, only they are considered. Otherwise, all are considered
	 * Scheduler then determines the elevator that will take the shortest amount of time to reach the destination floor i.e smallest gap in floor difference
	 */
	private Elevator determineClosestElevator(){
		ArrayList<Elevator> consideredElevators;
		for (Elevator el : elevatorList){
			CommandData compCommand = el.getCurrentCommand(); //If empty ignore tba later once elevators implementation is finalized
			if ((compCommand.getDestFloor() < currentCommand.getDestFloor() && compCommand.getDir().equals("down")) ||
					(compCommand.getDestFloor() > currentCommand.getDestFloor() && compCommand.getDir().equals("up"))){
				consideredElevators.add(el);
			}
		}

		//If no elevators currently moving towards destination floor, consider all elevators
		if (consideredElevators.isEmpty()) consideredElevators = elevatorList;
		Elevator closestElevator = elevatorList.get(0);
		int closest = Math.abs(el.getDestFloor() - closestElevator.getDestFloor());

		//Iterate through considered elevators and choose the one closest to the next floor
		for (Elevator el : consideredElevators){
			if (Math.abs(el.getDestFloor() - closestElevator.getDestFloor()) < closest) closestElevator = el;
		}

		return closestElevator;
	}
	}