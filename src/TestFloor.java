import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Floor
 * @author Saffi Ali
 *
 */

class TestFloor {
    private Thread floor;
    private Thread scheduler;
    private ElevatorCommands commands;

    @BeforeEach
    public void setUp(){
        commands = new ElevatorCommands();

    }
    @AfterEach
    public void cleanUp(){
        floor.interrupt();
        scheduler.interrupt();
    }

    @Test
    /**
	 * Tests suite initialization
	 */
    void testInitial() {
        scheduler = new Scheduler(0,5);
        floor = new Floor(0);
        floor.start();
        scheduler.start();
        assertTrue(floor.isAlive());
        assertTrue(scheduler.isAlive());
        assertEquals(0,commands.getSize());

    }

    @Test
    /**
	 * Tests command send and receive
	 */
    void testSendAndRecieve() {
        scheduler = new Scheduler(1,5);
        floor = new Floor(1);
        floor.start();
        scheduler.start();
        //CommandData command = new CommandData(1, 3, "09:30:00", "up");
       // floor.setCommandSent(command);
        //floor.sendAndReceive();
        //assertEquals(command, floor.getCommandConfirmed());
    }
    
    @Test
    /**
	 * Tests time string conversion to milliseconds
	 */
    void testConvertToMillis() {
    	assertEquals(((Floor) floor).convertToMillis("00.00.00.000"), 0);
    	assertEquals(((Floor) floor).convertToMillis("00.00.03.100"), 3100);
    }

}
