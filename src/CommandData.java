import java.io.Serializable;
import java.time.LocalTime;

/**
 * Structure for passing commands between threads
 * @author Ashwin Stoparczyk
 *
 */
public class CommandData implements Serializable {
	private String time;
	private int startFloor;
	private int destFloor;
	private String dir;
	private String source;
	private String dest;
	
	/**
	 * Constructor
	 * 
	 * @param time Time command was made
	 * @param startFloor Starting floor of elevator
	 * @param destFloor Destination floor of elevator
	 * @param dir Direction from starting to destination floor (up or down)
	 * @param source Source of the command ("floor", "scheduler", or "elevator")
	 * @param dest Destination of the command ("floor", "scheduler", or "elevator")
	 */
	public CommandData(String time, int startFloor, int destFloor, String dir, String source, String dest){
		this.setTime(time);
		this.setStartFloor(startFloor);
		this.setDestFloor(destFloor);
		this.setDir(dir);
		this.setSource(source);
		this.setDest(dest);
	}


	/**
	 * Getters and setters for CommandData fields
	 */
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}
}
