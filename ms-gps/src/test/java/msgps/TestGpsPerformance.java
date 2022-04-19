package msgps;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import msuser.configuration.TestModeConfiguration;
import msuser.helper.InternalTestHelper;
import msgps.model.Gps;
import msgps.service.GpsService;
import msrewards.model.Rewards;
import msuser.service.UserService;
import msuser.model.User;

@SpringBootTest
public class TestGpsPerformance {
	
	@Test
	public void highVolumeTrackLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		Rewards rewards = new Rewards(gpsUtil, new RewardCentral());
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(100000);
		Gps gps = new Gps(gpsUtil, rewards);
		GpsService gpsService = new GpsService(gps);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		UserService userService = new UserService(testModeConfiguration);

		List<User> allUsers = new ArrayList<>();
		allUsers = userService.getAllUsers();
		
	    StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for(User user : allUsers) {
			gpsService.trackUserLocation(user);
		}
		
		for(User user : allUsers) {
			while(user.getVisitedLocations().size() < 4) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}
		
		for(User user: allUsers) {
			VisitedLocation visitedLocation = user.getVisitedLocations().get(3);
			assertTrue(visitedLocation != null);
		}
		stopWatch.stop();
		testModeConfiguration.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
}
