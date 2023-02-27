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
		while (true) {
			waitForCommand();
			respondBack();
		}
	}
	
	/**
	 * Elevator waits for a command to be processed by the Scheduler, then executes it
	 */
	private void waitForCommand() {
		synchronized (commands) {
			while (commands.getSize() < 1) { //Wait until commands list is populated
				try {
					wait();
				} catch (InterruptedException e) {
					return;
				}
			}

			boolean validCommand = false;

			//Iterate through commands to check for a command going from scheduler to this elevator, which must be an actionable command
			for (CommandData cd : commands){
				if (cd.getSource().equals("scheduler") && cd.getDest().equals("elevator")){
					currentCommand = cd;
					validCommand = true;
				}
			}

			if (validCommand) {
				System.out.println("Elevator received command responding back!");
			} else { System.out.println("No elevator-bound command exists, continue waiting");}
			commands.notifyAll();
		}
	}

	/**
	 * Elevator sends response back to Scheduler to confirm its previous command was executed properly
	 */
	private void respondBack(){
		synchronized (commands){
			while (commands.getSize() < 0) { //Wait until commands list is empty
				try {
					wait();
				} catch (InterruptedException e) {
					return;
				}
			}
		
		//Send executed command back to Scheduler
		commands.addCommand(currentCommand.getTime(), currentCommand.getStartFloor(), currentCommand.getDestFloor(), currentCommand.getDir(), "elevator", "scheduler");
		System.out.println("Elevator sent command back!");
		commands.notifyAll();
		}		
	}
}
