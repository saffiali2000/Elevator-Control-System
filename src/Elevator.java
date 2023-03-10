import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
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
		
	private ElevatorCommands commands; //Shared commands list
	private CommandData currentCommand; //Currently-executing commands. Will later be a list of commands
	//private ArrayList<CommandData> elevatorList;
	//private ArrayList<CommandData> returnList;
	
	 
	/**
	 * Constructor
	 * 
	 * @param commands ArrayList<CommandData> list of information that will be consumed
	 */
	public Elevator(ElevatorCommands commands) {
		this.commands = commands;
		currentCommand = null;
		//this.elevatorList = commands.getElevatorList();
		//this.returnList = commands.getReturnElevatorList();
		try {
			// Construct a datagram socket and bind it to port 69
			// on the local host machine. This socket will be used to
			// receive UDP Datagram packets.
			receiveSocket = new DatagramSocket(69);
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

		// Construct a datagram socket and bind it to any available
		// port on the local host machine. This socket will be used to
		// send Datagram packets.
		try {
			sendSocket = new DatagramSocket();
		} catch (SocketException se) {   // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}

		// Construct a DatagramPacket for receiving server packets up
		// to 100 bytes long (the length of the byte array).
		byte[] data = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		System.out.println("Elevator: Waiting for Packet.\n");

		try {
			// Block until a datagram is received via sendReceiveSocket.
			System.out.println("Waiting...");
			receiveSocket.receive(receivePacket);
		} catch(IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		System.out.println("Elevator: Packet received from scheduler:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		int len = receivePacket.getLength();
		System.out.println("Length: " + len);
		System.out.println("Byte Array: ");
		System.out.print("String Form: ");

		// Form a String from the byte array.
		String received = new String(data,0,len);
		System.out.println(received+"\n");

		byte[] sendData;
		sendData = new byte[]{0, 3, 0, 1};

		// Prepare a DatagramPacket to send back to client via host
		sendPacket = new DatagramPacket(sendData, sendData.length,
				receivePacket.getAddress(), receivePacket.getPort());


		// Print out contents of packet
		System.out.println("Server: Sending packet to host:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		int len2 = sendPacket.getLength();
		System.out.println("Length: " + len2);
		System.out.print("String Form: ");
		System.out.println(new String(sendPacket.getData(),0,len2));

		// Send the datagram packet to the server via the send/receive socket.

		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Server: Packet sent to host.\n");

		//Close send socket when finished
		sendSocket.close();
	}

	public CommandData getCurrentCommand(){
		return currentCommand;
	}

	public void setCurrentCommand(CommandData currentCommand){
		this.currentCommand = currentCommand;
	}
}
