
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Floor extends Thread {
	private ElevatorCommands commands;
	public ArrayList<CommandData> floorList;
	private CommandData commandSent;

	public Floor(ElevatorCommands commands) {
		this.commands = commands;
		this.floorList = commands.getFloorList();
		commandSent = null;
	}


	/**
	 * @Override default run method
	 */
	public void run() {

		createCommand(1, 5, "up");
		// getCommand()
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
			System.out.println("Created Command and sent to server!");
		}
	}
}


//
//Floor would remember its command
