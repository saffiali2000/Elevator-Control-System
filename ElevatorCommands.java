import java.util.*;

public class ElevatorCommands {
	
	ArrayList<CommandData> sharedCommands;
	boolean empty;
	boolean commandAdded;
	
	public ElevatorCommands() {
		
		sharedCommands = new ArrayList<CommandData>();
		empty = true;
		
		
	}
	
	//Floor calls this
	public synchronized void addCommand(CommandData command) {
		while(sharedCommands.size() >= 10 && commandAdded == true) { //Testing, but technically for now don't need a cap
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		sharedCommands.add(command);
		commandAdded = true; 
		notifyAll();
	}
	
	
	public synchronized CommandData getCommand() {
		while(!empty) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		notifyAll();
		return sharedCommands.get(0);
	}
	
	
	public synchronized void organizedCommands() {
		while(empty || commandAdded == false ) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//organize
		commandAdded = false;
		notifyAll();
	}
	
	public int getSize() {
		return sharedCommands.size();
	}

}

// For this iteration the Scheduler is only being used as a communication channel from the Floor thread to the Elevator
//thread and back again.

//elevator calls 

//Command Add -> scheduler checks to see command is added to list, and if it has tells elevator the command

