

/**
 * Simulates the subsystems of an elevator.
 * @author Henry Lin
 *
 */

public class ElevatorSubsystem {
	public enum ElevatorState {Idle, Open, MovingUp, MovingDown};
	/** The Elevator's current state. */
	private ElevatorState currentState;
	/** Tells whether the door is open or not. */
	private boolean doorOpen;
	/** The elevator's current floor. */
	private int curr;
	/** The elevator's current destination floor. */
	private int dest;

	/**
	 * Create a new ElevatorSubsystem in the Idle state with doors closed.
	 */
	public ElevatorSubsystem() {
		currentState = ElevatorState.Idle;
		doorOpen = false;
		curr = 1;
		dest = 1;
	}
	
	/**
	 * Handle the buttonPressed event.
	 */
	public void handleButtonPressed() {
		if (currentState == ElevatorState.Idle) {
			if (dest > curr) {
				currentState = ElevatorState.MovingUp;
				move();
			} else if (dest < curr) {
				currentState = ElevatorState.MovingDown;
				move();
			} else if (curr == dest) {
				currentState = ElevatorState.Open;
				openDoors();
			}
		}
	}
	
	/**
	 * Simulate the activity of opening the doors.
	 */
	private void openDoors() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		doorOpen = true;
	}
	
	/**
	 * Handle the doorClosed event.
	 */
	public void handleDoorClosed() {
		if (currentState == ElevatorState.Open) {
			doorOpen = false;
			if (dest > curr) {
				currentState = ElevatorState.MovingUp;
				move();
			} else if (dest < curr) {
				currentState = ElevatorState.MovingDown;
				move();
			} else if (dest == curr) {
				currentState = ElevatorState.Idle;
			}
		}
	}
	
	/**
	 * Handle the arrived event.
	 */
	public void handleArrived() {
		if (currentState == ElevatorState.MovingUp || currentState == ElevatorState.MovingDown) {
			currentState = ElevatorState.Open;
			openDoors();
		}
	}
	
	/**
	 * Move the elevator to the destination floor.
	 */
	private void move() {
		try {
			Thread.sleep((long) (timeToTravel(curr, dest) * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		curr = dest;
	}
	
	/**
	 * Provide the required time (in seconds) to travel between the specified floors.
	 * @param from Floor to start from
	 * @param to Floor to end at
	 * @return The time in seconds to travel between the floors
	 */
	public static double timeToTravel(int from, int to) {
		return Math.abs(to - from) * 3 + 4;
	}
	
	/**
	 * Return the elevator's current state.
	 * @return The elevator's current state
	 */
	public ElevatorState getState() {
		return currentState;
	}
	
	/**
	 * Set the destination floor to the given floor.
	 * @param floor The new destination
	 */
	public void setDestination(int floor) {
		dest = floor;
	}
	
	/**
	 * Return the current destination floor.
	 * @return The current destination
	 */
	public int getDestination() {
		return dest;
	}
	
	/**
	 * Return the current floor.
	 * @return The current floor
	 */
	public int getCurrentFloor() {
		return curr;
	}
}
