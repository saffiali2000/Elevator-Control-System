import java.util.*;

public class ElevatorCommands {
	
	ArrayList<CommandData> floorCommands;
	ArrayList<CommandData> elevatorCommands;

	ArrayList<CommandData> returnFloorCommands;
	ArrayList<CommandData> returnElevatorCommands;
	
	public ElevatorCommands() {
		
		floorCommands = new ArrayList<CommandData>();
		elevatorCommands = new ArrayList<CommandData>();
		returnFloorCommands = new ArrayList<CommandData>();
		returnElevatorCommands = new ArrayList<CommandData>();

	}
	
	//Floor calls this
	public void addFloorCommand(CommandData command) {floorCommands.add(command);}
	public void addFloorReturn(CommandData command) { returnFloorCommands.add(command);}

	public void addElevatorCommand(CommandData command) {elevatorCommands.add(command);}
	public void addElevatorReturn(CommandData command) {returnElevatorCommands.add(command);}
	
	
	public CommandData getFloorCommand(int index) {
		CommandData temp = floorCommands.get(index);
		floorCommands.remove(index);
		return temp;

	}

	public CommandData getElevatorCommand(int index) {
		CommandData temp = elevatorCommands.get(index);
		elevatorCommands.remove(index);
		return temp;
	}

	public CommandData getFloorReturn(int index) {
		CommandData temp = returnFloorCommands.get(index);
		returnFloorCommands.remove(index);
		return temp;

	}

	public CommandData getElevatorReturn(int index) {
		CommandData temp = returnElevatorCommands.get(index);
		returnElevatorCommands.remove(index);
		return temp;
	}

	
	public int getFloorSize() {
		return floorCommands.size();
	}

	public int getElevatorSize() {
		return elevatorCommands.size();
	}

	public int getReturnESize() {
		return returnElevatorCommands.size();
	}

	public int getReturnFSize() {
		return returnFloorCommands.size();
	}

	public ArrayList<CommandData> getFloorList(){
		return floorCommands;
	}

	public ArrayList<CommandData> getElevatorList(){
		return elevatorCommands;
	}

	public ArrayList<CommandData> getReturnFloorList(){
		return returnFloorCommands;
	}

	public ArrayList<CommandData> getReturnElevatorList(){
		return returnElevatorCommands;
	}

}

// For this iteration the Scheduler is only being used as a communication channel from the Floor thread to the Elevator
//thread and back again.

//elevator calls 

//Command Add -> scheduler checks to see command is added to list, and if it has tells elevator the command

