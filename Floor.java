
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Floor extends Thread {
	private ElevatorCommands commands;
	public ArrayList<CommandData> floorList;
	public ArrayList<CommandData> floorReturnList;
	private CommandData commandSent;
	private CommandData commandConfirmed;

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

	public void createCommand(int startFloor, int destFloor, String dir) {
		synchronized (floorList) {
			while (commands.getFloorSize() > 0) {
				try {
					floorList.wait();
				} catch (InterruptedException e) {
					return;
				}
			}
			LocalTime time = LocalTime.now();
			CommandData command = new CommandData(time, startFloor, destFloor, dir);
			commandSent = command;
			commands.addFloorCommand(command);
			System.out.println("Floor created command and sent to server!");
			floorList.notifyAll();
		}
	}

	private void waitForCommand() {
		synchronized (floorReturnList) {
			while (commands.getElevatorSize() == 0) {
				try {
					floorReturnList.wait();
				} catch (InterruptedException e) {
					return;
				}
					commandConfirmed = commands.getFloorReturn(0);
				if(commandSent == commandConfirmed) {
					System.out.println("Floor received command back!");
					floorReturnList.notifyAll();
					System.exit(0);
				}
			}
		}
	}
}
