import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class Errors {
    private int currentFloor;
    private boolean doorOpen;
    private boolean moving;
    private boolean fault;
    private int faultFloor;
    private Timer floorTimer;
    private Timer doorTimer;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    // Constructor to initialize elevator state
    public Errors() {
        currentFloor = 0;
        doorOpen = false;
        moving = false;
        fault = false;
        faultFloor = -1;
        floorTimer = new Timer();
        doorTimer = new Timer();
        socket = null;
        inputStream = null;
        outputStream = null;
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
                if (currentFloor != floor) { // Check if elevator is stuck between floors
                    fault = true;
                    faultFloor = currentFloor;
                    sendFaultData(); // Send fault data to scheduler
                    stopElevator(); // Stop elevator movement
                }
            }
        }, timeout);
    }

    private void stopElevator() {
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
                if (!doorOpen) { // Check if door is stuck closed
                    fault = true;
                    faultFloor = currentFloor;
                    sendFaultData(); // Send fault data to scheduler
                }
            }
        }, timeout);
    }

    private void sendFaultData() {
    }

    // Stops the door timer
    public void stopDoorTimer() {
        doorTimer.cancel();
        doorTimer = new Timer();
    }

    // Connects to the scheduler using a data socket
    public void connectToScheduler(String host, int port) {
        try {
            socket = new Socket(host, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error connecting to scheduler: " + e.getMessage());
        }
    }
}

// Sends elevator state data to the scheduler
