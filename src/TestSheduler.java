import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * JUnit tests for the Scheduler class.
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
        Thread floor1 = new Floor(commands, 40);
        Thread floor2 = new Floor(commands, 41);
        Thread scheduler = new Scheduler(commands);
        Thread elevator1 = new Elevator(commands, 50);
        Thread elevator2 = new Elevator(commands, 51);

        //Start all treads
        floor1.start();
        floor2.start();
        scheduler1.start();
        elevator1.start();
        elevator2.start();
    }

    @Test
    /**
     * Tests scheduler with a test input file (TODO)
     * All contained methods are private but are all present and produce their own outputs when used
     */
    public void testScheduler(){
        floor1.createCommand(1, 2, System.currentTimeMillis(), "up");
        delayFor(10000);
        floor2.createCommand(2, 1, System.currentTimeMillis(), "down");
        delayFor(10000);
        floor1.createCommand(1, 1, System.currentTimeMillis(), "up");
        delayFor(1000);
        floor1.createCommand(1, 1, System.currentTimeMillis(), "jkhmn");
        delayFor(1000);
        floor1.createCommand(13, 1, System.currentTimeMillis(), "down");
        delayFor(1000);
        floor1.createCommand(1, 11, System.currentTimeMillis(), "up");

        //Expected output is two working commands, with all outputs, then a bunch of failed commands (to and from same floor, impossible name for direction, etc.)
        //Will also contain outputs from UDP passing in private methods
    }
}