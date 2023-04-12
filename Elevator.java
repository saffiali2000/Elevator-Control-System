import java.util.Timer;
import java.util.TimerTask;

public class Elevator{
    // Fields to represent the state of the elevator
    private int currentFloor;
    private boolean doorOpen;
    private boolean moving;
    private boolean fault;
    private int faultFloor;
    private Timer floorTimer;
    private Timer doorTimer;

    // Constructor to initialize elevator state
    public Elevator() {
        currentFloor = 0;
        doorOpen = false;
        moving = false;
        fault = false;
        faultFloor = -1;
        floorTimer = new Timer();
        doorTimer = new Timer();
    }

    // Getter for current floor
    public int getCurrentFloor() {
        return currentFloor;
    }

    // Setter for current floor
    public void setCurrentFloor(int floor) {
        currentFloor = floor;
    }

    // Getter for door open state
    public boolean isDoorOpen() {
        return doorOpen;
    }

    // Setter for door open state
    public void setDoorOpen(boolean open) {
        doorOpen = open;
    }

    // Getter for elevator moving state
    public boolean isMoving() {
        return moving;
    }

    // Setter for elevator moving state
    public void setMoving(boolean move) {
        moving = move;
    }

    // Getter for fault state
    public boolean hasFault() {
        return fault;
    }

    // Setter for fault state
    public void setFault(boolean hasFault) {
        fault = hasFault;
    }

    // Getter for fault floor
    public int getFaultFloor() {
        return faultFloor;
    }

    // Setter for fault floor
    public void setFaultFloor(int floor) {
        faultFloor = floor;
    }

    // Starts the floor timer
    public void startFloorTimer(int floor, int timeout) {
        floorTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Check if the elevator has reached the correct floor
                if (currentFloor != floor) {
                    // Assume the elevator is stuck between floors or the floor sensor has failed
                    fault = true;
                    faultFloor = currentFloor;
                }
            }
        }, timeout);
    }

    // Stops the floor timer
    public void stopFloorTimer() {
        floorTimer.cancel();
        floorTimer = new Timer();
    }

    // Starts the door timer
    public void startDoorTimer(int timeout) {
        doorTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Check if the door is open
                if (!doorOpen) {
                    // Assume the door is stuck closed or the door sensor has failed
                    fault = true;
                    faultFloor = currentFloor;
                }
            }
        }, timeout);
    }

    // Stops the door timer
    public void stopDoorTimer() {
        doorTimer.cancel();
        doorTimer = new Timer();
    }
}
