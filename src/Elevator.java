import java.util.*;

/**
 * 
 * Elevator that will consumer information from the command list to eventually move 
 * @author Walid Baitul Islam
 *
 */


public class Elevator extends Thread{
		
	private ElevatorCommands commands;
	private CommandData currentCommand;
	private ArrayList<CommandData> elevatorList;
	private ArrayList<CommandData> returnList;
	
	 
	/**
	 * Constructor
	 * 
	 * @param commands ArrayList<CommandData> list of information that will be consumed
	 */
	public Elevator(ElevatorCommands commands) {
		this.commands = commands;
		currentCommand = null;
		this.elevatorList = commands.getElevatorList();
		this.returnList = commands.getReturnElevatorList();
	}
	
	/**
	 * @Override default run method
	 */
	public void run() {
			waitForCommand();
			respondBack();
	}
	
	/**
	 * Elevator waits for a command to be processed by the Scheduler, then executes it
	 */
	private void waitForCommand() {
		synchronized (elevatorList) {
			while (commands.getElevatorSize() == 0) { //Wait until commands list is populated
				try {
					elevatorList.wait();
				} catch (InterruptedException e) {
					return;
				}
			}
			
			//Grab next command
			currentCommand = commands.getElevatorCommand(0); 
			System.out.println("Elevator received command responding back!");
			elevatorList.notifyAll();
		}
	}

	/**
	 * Elevator sends response back to Scheduler to confirm its previous command was executed properly
	 */
	private void respondBack(){
		synchronized (returnList){
			while (commands.getReturnESize() < 0) { //Wait until returning commands list is empty
				try {
					returnList.wait();
				} catch (InterruptedException e) {
					return;
				}
			}
		
		//Send executed command back to Scheduler
		commands.addElevatorReturn(currentCommand); 
		System.out.println("Elevator sent command back!");
		returnList.notifyAll();
		}		
	}
}
