import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

/**
 * Unit tests for Elevator
 * @author Saffi Ali
 *
 */

class TestElevator {
    private Thread elevator;
    private Thread scheduler;
    private ElevatorCommands commands;

    private CommandData commandSent;

    DatagramPacket sendPacket;

    @BeforeEach
    public void setUp(){
        commands = new ElevatorCommands();

    }
    @AfterEach
    public void cleanUp(){
        elevator.interrupt();
        scheduler.interrupt();
    }

    @Test
    void testInitial() {
        scheduler = new Scheduler(commands,1);
        elevator = new Elevator(commands,2);
        elevator.start();
        scheduler.start();
        assertTrue(elevator.isAlive());
        assertTrue(scheduler.isAlive());
        assertEquals(0,commands.getSize());

    }

    @Test
    void testSendAndRecieve() {
        scheduler = new Scheduler(commands,3);
        elevator = new Elevator(commands,4);
        scheduler.start();
        elevator.start();
        sendCommand();
        //assertEquals(commandSent,scheduler.getElevatorReturn);
    }

    public void sendCommand(){
        DatagramSocket sendReceiveSocket = null;
        try {
            sendReceiveSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        commandSent = new CommandData("9:00",0,9,"up","floor","elevator");
        try {
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(5000);
            ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
            os.flush();
            os.writeObject(commandSent);
            os.flush();

            //retrieves byte array
            byte[] sendMsg = byteStream.toByteArray();
            sendPacket = new DatagramPacket(sendMsg, sendMsg.length,
                    InetAddress.getLocalHost(), 3);
            os.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Send the datagram packet to the server via the send/receive socket.
        try {
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
