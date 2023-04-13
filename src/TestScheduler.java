import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * JUnit tests for the Scheduler class.
 * @author Ashwin Stoparczyk
 *
 */

class TestScheduler {

    @Test
    /**
     * Tests scheduler with a test input file 
     * All contained methods are private but are all present and produce their own outputs when used
     */
    void testScheduler(){
        //Create Floor, Elevator, and Scheduler threads
        Thread floor1 = new Floor(40);
        Thread floor2 = new Floor(41);
        Thread scheduler = new Scheduler(80,40);
        Thread elevator1 = new Elevator(50, 52);
        Thread elevator2 = new Elevator(51, 53);

        //Start all threads
        floor1.start();
        floor2.start();
        scheduler.start();
        elevator1.start();
        elevator2.start();
       
        ((Floor) floor1).readFile("schedulerTestCommands.csv");

        //Expected output is two working commands, with all outputs, then a bunch of failed commands (to and from same floor, impossible name for direction, etc.)
        //Will also contain outputs from UDP passing in private methods
    }
}