import java.util.*;

/**
 * Collection of methods used by different commands lists
 * @author Walid Baitul Islam
 *
 */
public class ElevatorCommands {
	
	private ArrayList<CommandData> commands;

	/**
	 * Constructor
	 */
	public ElevatorCommands() {
		commands = new ArrayList<CommandData>();
	}
	
	/**
	 * Adds a specified command to the proper commands list
	 * @param command The command to be added
	 */
	public void addCommand(CommandData command) {commands.add(command);}


	/**
	 * Getters for a specific command
	 * @param index Index of the command to be retrieved
	 * @return The command at the specified index
	 */
	/*
	public CommandData getCommand(int index) {
		CommandData temp = floorCommands.get(index);
		floorCommands.remove(index);
		return temp;
	}
	 */

	/**
	 * Getters for size of commands list
	 * @return The number of commands in the specified list
	 */
	public int getSize() {
		return commands.size();
	}

	/**
	 * Getters for commands lists themselves
	 * @return The specified commands list
	 */
	public ArrayList<CommandData> getList(){
		return commands;
	}

}