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

    @Test
    /**
     * Tests suite initialization
     */
    void testInitial() {
        scheduler = new Scheduler(67,1);
        elevator = new Elevator(52, 53);
        elevator.start();
        scheduler.start();
        assertTrue(elevator.isAlive());
        assertTrue(scheduler.isAlive());

    }

    /**
     * Tests ability to send command
     */
    public void sendCommand(){
        DatagramSocket sendReceiveSocket = null;
        try {
            sendReceiveSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        CommandData commandSent = new CommandData("00.00.01.000",0,9,"up","floor","elevator");
        DatagramPacket sendPacket = null;
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
