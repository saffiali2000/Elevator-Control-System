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
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;

	private int portNum;
		
	private ElevatorCommands commands; //Shared commands list
	private CommandData currentCommand; //Currently-executing commands. Will later be a list of commands
	//private ArrayList<CommandData> elevatorList;
	//private ArrayList<CommandData> returnList;
	
	 
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
	}
	
	/**
	 * @Override default run method
	 */
	public void run() {
		sendAndReceive();
		//while (true) {
		//	waitForCommand();
		//}
	}

	
	/**
	 * Elevator waits for a command to be processed by the Scheduler, then executes it
	 */
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


	/**
	 * Elevator sends response back to Scheduler to confirm its previous command was executed properly
	 */

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

	/**
	 * Receive and send method
	 * Sends and receives messages from and to client via the host, waiting every time it has to receive
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

		currentCommand.setSource("elevator"); // Implement this with state changes
		currentCommand.setDest("floor"); // Implement this with state changes

		//Send packet response to scheduler
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			os.flush();
			os.writeObject(currentCommand);
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
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		int len = sendPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("String Form: ");
		System.out.println(new String(sendPacket.getData(), 0, len));

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

	public CommandData getCurrentCommand(){
		return currentCommand;
	}

	public void setCurrentCommand(CommandData currentCommand){
		this.currentCommand = currentCommand;
	}

	public int getPortNum(){return portNum;}
}
