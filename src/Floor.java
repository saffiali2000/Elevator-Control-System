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

	DatagramPacket sendCommandPkt, receiveReplyPkt, requestUpdatePkt, recevUpdatePkt;
	DatagramSocket sendReceiveSocket;
	DatagramSocket sendRecevAck;
	private long startTime;
	private int portNum;
	private ArrayList<ArrayList<String>> fileCommands;
	private CommandData commandSent; //Original command created and sent to scheduler
	private SendReceiveThread sendThread;


	/**
	 * Constructor
	 * @param commands List of commands relevant to this floor
	 */
	public Floor(int port) {
		this.portNum = port;
		commandSent = null;
		try {
			// Construct a datagram socket and bind it to any available
			// port on the local host machine. This socket will be used to
			// send and receive UDP Datagram packets.
			sendReceiveSocket = new DatagramSocket();
			sendRecevAck = new DatagramSocket();
		} catch (SocketException se) {   // Can't create the socket.
			se.printStackTrace();
			System.exit(1);
		}
		readFile();
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
		startTime = System.currentTimeMillis();
		for (int i = 0; i < fileCommands.size(); i++) {
			String tempTime = ((String) fileCommands.get(i).get(0));
			int tempFloor = (Integer.parseInt((String)fileCommands.get(i).get(1)));
			int tempDest = (Integer.parseInt((String) fileCommands.get(i).get(2)));
			String tempDir = ((String) fileCommands.get(i).get(3));
			/*
			 * Thread.sleep() until the correct time before sending 
			 */
			createCommand(tempFloor, tempDest, tempTime,tempDir);
		}
		while(true){
			sendAndReceiveAck();
		}
	}

	/**
	 * Read file method
	 * Reads csv file for list of commands to send
	 */
	public void readFile(){
		fileCommands = new ArrayList<>();
		ArrayList<String> commandsRead = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader("commands.csv"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				for (int i= 0 ;i< values.length;i++) {
					commandsRead.add(values[i]);
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
	 * Read file method
	 * Overloaded for testing
	 * Reads given csv file for list of commands to send and prints commands out to console
	 */
	public void readFile(String fileName){
		fileCommands = new ArrayList<>();
		ArrayList<String> commandsRead = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				for (int i= 0 ;i< values.length;i++) {
					commandsRead.add(values[i]);
				}
				fileCommands.add(commandsRead);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		for (int i = 0; i < fileCommands.size(); i++) {
			String tempTime = ((String) fileCommands.get(i).get(0));
			int tempFloor = (Integer.parseInt((String)fileCommands.get(i).get(1)));
			int tempDest = (Integer.parseInt((String) fileCommands.get(i).get(2)));
			String tempDir = ((String) fileCommands.get(i).get(3));
			/*
			 * Thread.sleep() until the correct time before sending 
			 */
			System.out.println("Time: " + tempTime + " Start floor: " + tempFloor + " Dest floor: " + tempDest + " Dir: " + tempDir);
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
			Long currentCommandTime = convertToMillis(commandSent.getTime());
			
			while (currentCommandTime > System.currentTimeMillis()); //Wait until time for next command to be sent
			os.flush();
			os.writeObject(commandSent);
			os.flush();

			//retrieves byte array
			byte[] sendMsg = byteStream.toByteArray();
			sendCommandPkt = new DatagramPacket(sendMsg, sendMsg.length,
					InetAddress.getLocalHost(), portNum);
			os.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


		//Print out packet content
		System.out.println("Floor: Sending command to scheduler:");
		// Send the datagram packet to the server via the send/receive socket.
		try {
			sendRecevAck.send(sendCommandPkt);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Floor: Command sent to scheduler.\n");

		byte[] data = new byte[5000];
		receiveReplyPkt = new DatagramPacket(data, data.length);
		System.out.println("Floor: Waiting for Confirmation.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			sendReceiveSocket.receive(receiveReplyPkt);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Floor: Received Confirmation.\n");
	}

	public void sendAndReceiveAck() {
		byte[] sendMsg = "Requesting Update".getBytes();
		try {
			requestUpdatePkt= new DatagramPacket(sendMsg, sendMsg.length, InetAddress.getLocalHost(), 24);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}

		//Print out packet content
		System.out.println("Floor: Requesting For Elevator Update:");
		// Send the datagram packet to the server via the send/receive socket.
		try {
			sendRecevAck.send(requestUpdatePkt);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Floor: Request sent to Scheduler.\n");

		byte[] data = new byte[5000];
		recevUpdatePkt = new DatagramPacket(data, data.length);
		System.out.println("Floor: Waiting for Reply.\n");

		// Block until a datagram packet is received from receiveSocket.
		try {
			System.out.println("Waiting..."); // so we know we're waiting
			sendRecevAck.receive(recevUpdatePkt);
		} catch (IOException e) {
			System.out.print("IO Exception: likely:");
			System.out.println("Receive Socket Timed Out.\n" + e);
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("Floor: Received Updated. Elevator Responded Successfully!.\n");
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
				synchronized (Floor.this) {
					try {
						Floor.this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sendAndReceive();
					commandSent = null;
					notifyAll();
				}
			}
		}
	}
	
	/**
	 * Converts a string in format hh.mm.ss.mmm to a long for scheduling
	 * @param input Time field of the input command
	 * @return Long conversion of the time field of the input command (in milliseconds)
	 */
	public long convertToMillis(String input) {
		long time = 0;
		String[] values = input.split("[.]"); 
		time += (Long.parseLong(values[0]) * 3600000); //hours
		time += (Long.parseLong(values[1]) * 60000); //minutes
		time += (Long.parseLong(values[2]) * 1000); //seconds
		time += (Long.parseLong(values[3])); //milliseconds
		return time;
	}
	
	public static void main(String[] args) {
		Thread floor1 = new Floor(23);
		floor1.start();
	}
}