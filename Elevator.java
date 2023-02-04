import java.time.LocalTime;
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
	private ArrayList<CommandData> elevatorList;
	
	 
	
	public Elevator(ElevatorCommands commands) {
		this.commands = commands;
		currentCommand = null;
		this.elevatorList = commands.getElevatorList();
	}
	
	/**
	 * @Override default run method
	 */
	@Override
	public void run() {
			waitForCommand();

	}
	
	
	private void waitForCommand() {
		synchronized (elevatorList) {
			while (commands.getElevatorSize() == 0) {
				try {
					elevatorList.wait();
				} catch (InterruptedException e) {
					return;
				}
				currentCommand = commands.getElevatorCommand(0);
				System.out.println("Elevator received command responding back!");
			}
		}
	}

	private void respondBack(){
		{

		}
	}
	
	
}

//Elevator asks the schedular if theres a command ready
//if yes
//takes from the list 
//schedular gives index to elevator 
