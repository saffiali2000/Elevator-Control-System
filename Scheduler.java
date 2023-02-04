import java.util.ArrayList;
import java.util.List;

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
			while (commands.getFloorSize() == 0) {
				try {
					floorList.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			System.out.println("Server received command and sorting!");
			currentCommand = commands.getFloorCommand(0);
			floorList.notifyAll();
		}
	}

	private void notifyElevator() {
		synchronized (elevatorList) {
			while (commands.getElevatorSize() > 0) {
				try {
					elevatorList.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			commands.addElevatorCommand(currentCommand);
			System.out.println("Server sent command to elevator!");
			elevatorList.notifyAll();
		}
	}

	private void sortReturnCommands() {
		synchronized (returnElevatorList) {
			while (commands.getReturnESize() == 0) {
				try {
					returnElevatorList.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			System.out.println("Server received return command and sorting!");
			currentCommand = commands.getElevatorReturn(0);
			returnElevatorList.notifyAll();
		}
	}

	private void notifyFloor() {
		synchronized (returnFloorList) {
			while (commands.getElevatorSize() > 0) {
				try {
					returnFloorList.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			commands.addFloorReturn(currentCommand);
			System.out.println("Server sent command to floor!");
			returnFloorList.notifyAll();
		}
	}
}




