import java.util.ArrayList;

/**
 * Manages and distributes commands between Floors and Elevators
 * @author user Ashwin Stoparczyk
 *
 */
public class Scheduler extends Thread {
	private ElevatorCommands commands;
	private CommandData currentCommand;
	private ArrayList<CommandData> floorList;
	private ArrayList<CommandData> elevatorList;

	private ArrayList<CommandData> returnFloorList;

	private ArrayList<CommandData> returnElevatorList;

	/**
	 * Constructor
	 * @param commands List of elevator commands that the Scheduler will manage
	 */
	public Scheduler(ElevatorCommands commands) {
		this.commands = commands;
		this.floorList = commands.getFloorList();
		this.elevatorList = commands.getElevatorList();
		this.returnElevatorList = commands.getReturnElevatorList();
		this.returnFloorList = commands.getReturnFloorList();
	}

	;


	/**
	 * @Override default run method
	 */
	public void run() {
		sortElevatorCommands();
		notifyElevator();
		sortReturnCommands();
		notifyFloor();
	}

	/**
	 * Sorts through pending commands and delegates to the proper elevator
	 * No sorting algorithm yet
	 */
	private void sortElevatorCommands() {
		synchronized (floorList) {
			while (commands.getFloorSize() == 0) { //Wait until commands list is populated
				try {
					floorList.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			
			//Receive command and sort list
			//Sorting not yet implemented
			System.out.println("Server received command and sorting!");
			currentCommand = commands.getFloorCommand(0);
			floorList.notifyAll();
		}
	}

	/**
	 * Scheduler sends a command to an Elevator
	 */
	private void notifyElevator() {
		synchronized (elevatorList) {
			while (commands.getElevatorSize() > 0) { //Wait until elevator can accept new command
				try {
					elevatorList.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			
			//Give command to Elevator
			commands.addElevatorCommand(currentCommand); 
			System.out.println("Server sent command to elevator!");
			elevatorList.notifyAll();
		}
	}

	/**
	 * Sorts through commands returned by Elevators
	 */
	private void sortReturnCommands() {
		synchronized (returnElevatorList) {
			while (commands.getReturnESize() == 0) { //Wait until return commands list is populated
				try {
					returnElevatorList.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			
			//Add the returned command to the list and sort
			currentCommand = commands.getElevatorReturn(0); 
			System.out.println("Server received return command and sorting!");			
			returnElevatorList.notifyAll();
		}
	}

	/**
	 * Sends a returned command to the floor it originated from, confirming that it was executed properly
	 */
	private void notifyFloor() {
		synchronized (returnFloorList) {
			while (commands.getElevatorSize() > 0) { //Wait until elevator can accept new command (?)
				try {
					returnFloorList.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			
			//Return command to the floor
			commands.addFloorReturn(currentCommand); 
			System.out.println("Server sent command to floor!");
			returnFloorList.notifyAll();
		}
	}
}