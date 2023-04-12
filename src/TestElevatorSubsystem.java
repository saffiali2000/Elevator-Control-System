import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ElevatorSubsystem.
 * Note: tests will take some time to run because of the elevator moving.
 * @author Henry Lin
 *
 */

class TestElevatorSubsystem {
	private ElevatorSubsystem subsystem;

	@BeforeEach
	void setUp() throws Exception {
		subsystem = new ElevatorSubsystem(24);
	}
	
	@Test
	/**
	 * Tests suite initialization
	 */
	void testInitialState() {
		assertEquals(ElevatorSubsystem.ElevatorState.Idle, subsystem.getState());
		assertEquals(1, subsystem.getDestination());
	}

	@Test
	/**
	 * Tests subsystem reaction to button pressed stimulus
	 */
	void testHandleButtonPressed() {
		subsystem.handleButtonPressed();
		assertEquals(ElevatorSubsystem.ElevatorState.Open, subsystem.getState());
		subsystem.handleDoorClosed();
		assertEquals(ElevatorSubsystem.ElevatorState.Idle, subsystem.getState());
		subsystem.setDestination(2);
		subsystem.handleButtonPressed();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) { e.printStackTrace(); }
		assertEquals(ElevatorSubsystem.ElevatorState.MovingUp, subsystem.getState());
		subsystem.handleArrived();
		subsystem.setDestination(1);
		subsystem.handleDoorClosed();
		subsystem.handleButtonPressed();
		assertEquals(ElevatorSubsystem.ElevatorState.MovingDown, subsystem.getState());
	}

	@Test
	/**
	 * Tests subsystem reaction to door closed stimulus
	 */
	void testHandleDoorClosed() {
		subsystem.handleDoorClosed();
		assertEquals(ElevatorSubsystem.ElevatorState.Idle, subsystem.getState()); // No change
		subsystem.handleButtonPressed();
		subsystem.handleDoorClosed();
		assertEquals(ElevatorSubsystem.ElevatorState.Idle, subsystem.getState()); // Back to Idle
		subsystem.handleButtonPressed();
		subsystem.setDestination(2);
		subsystem.handleDoorClosed();
		assertEquals(ElevatorSubsystem.ElevatorState.MovingUp, subsystem.getState());
		try {
			Thread.sleep((long) (ElevatorSubsystem.timeToTravel(1, 2) * 1000 + 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(2, subsystem.getCurrentFloor());
		subsystem.handleArrived();
		assertEquals(ElevatorSubsystem.ElevatorState.Open, subsystem.getState());
		subsystem.setDestination(1);
		subsystem.handleDoorClosed();
		assertEquals(ElevatorSubsystem.ElevatorState.MovingDown, subsystem.getState());
	}

	@Test
	/**
	 * Tests subsystem reaction to elevator arrived stimulus
	 */
	void testHandleArrived() {
		subsystem.handleArrived();
		assertEquals(ElevatorSubsystem.ElevatorState.Idle, subsystem.getState());
		subsystem.handleButtonPressed();
		subsystem.handleArrived();
		assertEquals(ElevatorSubsystem.ElevatorState.Open, subsystem.getState()); // No change
		subsystem.setDestination(2);
		subsystem.handleDoorClosed();
		subsystem.handleArrived();
		assertEquals(ElevatorSubsystem.ElevatorState.Open, subsystem.getState()); // Back to Open
	}

}
