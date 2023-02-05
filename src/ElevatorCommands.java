import java.util.*;

/**
 * Collection of methods used by different commands lists
 * @author Walid Baitul Islam
 *
 */
public class ElevatorCommands {
	
	ArrayList<CommandData> floorCommands;
	ArrayList<CommandData> elevatorCommands;

	ArrayList<CommandData> returnFloorCommands;
	ArrayList<CommandData> returnElevatorCommands;
	
	/**
	 * Constructor
	 */
	public ElevatorCommands() {
		
		floorCommands = new ArrayList<CommandData>();
		elevatorCommands = new ArrayList<CommandData>();
		returnFloorCommands = new ArrayList<CommandData>();
		returnElevatorCommands = new ArrayList<CommandData>();

	}
	
	/**
	 * Adds a specified command to the proper commands list
	 * @param command The command to be added
	 */
	public void addFloorCommand(CommandData command) {floorCommands.add(command);}
	public void addFloorReturn(CommandData command) { returnFloorCommands.add(command);}

	public void addElevatorCommand(CommandData command) {elevatorCommands.add(command);}
	public void addElevatorReturn(CommandData command) {returnElevatorCommands.add(command);}
	
	
	/**
	 * Getters for a specific command
	 * @param index Index of the command to be retrieved
	 * @return The command at the specified index
	 */
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

	
	/**
	 * Getters for size of commands list
	 * @return The number of commands in the specified list
	 */
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

	/**
	 * Getters for commands lists themselves
	 * @return The specified commands list
	 */
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