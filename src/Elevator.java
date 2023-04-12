import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * Elevator that will consumer information from the command list to eventually move 
 * @author Walid Baitul Islam
 *
 */


public class Elevator extends Thread {
	//public static final long serialVersionUID = 1;
	
	DatagramPacket sendInfo,sendRequest,receiveUpdate,sendUpdate; //UDP data packets
	DatagramSocket sendSocket, receiveSocket,sendInfoSocket; //UDP data sockets
	private int portNum; //This Elevator's port number
	private CommandData currentCommand; //Currently-executing commands. Will later be a list of commands
	private ElevatorSubsystem subsystem; //This Elevator's subsystem

	private ArrayList<String[]> errors; //List of errors which will affect this elevator
	private boolean isReady; //Denotes if the elevator is ready to receive a command


	/**
	 * Constructor
	 *
	 * @param port Port number that will be used for UDP data transfer
	 */
	public Elevator(int port) {
		this.portNum = port;
		currentCommand = null;
		this.setReady(false);
		try {
			// Construct a datagram socket and bind it to port 69
			// on the local host machine. This socket will be used to
			// receive UDP Datagram packets.
			receiveSocket = new DatagramSocket(portNum);
			sendInfoSocket = new DatagramSocket();
			sendSocket = new DatagramSocket();
		} catch (SocketException se) {   // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
		this.subsystem = new ElevatorSubsystem(this.portNum);
	}

	/**
	 * @Override default run method
	 */
	public void run() {
		notifyExists();
		readFile();
		while(true) {
			sendAndReceive();
			sendAck();
		}
	}

	/* if(error == "doorstuck")
	 * {
	 * 		int exit = subsystem.doorError();
	 * if(exit !=1)
	 * {
	 * 	//remove elevator
	 * }
	 *
	 * else if (error =="elevatorStuck")
	 * {
	 * 		int exit = subsystem.elevatorStuck();
	 * 		//remove elevator
	 * 		// transfer floors to another elevator;
	 *}
	 */

	/**
	 * Notifies the scheduler via RPC that the elevator was initialized along with information about the elevator
	 */
	public void notifyExists() {
		try {
			//Prepare information packet for sending
			setReady(true);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			os.flush();
			os.writeObject(this.subsystem);
			os.flush();
			
			InetAddress serverAddress = InetAddress.getLocalHost();

			//retrieves byte array
			byte[] sendMsg = byteStream.toByteArray();
			sendInfo = new DatagramPacket(sendMsg, sendMsg.length,
					serverAddress, 5507);
			os.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		//Print out packet content
		System.out.println("Elevator: Sending elevator info to scheduler:");

		// Send the datagram packet to the server via the send/receive socket.
		try {
			sendInfoSocket.send(sendInfo);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Elevator: Packet sent to scheduler.\n");
	}

	/**
	 * Receive and send method
	 * Sends and receives messages from and to floor via the scheduler, waiting every time it has to receive
	 */
	public void sendAndReceive() {
		//Create request packet information
		try {
			InetAddress serverAddress = InetAddress.getLocalHost();

			byte[] reqMsg = "requesting command".getBytes();
			sendRequest = new DatagramPacket(reqMsg, reqMsg.length,
					serverAddress, portNum);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Print out packet content
		System.out.println("Elevator: Sending request packet to scheduler:");

		// Send the datagram packet to the server via the send/receive socket.
		try {
			sendSocket.send(sendRequest);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Elevator: Packet sent to scheduler.\n");


		// Construct a DatagramPacket for receiving floor packets up
		// to 100 bytes long (the length of the byte array).
		byte[] tempData = new byte[5000];
		DatagramPacket receiveRequest = null;
		try {
			receiveRequest = new DatagramPacket(tempData, tempData.length, InetAddress.getLocalHost(),portNum);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Elevator: Waiting for Command.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			receiveSocket.connect(InetAddress.getLocalHost(),portNum);
			receiveSocket.receive(receiveRequest);
			ByteArrayInputStream byteStream = new ByteArrayInputStream(tempData);
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
			Object o = is.readObject();
			is.close();
			currentCommand = (CommandData) o;

		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		System.out.println("Elevator: Received Packet.\n");
		subsystem.setCommand(currentCommand);

		//Subsystem updates state
		subsystem.setDestination(currentCommand.getDestFloor());
		subsystem.handleButtonPressed();
	}
	/**
	 * Send Acknowledgement/Updated state
	 * Sends and receives messages from and to floor via the scheduler, waiting every time it has to receive
	 */
	public void sendAck() {

		//Send updated location to scheduler
		try {
			setReady(true);
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			os.flush();
			os.writeObject(this);
			os.flush();

			//retrieves byte array
			byte[] sendMsg = byteStream.toByteArray();
			sendUpdate = new DatagramPacket(sendMsg, sendMsg.length,
					InetAddress.getLocalHost(), portNum);
			os.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		//Print out packet content
		System.out.println("Elevator: Sending packet to scheduler:");

		// Send the datagram packet to the server via the send/receive socket.
		try {
			sendSocket.send(sendUpdate);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Elevator: Packet sent to scheduler.\n");

		byte[] data = new byte[5000];
		receiveUpdate = new DatagramPacket(data, data.length);
		System.out.println("Elevator: Waiting for Confirmation Reply .\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			receiveSocket.receive(receiveUpdate);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Elevator: Received Confirmation Reply.\n");
	}

	/**
	 * Gets the current Command elevator is processing
	 * @return currentCommand
	 */
	public CommandData getCurrentCommand(){
		return currentCommand;
	}

	/**
	 * Sets the current Command elevator is processing
	 */
	public void setCurrentCommand(CommandData currentCommand){
		this.currentCommand = currentCommand;
	}

	/**
	 * Gets the current port number for the socket
	 * @return portNum
	 */
	public int getPortNum(){return portNum;}

	/**
	 * Reads an input file containing errors for error handling and stores them in an ArrayList
	 */
	public void readFile(){
		errors = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("errors.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				errors.add(values);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	
	/**
	 * Returns the destination floor of the elevator's current command
	 * @return destFloor
	 */
	public int getDestFloor() {
		return currentCommand.getDestFloor();
	}
	
	/**
	 * Main runnable method
	 * @param args default
	 */
	public static void main(String[] args) {
		Thread elevator1 = new Elevator(150);
		//Thread elevator2 = new Elevator(5070);
		//Thread elevator3 = new Elevator(5071);
		//Thread elevator4 = new Elevator(5072);
		elevator1.start();
		//elevator2.start();
		//elevator3.start();
		//elevator4.start();
	}

	//Getters and setters
	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
}

