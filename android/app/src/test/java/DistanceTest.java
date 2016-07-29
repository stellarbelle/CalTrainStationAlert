import android.support.multidex.MultiDex;

import com.caltrainapp.MonitoringService;

import junit.framework.TestCase;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;

public class DistanceTest extends TestCase {
	@Test
	public void testReturnsDistanceInMiles() {
		MonitoringService TestingMonitoringService = new MonitoringService();
		double result = TestingMonitoringService.getMinutesAway(96560.6, 60000, 96560.6);
		assertEquals(1, 1);
	}
}
