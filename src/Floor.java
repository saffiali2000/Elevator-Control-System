import java.time.LocalTime;
import java.util.List;

public class Floor extends Thread{
	private List<CommandData> commands;
	
	public Floor(List<CommandData> commands) {
		this.commands = commands;
	}

	
	/**
	 * @Override default run method
	 */
	public void run() {
		createCommand(1,5,"up");
		System.out.println("Floor created command");
		synchronized(this) {
			while (commands.size() > 0) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				System.out.println("Floor Checking for Command");
				getCommand();
			}
		}
	}

	public void createCommand(int startFloor,int destFloor,String dir){
			LocalTime time = LocalTime.now();
			CommandData command = new CommandData(time,startFloor,destFloor,dir);
			commands.add(command);
	}

	public void getCommand(){
		commands.remove(0);
		System.out.println("Floor recieved message from Elevator");

	}

}
