import java.io.*;
import java.net.*;
import java.util.ArrayList;

//import Scheduler.SchedulerState;

/**
 * Manages and distributes commands between Floors and Elevators
 * @author user Ashwin Stoparczyk
 *
 */
public class Scheduler extends Thread {
	public enum SchedulerState {Idle, Sorting};
	DatagramPacket sendElevCommandPkt, recevFloorCommandPkt, sendFloorConfirm,receiveElevUpdate,sendElevReply;
	DatagramSocket sendReceiveSocket, receiveSocket,sendFloorReply,receiveElevInfo,sendFloorUpdate;
	private ElevatorCommands commands; //Shared command list
	private CommandData currentCommand; //Currently-managed command
	private CommandData recevCommand;
	private ArrayList<Elevator> elevatorList;
	private ArrayList<Floor> floorList;
	
	private SchedulerState schedulerState;

	private SendReceiveElevator sendThread;

	private int elevNum;

	private int portNum;
	public static final int WELL_KNOWN_PORT = 23;

	/**
	 * Constructor
	 * @param commands List of elevator commands that the Scheduler will manage
	 */
	public Scheduler(int portNum,int elevNum) {
		schedulerState = SchedulerState.Idle;
		this.portNum = portNum;
		this.elevatorList = new ArrayList<Elevator>();
		this.floorList = new ArrayList<Floor>();
		this.elevNum = elevNum;
		try {
			// Construct a datagram socket and bind it to any available
			// port on the local host machine. This socket will be used to
			// send UDP Datagram packets.
			sendReceiveSocket = new DatagramSocket();

			receiveElevInfo = new DatagramSocket(55);
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

	private void recevElevInfo(){

		byte[] data = new byte[5000];
		DatagramPacket receiveActiveElev = new DatagramPacket(data, data.length);
		System.out.println("Scheduler: Waiting for Elevator Info.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			receiveElevInfo.receive(receiveActiveElev);
			ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
			Object o = is.readObject();
			is.close();
			elevatorList.add((Elevator) o);

		} catch (IOException | ClassNotFoundException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Scheduler: Received Info.\n");

	}

	/**
	 * Sorts through pending commands and delegates to the proper elevator
	 * No sorting algorithm yet
	 */
	private void sortCommands() {
			//Update state
			if (schedulerState == SchedulerState.Sorting) {				
			
				System.out.println("Server received command and sorting!");
				//currentCommand = commands.getCommand(0); //Selects next command to be moved
	
				//Decide if command is valid needs to be refined
				if (!(currentCommand.getDir().equals("up") || currentCommand.getDir().equals("down") || currentCommand.getDest().equals("floor") || currentCommand.getDest().equals("server") || currentCommand.getDest().equals("elevator") ||
					currentCommand.getSource().equals("floor") || currentCommand.getSource().equals("server") || currentCommand.getSource().equals("elevator")) || currentCommand.getDest().equals(currentCommand.getSource()) ||
					currentCommand.getStartFloor() > elevatorList.size() || currentCommand.getDestFloor() > elevatorList.size()) {
					System.out.println("Command invalid. Removing");
					currentCommand = null;
				}
	
				//Determine best elevator to send command to
				Elevator closestElevator = determineClosestElevator();
	
				//Send command
				if (currentCommand.getDest().equals("elevator") ) {
					sendCommandElevator(closestElevator);
				}
	
				//Update state
				schedulerState = SchedulerState.Idle;
				//commands.notifyAll();
			//}
			} else {System.out.println("Scheduler not ready to sort yet");}
	}

	/**
	 * Scheduler sends a command to either an Elevator
	 * @param elevator The elevator the command is sent to
	 */
	public void sendCommandElevator(Elevator elevator) {
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
			System.out.println( "Scheduler: Sending command to elevator");
	
			// Send the datagram packet to the server via the socket.
			try {
				sendReceiveSocket.send(sendElevCommandPkt);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
	
			System.out.println("Scheduler: Command sent to elevator\n");
			schedulerState = SchedulerState.Sorting;
	
			} else {System.out.println("Scheduler is still sorting, cannot accept command immediately");}
	}

	/**
	 * Scheduler sends a command to a Floor
	 */
	public void recevUpdateFloor() {
		byte[] data = new byte[5000];
		DatagramPacket receiveUpdateRequest = new DatagramPacket(data, data.length);
		System.out.println("Scheduler: Waiting for Update Request.\n");

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
		System.out.println("Scheduler: Received Update Request.\n");
	}
	
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
		System.out.println( "Scheduler: Sending update to floor");

		// Send the datagram packet to the server via the socket.
		try {
			sendFloorUpdate.send(sendReply);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Scheduler: Update sent to floor\n");
	}
	
	/**
	 * Scheduler sends a command to either an Elevator
	 * @param elevator The elevator the command is sent to
	 */
	public void sendCommandElevator(Elevator elevator, CommandData command) {
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
			System.out.println( "Scheduler: Sending command to elevator");
	
			// Send the datagram packet to the server via the socket.
			try {
				sendReceiveSocket.send(sendElevCommandPkt);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
	
			System.out.println("Scheduler: Command sent to elevator\n");
			schedulerState = SchedulerState.Sorting;
	
			} else {System.out.println("Scheduler is still sorting, cannot accept command immediately");}
	}

	
	
	/**
	 * Determines the Elevator closest to the current command's destination
	 * Algorithm:
	 * Scheduler first searches for any elevators that are moving towards the destination floor. If at least one exists, only they are considered. Otherwise, all are considered
	 * Scheduler then determines the elevator that will take the shortest amount of time to reach the destination floor i.e smallest gap in floor difference
	 *
	 * @return closestElevator The Elevator which is closest to the destination floor
	 */
	public Elevator determineClosestElevator(){
		ArrayList<Elevator> consideredElevators = null;
		for (Elevator el : elevatorList){
			CommandData compCommand = el.getCurrentCommand(); //If empty ignore tba later once elevators implementation is finalized
			if ((compCommand.getDestFloor() < currentCommand.getDestFloor() && compCommand.getDir().equals("down")) ||
					(compCommand.getDestFloor() > currentCommand.getDestFloor() && compCommand.getDir().equals("up"))){
				consideredElevators.add(el);
			}
		}

		//If no elevators currently moving towards destination floor, consider all elevators
		if (consideredElevators.isEmpty()) consideredElevators = elevatorList;
		Elevator closestElevator = elevatorList.get(0);
		int closest = Math.abs(closestElevator.getDestFloor() - currentCommand.getDestFloor());

		//Iterate through considered elevators and choose the one closest to the next floor
		for (Elevator el : consideredElevators){
			if (Math.abs(el.getDestFloor() - closestElevator.getDestFloor()) < closest) closestElevator = el;
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
		try {
			receiveSocket = new DatagramSocket(23);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Scheduler: Waiting for Command.\n");

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
		System.out.println("Scheduler: Received Commands.\n");

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
		System.out.println( "Scheduler: Sending Confirmation to floor:");

		// Send the datagram packet to the server via the socket.
		try {
			sendFloorReply.send(sendFloorConfirm);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Scheduler: Confirmation sent to floor\n");
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
		System.out.println("Scheduler: Waiting for Elevator Update.\n");
		int index = 0;

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			sendReceiveSocket.receive(receiveElevUpdate);
			ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
			Object o = is.readObject();
			is.close();
			Elevator tempElevator = (Elevator) o;
			for (Elevator elevator : elevatorList){
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
		System.out.println("Scheduler: Received Update.\n");

		String reply = "Received Update!";
		byte[] sendMsg = reply.getBytes();
		sendElevReply = new DatagramPacket(sendMsg, sendMsg.length,
				receiveElevUpdate.getAddress(), receiveElevUpdate.getPort());

		//Print out content of the message host is sending
		System.out.println( "Scheduler: Sending reply to Elevator:");

		// Send the datagram packet to the server via the socket.
		try {
			sendReceiveSocket.send(sendElevReply);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Scheduler: Reply sent to Elevator\n");

	}
	
	public CommandData getCurrentCommand() {
		return currentCommand;
	}

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
	public static void main(String[] args) {

		Scheduler scheduler = new Scheduler(WELL_KNOWN_PORT,4);
		scheduler.start();
	}
	}
