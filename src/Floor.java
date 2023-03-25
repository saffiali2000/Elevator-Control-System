import java.io.*;
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

	private int portNum;
	private ElevatorCommands commands; //Shared list of commands
	//public ArrayList<CommandData> floorList; //Log of all commands sent by this floor

	private ArrayList<ArrayList> fileCommands;
	private CommandData commandSent; //Original command created and sent to scheduler
	private CommandData commandConfirmed; //Command executed by elevator and returned by scheduler
	
	private SendReceiveThread sendThread;


	/**
	 * Constructor
	 * @param commands List of commands relevant to this floor
	 */
	public Floor(int port) {
		this.portNum = port;
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
		
		sendThread = new SendReceiveThread();
		sendThread.start();
	}


	/**
	 * @Override default run method
	 */
	/**
	 * @Override default run method
	 */
	public void run() {
		readFile();
		for (int i = 0; i < fileCommands.size(); i++) {
			String tempTime = ((String) fileCommands.get(i).get(0));
			int tempFloor = ((Integer) fileCommands.get(i).get(1));
			int tempDest = ((Integer) fileCommands.get(i).get(2));
			String tempDir = ((String) fileCommands.get(i).get(3));
			/*
			 * Thread.sleep() until the correct time before sending 
			 */
			createCommand(tempFloor, tempDest, tempTime,tempDir);
		}
	}

	/**
	 * Read file method
	 * Reads csv file for list of commands to send
	 */
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
	/**
	 * Receive and send method
	 * Sends and receives messages from and to elevator via the scheduler, waiting every time it has to receive
	 */
	public void sendAndReceive() {
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
			ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			os.flush();
			os.writeObject(commandSent);
			os.flush();

			//retrieves byte array
			byte[] sendMsg = byteStream.toByteArray();
			sendPacket = new DatagramPacket(sendMsg, sendMsg.length,
					InetAddress.getLocalHost(), portNum);
			os.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		//Print out packet content
		System.out.println("Floor: Sending packet to scheduler:");
		// Send the datagram packet to the server via the send/receive socket.
		try {
			sendReceiveSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Floor: Packet sent to scheduler.\n");

		byte[] data = new byte[5000];
		receivePacket = new DatagramPacket(data, data.length);
		System.out.println("Floor: Waiting for Packet.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			sendReceiveSocket.receive(receivePacket);
			ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
			ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
			Object o = is.readObject();
			is.close();
			commandConfirmed = (CommandData) o;

		} catch (IOException | ClassNotFoundException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Floor: Received Packet.\n");
	}

		/**
		 * Creates a floor-source command and sends it to Scheduler
		 * @param startFloor Starting floor of elevator
		 * @param destFloor Destination floor of elevator
		 * @param dir Up or down
		 */

	public void createCommand(int startFloor, int destFloor, String time, String dir) {
			CommandData command = new CommandData(time, startFloor, destFloor, dir, "floor", "elevator");
			synchronized (this) {
				commandSent = command;
				notifyAll();
			}
	}
	/**
	 * Gets the current port number for the socket
	 * @return portNum
	 */
	public int getPortNum(){ return portNum;}
	
	/**
	 * Thread for sending and receiving, does not block main Floor thread.
	 */
	private class SendReceiveThread extends Thread {
		@Override
		public void run() {
			while (commandSent == null) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sendAndReceive();
				synchronized (Floor.this) {
					commandSent = null;
					notifyAll();
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Thread floor1 = new Floor(23);
		floor1.start();
	}
}


