import java.util.*;

public class ElevatorCommands {
	
	ArrayList<CommandData> floorCommands;
	ArrayList<CommandData> elevatorCommands;
	
	public ElevatorCommands() {
		
		floorCommands = new ArrayList<CommandData>();
		elevatorCommands = new ArrayList<CommandData>();

	}
	
	//Floor calls this
	public void addFloorCommand(CommandData command) {
		floorCommands.add(command);
	}
	
	
	public CommandData getFloorCommand(int index) {
		CommandData temp = floorCommands.get(index);
		floorCommands.remove(index);
		return temp;

	}

	public void addElevatorCommand(CommandData command) {
		elevatorCommands.add(command);
	}


	public CommandData getElevatorCommand(int index) {
		CommandData temp = elevatorCommands.get(index);
		elevatorCommands.remove(index);
		return temp;
	}
	
	public int getFloorSize() {
		return floorCommands.size();
	}

	public int getElevatorSize() {
		return elevatorCommands.size();
	}

	public ArrayList<CommandData> getFloorList(){
		return floorCommands;
	}

	public ArrayList<CommandData> getElevatorList(){
		return elevatorCommands;
	}

}

// For this iteration the Scheduler is only being used as a communication channel from the Floor thread to the Elevator
//thread and back again.

//elevator calls 

//Command Add -> scheduler checks to see command is added to list, and if it has tells elevator the command

