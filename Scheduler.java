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

	public Scheduler(ElevatorCommands commands) {
		this.commands = commands;
		this.floorList = commands.getFloorList();
		this.elevatorList = commands.getElevatorList();
	};
	

	/**
	 * @Override default run method
	 */
	public void run() {
			sortElevatorCommands();
			notifyElevator();
	}
	
	/**
	 * Sorts through pending commands and delegates to the proper elevator
	 * No sorting algorithm yet
	 */
	private void sortElevatorCommands() {
		synchronized(floorList) {
			while (commands.getFloorSize() == 0) {
				try {
					wait();
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

		private void notifyElevator(){
		synchronized (elevatorList){
			while(commands.getElevatorSize()>0){
				try{
					wait();
				} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
			commands.addElevatorCommand(currentCommand);
			System.out.println("Sent command to elevator!");
			elevatorList.notifyAll();
		}
		}
	}


//Assumption
//Schedular is modify the list by a certain algorithm




