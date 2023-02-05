import java.time.LocalTime;

/**
 * Structure for passing commands between threads
 * @author Ashwin Stoparczyk
 *
 */
public class CommandData {
	private LocalTime time;
	private int startFloor;
	private int destFloor;
	private String dir;
	
	/**
	 * Constructor
	 * 
	 * @param time Time command was made
	 * @param startFloor Starting floor of elevator
	 * @param destFloor Destination floor of elevator
	 * @param dir Direction from starting to destination floor (up or down)
	 */
	public CommandData(LocalTime time, int startFloor, int destFloor, String dir){
		this.setTime(time);
		this.setStartFloor(startFloor);
		this.setDestFloor(destFloor);
		this.setDir(dir);
	}


	/**
	 * Getters and setters for CommandData fields
	 */
	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public int getStartFloor() {
		return startFloor;
	}

	public void setStartFloor(int startFloor) {
		this.startFloor = startFloor;
	}

	public int getDestFloor() {
		return destFloor;
	}

	public void setDestFloor(int destFloor) {
		this.destFloor = destFloor;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}	
}
