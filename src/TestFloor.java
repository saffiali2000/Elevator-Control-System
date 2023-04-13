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

    @Test
    /**
	 * Tests suite initialization
	 */
    void testInitial() {
        scheduler = new Scheduler(12346,2);
        floor = new Floor(12348);
        floor.start();
        scheduler.start();
        assertTrue(floor.isAlive());
        assertTrue(scheduler.isAlive());

    }
    
    @Test
    /**
	 * Tests time string conversion to milliseconds
	 */
    void testConvertToMillis() {
    	floor = new Floor(12345);
    	assertEquals(((Floor) floor).convertToMillis("00.00.00.000"), 0);
    	assertEquals(((Floor) floor).convertToMillis("00.00.03.100"), 3100);
    }

}
