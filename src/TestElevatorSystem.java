import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the ElevatorSystem class.
 * @author Henry Lin
 *
 */

class TestElevatorSystem {

	/**
	 * Stall thread for given amount of time. Used to wait
	 * since threads may take some time to execute.
	 * @param millis Time in milliseconds
	 */
	private void delayFor(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	/**
	 * Tests that communications are being received.
	 * TODO: In Iteration 1, the program terminates after one round of messages.
	 * Future iterations may change this behavior, so this test should be changed
	 * too.
	 */
	void testCommunicationsReceived() {
		//List of all pending commands
		ElevatorCommands commands = new ElevatorCommands();
		
		//Create Floor, Elevator, and Scheduler threads
		Thread floor1 = new Floor(commands);
		Thread scheduler = new Scheduler(commands);
		Thread elevator1 = new Elevator(commands);
		
		assertTrue(commands.getElevatorSize() == 0);
		assertTrue(commands.getFloorSize() == 0);
		assertTrue(commands.getReturnESize() == 0);
		assertTrue(commands.getReturnFSize() == 0);
		
		floor1.start();
		delayFor(1000);
		assertTrue(commands.getFloorSize() > 0);
		// Floor created a command
		
		scheduler.start();
		delayFor(1000);
		assertTrue(commands.getFloorSize() == 0);
		assertTrue(commands.getElevatorSize() > 0);
		// Scheduler consumed the command
		
		elevator1.start();
		// Program should terminate after this, if the elevator received the message.
		
		delayFor(10000);
		fail("Timeout: Floor did not terminate program");
	}

}
