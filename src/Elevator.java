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
	DatagramPacket sendPacket, receivePacket,sendInfo;
	DatagramSocket sendSocket, receiveSocket,sendInfoSocket;

	private int portNum;

	private ElevatorCommands commands; //Shared commands list
	private CommandData currentCommand; //Currently-executing commands. Will later be a list of commands
	//private ArrayList<CommandData> elevatorList;
	//private ArrayList<CommandData> returnList;

	private ElevatorSubsystem subsystem;

	private ArrayList<String> errors;


	/**
	 * Constructor
	 *
	 * @param commands ArrayList<CommandData> list of information that will be consumed
	 */
	public Elevator(ElevatorCommands commands, int port) {
		this.portNum = port;
		this.commands = commands;
		currentCommand = null;
		//this.elevatorList = commands.getElevatorList();
		//this.returnList = commands.getReturnElevatorList();
		try {
			// Construct a datagram socket and bind it to port 69
			// on the local host machine. This socket will be used to
			// receive UDP Datagram packets.
			receiveSocket = new DatagramSocket(port);
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
		}
		//while (true) {
		//	waitForCommand();
		//}
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





	///**
	// * Elevator waits for a command to be processed by the Scheduler, then executes it
	// */
	/*
	private void waitForCommand() {
		synchronized (commands) {
			while (commands.getSize() < 1) { //Wait until commands list is populated
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}

			boolean validCommand = false; //Valid command recieved

			//Iterate through commands to check for a command going from scheduler to this elevator, which must be an actionable command
			for (CommandData cd : commands){
				if (cd.getSource().equals("scheduler") && cd.getDest().equals("elevator")){
					currentCommand = cd;
					validCommand = true;
				}
			}

			//If a valid command was recieved execute it and respond back
			if (validCommand) {
				System.out.println("Elevator received command responding back!");
				//Execute command here
				respondBack();
			} else { System.out.println("No elevator-bound command exists, continue waiting");}
			commands.notifyAll();
		}
	}

	 */


	///**
	//* Elevator sends response back to Scheduler to confirm its previous command was executed properly
	//*/

	/*
	private void respondBack(){
		synchronized (commands) {
			while (commands.getSize() > 10) { //Wait until commands list is not overflowing (temporary)
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			//Send executed command back to Scheduler
			commands.addCommand(currentCommand.getTime(), currentCommand.getStartFloor(), currentCommand.getDestFloor(), currentCommand.getDir(), "elevator", "scheduler");
			System.out.println("Elevator sent command back!");
		}
	}

	 */

	public void notifyExists() {
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			os.flush();
			os.writeObject(this);
			os.flush();

			//retrieves byte array
			byte[] sendMsg = byteStream.toByteArray();
			sendInfo = new DatagramPacket(sendMsg, sendMsg.length,
					receivePacket.getAddress(), 55);
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

		// Construct a DatagramPacket for receiving floor packets up
		// to 100 bytes long (the length of the byte array).
		byte[] data = new byte[5000];
		receivePacket = new DatagramPacket(data, data.length);
		System.out.println("Elevator: Waiting for Packet.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			receiveSocket.receive(receivePacket);
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

		System.out.println("Elevator: Received Packet.\n");

		//change state here!!
		subsystem.handleButtonPressed();
		subsystem.setDestination(currentCommand.getDestFloor());
		subsystem.handleDoorClosed();
		subsystem.handleArrived();

		//Send updated location to scheduler
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			os.flush();
			os.writeObject(this);
			os.flush();

			//retrieves byte array
			byte[] sendMsg = byteStream.toByteArray();
			sendPacket = new DatagramPacket(sendMsg, sendMsg.length,
					receivePacket.getAddress(), receivePacket.getPort());
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
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Elevator: Packet sent to scheduler.\n");
		sendSocket.close();
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
}
