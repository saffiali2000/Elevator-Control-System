
import java.time.LocalTime;
import java.util.List;

public class Floor extends Thread{
	private ElevatorCommands commands;
	private CommandData commandSent;
	
	public Floor(ElevatorCommands commands) {
		this.commands = commands;
		commandSent = null;
	}

	
	/**
	 * @Override default run method
	 */
	public void run() {
		
		createCommand(1,5,"up");
		
		}
		

	public void createCommand(int startFloor,int destFloor,String dir){
			LocalTime time = LocalTime.now();
			CommandData command = new CommandData(time,startFloor,destFloor,dir);
			commandSent=command;
			commands.addCommand(command);
	}

	public CommandData getCommand(){
		return commandSent;
	}

}

//
//Floor would remember its command
