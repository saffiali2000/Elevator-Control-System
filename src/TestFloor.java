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
    void testInitial() {
        scheduler = new Scheduler(commands,0);
        floor = new Floor(commands,0);
        floor.start();
        scheduler.start();
        assertTrue(floor.isAlive());
        assertTrue(scheduler.isAlive());
        assertEquals(0,commands.getSize());

    }

    @Test
    void testSendAndRecieve() {
        scheduler = new Scheduler(commands,0);
        floor = new Floor(commands,0);
        floor.start();
        scheduler.start();
        CommandData command = new CommandData(1, 3, "09:30:00", "up");
        floor.setCommandSent(command);
        floor.sendAndReceive();
        assertEquals(command, floor.getCommandConfirmed());
    }

}
