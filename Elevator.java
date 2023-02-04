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
	private ArrayList<CommandData> returnList;
	
	 
	
	public Elevator(ElevatorCommands commands) {
		this.commands = commands;
		currentCommand = null;
		this.elevatorList = commands.getElevatorList();
		this.returnList = commands.getReturnElevatorList();
	}
	
	/**
	 * @Override default run method
	 */
	@Override
	public void run() {
			waitForCommand();
			respondBack();
	}
	
	private void waitForCommand() {
		synchronized (elevatorList) {
			while (commands.getElevatorSize() == 0) {
				try {
					elevatorList.wait();
				} catch (InterruptedException e) {
					return;
				}
			}
				currentCommand = commands.getElevatorCommand(0);
				System.out.println("Elevator received command responding back!");
				elevatorList.notifyAll();
		}
	}

	private void respondBack(){
			synchronized (returnList){
				while (commands.getReturnESize() < 0) {
					try {
						returnList.wait();
					} catch (InterruptedException e) {
						return;
					}
			}
				commands.addElevatorReturn(currentCommand);
				System.out.println("Elevator sent command back!");
				returnList.notifyAll();

		}
	
	
}
}
