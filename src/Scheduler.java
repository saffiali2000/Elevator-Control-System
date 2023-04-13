import java.io.*;
import java.net.*;
import java.util.ArrayList;

//import Scheduler.SchedulerState;

/**
 * Manages and distributes commands between Floors and Elevators
 * @author Ashwin Stoparczyk
 * @author Saffi Ali
 *
 */
public class Scheduler extends Thread {
	public enum SchedulerState {Idle, Sorting};
	DatagramPacket sendElevCommandPkt, recevFloorCommandPkt, sendFloorConfirm,receiveElevUpdate,sendElevReply;
	DatagramSocket sendReceiveSocket, receiveSocket,sendFloorReply,receiveElevInfo,sendFloorUpdate;
	private CommandData currentCommand; //Currently-managed command

	private ArrayList<ElevatorSubsystem> elevatorList;
	private ArrayList<Floor> floorList;
	
	private SchedulerState schedulerState;

	private SendReceiveElevator sendThread;

	private int elevNum; //Number of Elevators managed by the Scheduler

	private int portNum;

	/**
	 * Constructor
	 * @param commands List of elevator commands that the Scheduler will manage
	 */
	public Scheduler(int portNum,int elevNum) {
		schedulerState = SchedulerState.Idle;
		this.portNum = portNum;
		this.elevatorList = new ArrayList<ElevatorSubsystem>();
		this.floorList = new ArrayList<Floor>();
		this.elevNum = elevNum;
		try {
			// Construct datagram sockets
			sendReceiveSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(23);

			receiveElevInfo = new DatagramSocket(5507);
			sendFloorUpdate = new DatagramSocket(24);

		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
		sendThread = new Scheduler.SendReceiveElevator();
		sendThread.start();
	}

	
	/**
	 * @Override default run method
	 */
	public void run() {
		for (int i = 0;i < elevNum; i++) {
			recevElevInfo();
		}
		while (true) {
			receiveFloorCommand();
			sortCommands();
		}
	}

	/**
	 * Receives an update from an elevator via UDP
	 */
	private void recevElevInfo(){
		byte[] data = new byte[5000];
		DatagramPacket receiveActiveElev = new DatagramPacket(data, data.length);
		System.out.println("Waiting for Elevator Info.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			receiveElevInfo.receive(receiveActiveElev);
			ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
			Object o = is.readObject();
			is.close();
			elevatorList.add((ElevatorSubsystem) o);

		} catch (IOException | ClassNotFoundException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Received Info.\n");

	}

	/**
	 * Sorts through pending commands and delegates to the proper elevator
	 * No sorting algorithm yet
	 */
	private void sortCommands() {
			//Update state
			if (schedulerState != SchedulerState.Sorting) {
			
				System.out.println("Received command and sorting!");
				schedulerState = SchedulerState.Sorting;

				//Decide if command is valid needs to be refined
				//if (!(currentCommand.getDir().equals("up") || currentCommand.getDir().equals("down") || currentCommand.getDest().equals("floor") || currentCommand.getDest().equals("server") || currentCommand.getDest().equals("elevator") ||
					//currentCommand.getSource().equals("floor") || currentCommand.getSource().equals("server") || currentCommand.getSource().equals("elevator")) || currentCommand.getDest().equals(currentCommand.getSource()) ||
					//currentCommand.getStartFloor() > elevatorList.size() || currentCommand.getDestFloor() > elevatorList.size()) {
					//System.out.println("Command invalid. Removing");
					//currentCommand = null;
				//}
	
				//Determine best elevator to send command to
				ElevatorSubsystem closestElevator = determineClosestElevator();
	
				//Send command
				if (currentCommand.getDest().equals("elevator") ) {
					schedulerState = SchedulerState.Idle;
					sendCommandElevator(closestElevator);
				}
	
				//Update state
				schedulerState = SchedulerState.Idle;
				//commands.notifyAll();
			//}
			} else {System.out.println("Not ready to sort yet");}
	}

	/**
	 * Scheduler sends a command to either an Elevator
	 * @param elevator The elevator the command is sent to
	 */
	public void sendCommandElevator(ElevatorSubsystem elevator) {
		if (schedulerState == SchedulerState.Idle) {
			try {
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
				ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
				os.flush();
				os.writeObject(currentCommand);
				os.flush();
	
				//retrieves byte array
				byte[] sendMsg = byteStream.toByteArray();
				sendElevCommandPkt = new DatagramPacket(sendMsg, sendMsg.length,
						InetAddress.getLocalHost(), elevator.getPortNum());
				os.close();
	
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
	
			//Print out content of the message host is sending
			System.out.println("Sending command to chosen elevator");
	
			// Send the datagram packet to the server via the socket.
			try {
				sendReceiveSocket.send(sendElevCommandPkt);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
	
			System.out.println("Command sent to elevator\n");
			schedulerState = SchedulerState.Sorting;
			//sendReceiveSocket.close();
	
			} else {System.out.println("Scheduler is still sorting, cannot accept command immediately");}
	}

	/**
	 * Scheduler receives an update from a Floor
	 */
	public void recevUpdateFloor() {
		byte[] data = new byte[5000];
		DatagramPacket receiveUpdateRequest = new DatagramPacket(data, data.length);
		System.out.println(" Waiting for Update Request from floor!.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			sendFloorUpdate.receive(receiveUpdateRequest);

		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Received Update Request from floor!.\n");
	}
	
	/**
	 * Scheduler send an update to the floor subsystem via UDP
	 */
	public void sendUpdateFloor(){
		DatagramPacket sendReply;
		byte[] sendMsg = "Elevator received Command!".getBytes();
		try {
			sendReply = new DatagramPacket(sendMsg, sendMsg.length,
					InetAddress.getLocalHost(), 24);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		//Print out content of the message host is sending
		System.out.println("Sending elevator update to floor");

		// Send the datagram packet to the server via the socket.
		try {
			sendFloorUpdate.send(sendReply);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Update sent to floor\n");
	}

	/**
	 * Determines the Elevator closest to the current command's destination
	 * Algorithm:
	 * Scheduler first searches for any elevators that are moving towards the destination floor. If at least one exists, only they are considered. Otherwise, all are considered
	 * Scheduler then determines the elevator that will take the shortest amount of time to reach the destination floor i.e smallest gap in floor difference
	 *
	 * @return closestElevator The Elevator which is closest to the destination floor
	 */
	public ElevatorSubsystem determineClosestElevator(){
		ArrayList<ElevatorSubsystem> consideredElevators = new ArrayList<>();
		for (ElevatorSubsystem el : elevatorList){
			CommandData compCommand = el.getCurrentCommand(); //If empty ignore tba later once elevators implementation is finalized
			if (compCommand == null){
				consideredElevators.add(el);
			}
			else if ((compCommand.getDestFloor() < currentCommand.getDestFloor() && compCommand.getDir().equals("down")) ||
					(compCommand.getDestFloor() > currentCommand.getDestFloor() && compCommand.getDir().equals("up"))){
				consideredElevators.add(el);
			}
		}

		//If no elevators currently moving towards destination floor, consider all elevators
		if (consideredElevators.isEmpty()) consideredElevators = elevatorList;
		ElevatorSubsystem closestElevator = elevatorList.get(0);
		int closest = Math.abs(closestElevator.getDestination() - currentCommand.getDestFloor());

		//Iterate through considered elevators and choose the one closest to the next floor
		for (ElevatorSubsystem el : consideredElevators){
			if (Math.abs(el.getDestination() - closestElevator.getDestination()) < closest) closestElevator = el;
		}

		System.out.println("Closest elevator is: " + elevatorList.indexOf(closestElevator));
		return closestElevator;
	}

	/**
	 * Receive and send method
	 * Sends and receives messages from and to client/server, waiting every time it has to receive
	 */
	private void receiveFloorCommand()
	{
		// Construct a DatagramPacket for receiving floor packets up
		// to 100 bytes long (the length of the byte array).
		byte[] data = new byte[5000];
		recevFloorCommandPkt = new DatagramPacket(data, data.length);

		System.out.println("Waiting for Command from floor.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			receiveSocket.receive(recevFloorCommandPkt);
			ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
			Object o = is.readObject();
			is.close();
			currentCommand = (CommandData) o;

		} catch (IOException | ClassNotFoundException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Received a command.\n");

		try {
			sendFloorReply = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}

		String reply = "Received!";
		byte[] sendMsg = reply.getBytes();
		sendFloorConfirm = new DatagramPacket(sendMsg, sendMsg.length,
				recevFloorCommandPkt.getAddress(), recevFloorCommandPkt.getPort());

		//Print out content of the message host is sending
		System.out.println("Sending message confirmation to floor:");

		// Send the datagram packet to the server via the socket.
		try {
			sendFloorReply.send(sendFloorConfirm);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Confirmation sent to floor\n");
	}

	/**
	 * Receive and send method
	 * Sends and receives messages from and to client/server, waiting every time it has to receive
	 */
	private void receiveUpdateElevator()
	{
		// Construct a DatagramPacket for receiving floor packets up
		// to 100 bytes long (the length of the byte array).
		byte[] data = new byte[5000];
		receiveElevUpdate = new DatagramPacket(data, data.length);
		System.out.println("Waiting for Elevator to send update.\n");
		int index = 0;

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			sendReceiveSocket.receive(receiveElevUpdate);
			ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
			Object o = is.readObject();
			is.close();
			ElevatorSubsystem tempElevator = (ElevatorSubsystem) o;
			for (ElevatorSubsystem elevator : elevatorList){
				if (elevator.getPortNum() == tempElevator.getPortNum()){
					index = elevatorList.indexOf(elevator);
				}
			}
			elevatorList.set(index,tempElevator);

		} catch (IOException | ClassNotFoundException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Received update from elevator.\n");

		String reply = "Received Update!";
		byte[] sendMsg = reply.getBytes();
		sendElevReply = new DatagramPacket(sendMsg, sendMsg.length,
				receiveElevUpdate.getAddress(), receiveElevUpdate.getPort());

		//Print out content of the message host is sending
		System.out.println("Sending message confirmation to Elevator:");

		// Send the datagram packet to the server via the socket.
		try {
			sendReceiveSocket.send(sendElevReply);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Confirmation sent to Elevator\n");

	}
	
	//Getter
	public CommandData getCurrentCommand() {
		return currentCommand;
	}
	
	/**
	 * Background thread that manages elevator and floor updates
	 * @author Ashwin Stoparczyk
	 *
	 */
	private class SendReceiveElevator extends Thread {
		@Override
		public void run() {
			while (true) {
				recevUpdateFloor();
				receiveUpdateElevator();
				sendUpdateFloor();
				}
			}
		}
	
	/**
	 * Main runnable method
	 * @param args default
	 */
	public static void main(String[] args) {

		Scheduler scheduler = new Scheduler(23,1);
		scheduler.start();
	}
}
