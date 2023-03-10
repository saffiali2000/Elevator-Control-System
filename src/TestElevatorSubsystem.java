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
		subsystem = new ElevatorSubsystem();
	}
	
	@Test
	void testInitialState() {
		assertEquals(ElevatorSubsystem.ElevatorState.Idle, subsystem.getState());
		assertEquals(1, subsystem.getDestination());
	}

	@Test
	void testHandleButtonPressed() {
		subsystem.handleButtonPressed();
		assertEquals(ElevatorSubsystem.ElevatorState.Open, subsystem.getState());
	}

	@Test
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
		assertEquals(2, subsystem.getCurrentFloor());
		subsystem.handleArrived();
		assertEquals(ElevatorSubsystem.ElevatorState.Open, subsystem.getState());
		subsystem.setDestination(1);
		subsystem.handleDoorClosed();
		assertEquals(ElevatorSubsystem.ElevatorState.MovingDown, subsystem.getState());
	}

	@Test
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
