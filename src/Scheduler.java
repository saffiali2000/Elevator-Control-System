import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Manages and distributes commands between Floors and Elevators
 * @author user Ashwin Stoparczyk
 *
 */
public class Scheduler extends Thread {

	DatagramPacket sendElevatorPacket, receiveFloorPacket, sendFloorPacket,receiveElevator;
	DatagramSocket sendReceiveSocket, receiveSocket,sendFloorSocket;
	private ElevatorCommands commands; //Shared command list
	private CommandData currentCommand; //Currently-managed command
	private CommandData recevCommand;
	private ArrayList<Elevator> elevatorList;
	private ArrayList<Floor> floorList;

	/**
	 * Constructor
	 * @param commands List of elevator commands that the Scheduler will manage
	 */
	public Scheduler(ElevatorCommands commands) {
		this.commands = commands;
		this.elevatorList = new ArrayList<Elevator>();
		this.floorList = new ArrayList<Floor>();
		try {
			// Construct a datagram socket and bind it to any available
			// port on the local host machine. This socket will be used to
			// send UDP Datagram packets.
			sendReceiveSocket = new DatagramSocket();

			// Construct a datagram socket and bind it to port 23
			// on the local host machine. This socket will be used to
			// receive UDP Datagram packets.
			receiveSocket = new DatagramSocket(23);

		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	
	/**
	 * @Override default run method
	 */
	public void run() {
		while (true) {
			receiveFloor();
			sortCommands();
			receiveElevator();
			sortCommands();
		}
	}

	/**
	 * Sorts through pending commands and delegates to the proper elevator
	 * No sorting algorithm yet
	 */
	private void sortCommands() {
		/*
		synchronized (commands) {
			while (commands.getSize() < 1) { //Wait until commands list is populated
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}

		 */

			System.out.println("Server received command and sorting!");
			//currentCommand = commands.getCommand(0); //Selects next command to be moved

			//Decide if command is valid needs to be refined
			if (currentCommand.getDir() != "up" || currentCommand.getDir() != "down" || currentCommand.getDest() != "floor" || currentCommand.getDest() != "server" || currentCommand.getDest() != "elevator" ||
					currentCommand.getSource() != "floor" || currentCommand.getSource() != "server" || currentCommand.getSource() != "elevator") {
				System.out.println("Command invalid. Removing");
				currentCommand = null;
			}

			//Sort list here

			Elevator closestElevator = determineClosestElevator();

			if (currentCommand.getDest().equals("elevator") ){
				sendCommandElevator(closestElevator);
			} else {
				sendCommandFloor();
			}
			//commands.notifyAll();
		//}
	}

	/**
	 * Scheduler sends a command to either an Elevator
	 * Shell, needs to be updated with UDP
	 */
	private void sendCommandElevator(Elevator elevator) {
		/*
		synchronized (commands) {
			while (commands.getSize() > 10) { //Wait until commands list is not overflowing (temporary)
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}

			//Change command source to scheduler to mark that it has been processed and add it back to commands list
			//Command is already checked for validity in sortCommands()
			commands.addCommand(currentCommand.getTime(), currentCommand.getStartFloor(), currentCommand.getDestFloor(), currentCommand.getDir(), "scheduler", currentCommand.getDest());
			System.out.println("Server sent command to elevator!");
			commands.notifyAll();
			}

		 */

		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			os.flush();
			os.writeObject(currentCommand);
			os.flush();

			//retrieves byte array
			byte[] sendMsg = byteStream.toByteArray();
			sendElevatorPacket = new DatagramPacket(sendMsg, sendMsg.length,
					InetAddress.getLocalHost(), elevator.getPortNum());
			os.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		//Print out content of the message host is sending
		System.out.println( "Host: Sending packet to server:");
		System.out.println("To host: " + sendElevatorPacket.getAddress());
		System.out.println("Destination host port: " + sendElevatorPacket.getPort());
		int len = sendElevatorPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Byte Array: ");
		System.out.print("String Form: ");
		System.out.println(new String(sendElevatorPacket.getData(),0,len)+"\n");

		// Send the datagram packet to the server via the socket.
		try {
			sendReceiveSocket.send(sendElevatorPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Host: Packet sent to server\n");

		}

	/**
	 * Scheduler sends a command to a Floor
	 * Shell, needs to be updated with UDP
	 */
	private void sendCommandFloor() {
		/*
		synchronized (commands) {
			while (commands.getSize() > 10) { //Wait until commands list is not overflowing (temporary)
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}

			//Change command source to scheduler to mark that it has been processed and add it back to commands list
			//Command is already checked for validity in sortCommands()
			commands.addCommand(currentCommand.getTime(), currentCommand.getStartFloor(), currentCommand.getDestFloor(), currentCommand.getDir(), "scheduler", currentCommand.getDest());
			System.out.println("Server sent command to elevator!");
			commands.notifyAll();
		}

		 */

		try {
			sendFloorSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			os.flush();
			os.writeObject(recevCommand);
			os.flush();

			//retrieves byte array
			byte[] sendMsg = byteStream.toByteArray();
			sendFloorPacket = new DatagramPacket(sendMsg, sendMsg.length,
					receiveFloorPacket.getAddress(), receiveFloorPacket.getPort());
			os.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		//Print out content of the message host is sending
		System.out.println( "Host: Sending packet to server:");
		System.out.println("To host: " + sendFloorPacket.getAddress());
		System.out.println("Destination host port: " + sendFloorPacket.getPort());
		int len = sendFloorPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Byte Array: ");
		System.out.print("String Form: ");
		System.out.println(new String(sendFloorPacket.getData(),0,len)+"\n");

		// Send the datagram packet to the server via the socket.
		try {
			sendFloorSocket.send(sendFloorPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Host: Packet sent to server\n");

	}

	/**
	 * Determines the Elevator closest to the current command's destination
	 * Algorithm:
	 * Scheduler first searches for any elevators that are moving towards the destination floor. If at least one exists, only they are considered. Otherwise, all are considered
	 * Scheduler then determines the elevator that will take the shortest amount of time to reach the destination floor i.e smallest gap in floor difference
	 */
	private Elevator determineClosestElevator(){
		ArrayList<Elevator> consideredElevators;
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
		int closest = Math.abs(el.getDestFloor() - closestElevator.getDestFloor());

		//Iterate through considered elevators and choose the one closest to the next floor
		for (Elevator el : consideredElevators){
			if (Math.abs(el.getDestFloor() - closestElevator.getDestFloor()) < closest) closestElevator = el;
		}

		return closestElevator;
	}

	/**
	 * Receive and send method
	 * Sends and receives messages from and to client/server, waiting every time it has to receive
	 */
	public void receiveFloor()
	{
		// Construct a DatagramPacket for receiving floor packets up
		// to 100 bytes long (the length of the byte array).
		receiveSocket = new DatagramSocket();

		byte[] data = new byte[5000];
		receiveFloorPacket = new DatagramPacket(data, data.length);
		System.out.println("Scheduler: Waiting for Packet.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			receiveSocket.receive(receiveFloorPacket);
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
		System.out.println("Scheduler: Received Packet.\n");

	}

	public void receiveElevator()
	{
		// Construct a DatagramPacket for receiving floor packets up
		// to 100 bytes long (the length of the byte array).
		byte[] data = new byte[5000];
		receiveElevator = new DatagramPacket(data, data.length);
		System.out.println("Scheduler: Waiting for Packet.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			sendReceiveSocket.receive(receiveElevator);
			ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
			Object o = is.readObject();
			is.close();
			recevCommand = (CommandData) o;

		} catch (IOException | ClassNotFoundException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Scheduler: Received Packet.\n");

	}
	}
