package elromantico.com.car;

import static elromantico.com.car.SpeedCalculator.calculateSpeed;
import static org.junit.Assert.*;

import org.junit.Test;

public class SpeedCalculatorTest {

    @Test
    public void testSpeedCalculation(){
        long startTimestamp = System.currentTimeMillis();
        long endTimestamp = startTimestamp + 9 * 1000;

        double speed = calculateSpeed(new Location(42.656513, 23.345819, startTimestamp), new Location(42.656221, 23.344992, endTimestamp));

        assertEquals(30, speed, 0.01);
    }

}
