import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Floor that can create commands for elevators
 * @author Saffi Ali
 *
 */
public class Floor extends Thread {
	private ElevatorCommands commands; //Shared list of commands
	//public ArrayList<CommandData> floorList; //Log of all commands sent by this floor
	private CommandData commandSent; //Original command created and sent to scheduler
	private CommandData commandConfirmed; //COmmand executed by elevator and returned by scheduler

	/**
	 * Constructor
	 * @param commands List of commands relevant to this floor
	 */
	public Floor(ElevatorCommands commands) {
		this.commands = commands;
		//this.floorList = new ArrayList<CommandData>();
		commandSent = null;
	}


	/**
	 * @Override default run method
	 */
	public void run() {
		createCommand(1, 5, "up"); //Read from input file here
		while (true) {
			waitForCommand();
		}
	}

	/**
	 * Creates a floor-source command and sends it to Scheduler
	 * @param startFloor Starting floor of elevator
	 * @param destFloor Destination floor of elevator
	 * @param dir Up or down
	 */
	public void createCommand(int startFloor, int destFloor, String dir) {
		synchronized (commands) {
			while (commands.getSize() > 0) { //Wait until commands list is empty
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			
			//Create a new command and send it to the Scheduler
			LocalTime time = LocalTime.now();
			CommandData command = new CommandData(time, startFloor, destFloor, dir, "floor", "scheduler");
			commandSent = command;
			commands.addCommand(command);
			System.out.println("Floor created command and sent to server!");
			commands.notifyAll();
		}
	}

	/**
	 * Floor waits for its command to be returned to itself by Scheduler, to confirm the command was executed properly
	 */
	private void waitForCommand() {
		synchronized (commands) {
			while (commands.getElevatorSize() == 0) { //Wait until commands list is populated
				try {
					wait();
				} catch (InterruptedException e) {
					return;
				}

				boolean validCommand = false; //Returning command recieved

				//Iterate through commands to check for a command going from scheduler to this floor, which must be a returning command
				for (CommandData cd : commands){
					if (cd.getSource().equals("scheduler") && cd.getDest().equals("floor")){
						commandConfirmed = commands.getFloorReturn(0);
						validCommand = true;
					}
				}

				//If floor found a returning command compare it to the one it sent previously
				if (validCommand) {
					//Check if the returned command is the same as the original command
					if (commandSent == commandConfirmed) {
						System.out.println("Floor received command back!");
						//floorList.add(commandSent);
					} else {System.out.println("Floor received incorrect command back.");}

					commands.notifyAll();
					System.exit(0);
				}
			}
		}
	}
}
