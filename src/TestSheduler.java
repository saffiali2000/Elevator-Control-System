import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Scheduler class.
 * @author Ashwin Stoparczyk
 *
 */

class TestScheduler {

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

    @BeforeEach
    public void init(){
        //List of all pending commands
        ElevatorCommands commands = new ElevatorCommands();

        //Create Floor, Elevator, and Scheduler threads
        Thread floor1 = new Floor(commands);
        Thread floor2 = new Floor(commands);
        Thread scheduler = new Scheduler(commands);
        Thread elevator1 = new Elevator(commands);
        Thread elevator2 = new Elevator(commands);

        floor1.start();
        floor2.start();
        scheduler1.start();
        elevator1.start();
        elevator2.start();
    }

    @Test
    /**
     * Tests sortCommands
     */
    public void testSortCommands(){
        floor1.createCommand(1, 2, "up");
        delayFor(5000);
        floor2.createCommand(2, 1, "down");
        delayFor(5000);
        floor1.createCommand(1, 1, "up");
        delayFor(1000);
        floor1.createCommand(1, 1, "jkhmn");
        delayFor(1000);
        floor1.createCommand(13, 1, "down");
        delayFor(1000);
        floor1.createCommand(1, 11, "up");

        //Expected output is two correct commands, with all outputs, then a bunch of failed commands (to and from same floor, impossible name for direction, etc.)
    }

    @Test
    /**
     * Tests sendCommandElevator
     * TODO, needs UDP first
     */
    public void testSendCommandElevator(){

    }

    @Test
    /**
     * Tests sendCommandFloor
     * TODO, needs UDP first
     */
    public void testSendCommandFloor(){

    }

    @Test
    /**
     * Tests determineClosestElevator
     */
    public void testDetermineClosestElevator(){
        //Place both elevators on floor 2, then move elevator 2 towards floor 1 before
        floor1.createCommand(1, 2, "up");
        assertEquals(scheduler.determineClosestElevator(), elevator2);
    }
}