package msrewards.service;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import msgps.model.Gps;
import msgps.service.GpsService;
import msrewards.model.Rewards;
import rewardCentral.RewardCentral;
import msuser.configuration.TestModeConfiguration;
import msuser.helper.InternalTestHelper;
import msuser.model.User;
import msuser.model.UserReward;
import msuser.service.UserService;

@SpringBootTest
public class TestRewardsService {
	
	@Test
	public void userGetRewards() {
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		GpsUtil gpsUtil = new GpsUtil();
		Rewards rewards = new Rewards(gpsUtil, new RewardCentral());
		Gps gps = new Gps(gpsUtil, rewards);
		GpsService gpsService = new GpsService(gps);
		InternalTestHelper.setInternalUserNumber(0);
		
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		Attraction attraction = gpsUtil.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
		gpsService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
		}
		testModeConfiguration.tracker.stopTracking();
		assertTrue(userRewards.size() > 0);
	}
	
	@Test
	public void nearAllAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		Rewards rewards = new Rewards(gpsUtil, new RewardCentral());
		rewards.setProximityBuffer(Integer.MAX_VALUE);
		RewardsService rewardsService = new RewardsService(rewards);
		InternalTestHelper.setInternalUserNumber(1);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		UserService userService = new UserService(testModeConfiguration);
		
		rewardsService.calculateRewards(userService.getAllUsers().get(0));
		List<UserReward> userRewards = rewardsService.getUserRewards(userService.getAllUsers().get(0));
		
		try {
			TimeUnit.MILLISECONDS.sleep(1000);
		} catch (InterruptedException e) {
		}
		
		testModeConfiguration.tracker.stopTracking();

		assertEquals(gpsUtil.getAttractions().size(), userRewards.size());
	}
	
}
