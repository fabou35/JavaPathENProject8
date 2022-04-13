package msgps;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import msuser.configuration.TestModeConfiguration;
import msuser.helper.InternalTestHelper;
import msgps.service.GpsService;
import msrewards.service.RewardsService;
import msuser.service.UserService;
import msuser.model.User;

@SpringBootTest
public class TestGpsPerformance {

	@Ignore
	@Test
	public void highVolumeTrackLocation() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		// Users should be incremented up to 100,000, and test finishes within 15 minutes
		InternalTestHelper.setInternalUserNumber(100);
		GpsService gpsService = new GpsService(gpsUtil, rewardsService);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		UserService userService = new UserService(testModeConfiguration);

		List<User> allUsers = new ArrayList<>();
		allUsers = userService.getAllUsers();
		
	    StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for(User user : allUsers) {
			gpsService.trackUserLocation(user);
		}
		stopWatch.stop();
		testModeConfiguration.tracker.stopTracking();

		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
}
