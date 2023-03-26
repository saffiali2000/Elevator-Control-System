import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * Elevator that will consumer information from the command list to eventually move 
 * @author Walid Baitul Islam
 *
 */


public class Elevator extends Thread{
	DatagramPacket requestCommandPkt, receiveReplyPkt,sendInfoPkt,recevCommandPkt, sendUpdatePkt;
	DatagramSocket receiveCommandSocket,sendInfoSocket,senUpdateSocket;
	private int portNum;
	private CommandData currentCommand; //Currently-executing commands. Will later be a list of commands
	private ElevatorSubsystem subsystem;

	private ArrayList<String> errors;


	/**
	 * Constructor
	 *
	 * @param commands ArrayList<CommandData> list of information that will be consumed
	 */
	public Elevator(int port) {
		this.portNum = port;
		currentCommand = null;
		try {
			// Construct a datagram socket and bind it to port 69
			// on the local host machine. This socket will be used to
			// receive UDP Datagram packets.
			receiveCommandSocket = new DatagramSocket(port);
		} catch (SocketException se) {   // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
		this.subsystem = new ElevatorSubsystem();
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

	public void triggerErrors() {
		for (String error : errors) {
			if (error == "doorstuck") {
				int exit = subsystem.doorError();
				if (exit != 1) {
					//remove elevator
				} else if (error == "elevatorStuck") {
					//int exit = subsystem.elevatorStuck();
					//remove elevator
					// transfer floors to another elevator;
				}
			}
		}
	}

	public void notifyExists() {
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			os.flush();
			os.writeObject(this);
			os.flush();

			//retrieves byte array
			byte[] sendMsg = byteStream.toByteArray();
			sendInfoPkt = new DatagramPacket(sendMsg, sendMsg.length,
					InetAddress.getLocalHost(), 55);
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
			sendInfoSocket.send(sendInfoPkt);
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
		try {
			InetAddress serverAddress = InetAddress.getLocalHost();

			byte[] reqMsg = "requesting command".getBytes();
			requestCommandPkt = new DatagramPacket(reqMsg, reqMsg.length,
					serverAddress, portNum);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Print out packet content
		System.out.println("Elevator: Sending request to scheduler:");

		// Send the datagram packet to the server via the send/receive socket.
		try {
			receiveCommandSocket.send(requestCommandPkt);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Elevator: Request sent to scheduler.\n");


		// Construct a DatagramPacket for receiving floor packets up
		// to 100 bytes long (the length of the byte array).
		byte[] data = new byte[5000];
		recevCommandPkt = new DatagramPacket(data, data.length);
		System.out.println("Elevator: Waiting for Command.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			receiveCommandSocket.receive(recevCommandPkt);
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

		System.out.println("Elevator: Received Command.\n");

		//change state here!!
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
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			os.flush();
			os.writeObject(this);
			os.flush();

			//retrieves byte array
			byte[] sendMsg = byteStream.toByteArray();
			sendUpdatePkt = new DatagramPacket(sendMsg, sendMsg.length,
					recevCommandPkt.getAddress(), requestCommandPkt.getPort());
			os.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		//Print out packet content
		System.out.println("Elevator: Sending Update to scheduler:");

		// Send the datagram packet to the server via the send/receive socket.
		try {
			senUpdateSocket.send(sendUpdatePkt);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Elevator: Update sent to scheduler.\n");

		byte[] data = new byte[5000];
		receiveReplyPkt = new DatagramPacket(data, data.length);
		System.out.println("Elevator: Waiting for Confirmation Reply .\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			senUpdateSocket.receive(receiveReplyPkt);
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

	public void readFile(){
		errors = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("errors.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				errors.add(values[0]);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	public static void main(String[] args) {
		Thread elevator1 = new Elevator(69);
		Thread elevator2 = new Elevator(70);
		Thread elevator3 = new Elevator(71);
		Thread elevator4 = new Elevator(72);
		elevator1.start();
		elevator2.start();
		elevator3.start();
		elevator4.start();
	}
}

