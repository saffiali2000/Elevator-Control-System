import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Floor that can create commands for elevators
 * @author Saffi Ali
 *
 */
public class Floor extends Thread {
	private ElevatorCommands commands;
	public ArrayList<CommandData> floorList;
	public ArrayList<CommandData> floorReturnList;
	private CommandData commandSent;
	private CommandData commandConfirmed;

	/**
	 * Constructor
	 * @param commands List of commands relevant to this floor
	 */
	public Floor(ElevatorCommands commands) {
		this.commands = commands;
		this.floorList = commands.getFloorList();
		this.floorReturnList = commands.getReturnFloorList();
		commandSent = null;
	}


	/**
	 * @Override default run method
	 */
	public void run() {
		createCommand(1, 5, "up");
		waitForCommand();
	}

	/**
	 * Creates a floor-source command and sends it to Scheduler
	 * @param startFloor Starting floor of elevator
	 * @param destFloor Destination floor of elevator
	 * @param dir Up or down
	 */
	public void createCommand(int startFloor, int destFloor, String dir) {
		synchronized (floorList) {
			while (commands.getFloorSize() > 0) { //Wait until floor commands list is empty
				try {
					floorList.wait();
				} catch (InterruptedException e) {
					return;
				}
			}
			
			//Create a new command and send it to the Scheduler
			LocalTime time = LocalTime.now();
			CommandData command = new CommandData(time, startFloor, destFloor, dir); 
			commandSent = command;
			commands.addFloorCommand(command);
			System.out.println("Floor created command and sent to server!");
			floorList.notifyAll();
		}
	}

	/**
	 * Floor waits for its command to be returned to itself by Scheduler, to confirm the command was executed properly
	 */
	private void waitForCommand() {
		synchronized (floorReturnList) {
			while (commands.getElevatorSize() == 0) { //Wait until commands list is populated
				try {
					floorReturnList.wait();
				} catch (InterruptedException e) {
					return;
				}
								
				commandConfirmed = commands.getFloorReturn(0);
				
				//Check if the returned command is the same as the original command
				if(commandSent == commandConfirmed) {
					System.out.println("Floor received command back!");
					floorReturnList.notifyAll();
					System.exit(0);
				}
				else {
					System.out.println("Floor received incorrect command back.");
					floorReturnList.notifyAll();
					System.exit(0);
				}
			}
		}
	}
}
