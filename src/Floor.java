import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Floor that can create commands for elevators
 * @author Saffi Ali
 *
 */
public class Floor extends Thread {

	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceiveSocket;
	private ElevatorCommands commands; //Shared list of commands
	//public ArrayList<CommandData> floorList; //Log of all commands sent by this floor

	private ArrayList<ArrayList> fileCommands;
	private CommandData commandSent; //Original command created and sent to scheduler
	private CommandData commandConfirmed; //COmmand executed by elevator and returned by scheduler

	/**
	 * Constructor
	 * @param commands List of commands relevant to this floor
	 */
	public Floor(ElevatorCommands commands) {
		this.commands = commands;
		//this.floorList = new ArrayList<CommandData>();
		commandSent = null;

		try {
			// Construct a datagram socket and bind it to any available
			// port on the local host machine. This socket will be used to
			// send and receive UDP Datagram packets.
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {   // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
	}


	/**
	 * @Override default run method
	 */
	/**
	 * @Override default run method
	 */
	public void run() {
		readFile();
		int tempFloor = ((Integer) fileCommands.get(0).get(0));
		int tempDest = ((Integer) fileCommands.get(0).get(1));
		String tempDir = ((String) fileCommands.get(0).get(2));
		sendAndReceive(tempFloor,tempDest,tempDir);
		//createCommand(tempFloor, tempDest, tempDir); //Read from input file here
		//while (true) {
		//	waitForCommand();
		//}
	}

	public void readFile(){
		fileCommands = new ArrayList<>();
		ArrayList commandsRead = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("commands.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				for (int i= 0 ;i< values.length;i++) {
					commandsRead.add(values);
				}
				fileCommands.add(commandsRead);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	public void sendAndReceive(int startFloor, int destFloor, String dir) {
		//Create array of bytes for  read message
		int capacity = 100;

		ByteBuffer buffer = ByteBuffer.allocate(capacity);
		buffer.putInt(startFloor);
		buffer.putInt(destFloor);
		buffer.put(dir.getBytes());

		byte[] sendMsg = buffer.array();

		// Prepare a DatagramPacket and send it via sendReceiveSocket
		// to port on the destination host.
		try {
			sendPacket = new DatagramPacket(sendMsg, sendMsg.length,
					InetAddress.getLocalHost(), 23);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//Print out packet content
		System.out.println("Floor: Sending packet to scheduler:");
		System.out.println("To host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		int len = sendPacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Byte Array: ");
		System.out.print("String Form: ");
		System.out.println(new String(sendPacket.getData(), 0, len));

		// Send the datagram packet to the server via the send/receive socket.
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Floor: Packet sent to scheduler.\n");

		// Construct a DatagramPacket for receiving packets up
		// to 100 bytes long (the length of the byte array).
		byte[] data = new byte[100];
		receivePacket = new DatagramPacket(data, data.length);
		System.out.println("Client: Waiting for Packet.\n");

		try {
			// Block until a datagram is received via sendReceiveSocket.
			System.out.println("Waiting...");
			sendReceiveSocket.receive(receivePacket);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		// Process the received datagram.
		System.out.println("Floor: Packet received from scheduler:");
		System.out.println("From host: " + receivePacket.getAddress());
		System.out.println("Host port: " + receivePacket.getPort());
		len = receivePacket.getLength();
		System.out.println("Length: " + len);
		System.out.print("Byte Array: ");
		// Form a String from the byte array.
		System.out.println("String Form:");
		String received = new String(data, 0, len);
		System.out.println(received + "\n");


		/**
		 * Creates a floor-source command and sends it to Scheduler
		 * @param startFloor Starting floor of elevator
		 * @param destFloor Destination floor of elevator
		 * @param dir Up or down
		 */
	/*
	public void createCommand(int startFloor, int destFloor, String dir) {
		synchronized (commands) {
			while (commands.getSize() > 0) { //Wait until commands list is empty
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			
			//Create a new command and send it to the Scheduler
			LocalTime time = LocalTime.now();
			CommandData command = new CommandData(time, startFloor, destFloor, dir, "floor", "scheduler");
			commandSent = command;
			commands.addCommand(command);
			System.out.println("Floor created command and sent to server!");
			commands.notifyAll();
		}
	}

	 */

		/**
		 * Floor waits for its command to be returned to itself by Scheduler, to confirm the command was executed properly
		 */
	/*
	private void waitForCommand() {
		synchronized (commands) {
			while (commands.getElevatorSize() == 0) { //Wait until commands list is populated
				try {
					wait();
				} catch (InterruptedException e) {
					return;
				}

				boolean validCommand = false; //Returning command recieved

				//Iterate through commands to check for a command going from scheduler to this floor, which must be a returning command
				for (CommandData cd : commands){
					if (cd.getSource().equals("scheduler") && cd.getDest().equals("floor")){
						commandConfirmed = commands.getFloorReturn(0);
						validCommand = true;
					}
				}

				//If floor found a returning command compare it to the one it sent previously
				if (validCommand) {
					//Check if the returned command is the same as the original command
					if (commandSent == commandConfirmed) {
						System.out.println("Floor received command back!");
						//floorList.add(commandSent);
					} else {System.out.println("Floor received incorrect command back.");}

					commands.notifyAll();
					System.exit(0);
				}
			}
		}
	 */
	}
}
