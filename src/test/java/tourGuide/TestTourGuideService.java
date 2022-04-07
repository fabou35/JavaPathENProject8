package tourGuide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.Configuration.TestModeConfiguration;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.GpsService;
import tourGuide.service.RewardsService;
import tourGuide.service.UserService;
import tourGuide.user.User;
import tripPricer.Provider;

@SpringBootTest
public class TestTourGuideService {
	
	@Test
	public void getUserLocation() {
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		GpsService gpsService = new GpsService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = gpsService.trackUserLocation(user);
		testModeConfiguration.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}
	
	@Test
	public void addUser() {
		InternalTestHelper.setInternalUserNumber(0);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		UserService userService = new UserService(testModeConfiguration);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);
		
		User retrivedUser = userService.getUser(user.getUserName());
		User retrivedUser2 = userService.getUser(user2.getUserName());

		testModeConfiguration.tracker.stopTracking();
		
		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}
	
	@Test
	public void getAllUsers() {
		InternalTestHelper.setInternalUserNumber(0);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		UserService userService = new UserService(testModeConfiguration);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		userService.addUser(user);
		userService.addUser(user2);
		
		List<User> allUsers = userService.getAllUsers();

		testModeConfiguration.tracker.stopTracking();
		
		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}
	
	@Test
	public void trackUser() {
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		GpsService gpsService = new GpsService(gpsUtil, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = gpsService.trackUserLocation(user);
		
		testModeConfiguration.tracker.stopTracking();
		
		assertEquals(user.getUserId(), visitedLocation.userId);
	}
	
	@Test
	public void getNearbyAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		
		InternalTestHelper.setInternalUserNumber(0);
		GpsService gpsService = new GpsService(gpsUtil, rewardsService);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = gpsService.trackUserLocation(user);
		
		List<Attraction> attractions = gpsService.getNearByAttractions(visitedLocation);
		
		testModeConfiguration.tracker.stopTracking();
		
		assertEquals(5, attractions.size());
	}
	
	@Test
	public void getNearByAttractionsListForDisplay() {
		GpsUtil gpsUtil = new GpsUtil();
		RewardsService rewardsService = new RewardsService(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(1);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		GpsService gpsService = new GpsService(gpsUtil, rewardsService);
		UserService userService = new UserService(testModeConfiguration);
		VisitedLocation visitedLocation = userService.getUser(userService.getAllUsers().get(0).getUserName()).getLastVisitedLocation();
		
		Map<String, Object> userLocationMap = new TreeMap<>();
		userLocationMap.put("userLatitude", visitedLocation.location.latitude);
		userLocationMap.put("userLongitude", visitedLocation.location.longitude);
		
		List<Map<String, Object>> nearByAttractionsList = gpsService.getNearByAttractionsForDisplay(visitedLocation);
		testModeConfiguration.tracker.stopTracking();
		
		assertEquals(6, nearByAttractionsList.size());
		assertThat(userLocationMap).isIn(nearByAttractionsList);
	}
	
	@Test
	public void getTripDeals() {
		InternalTestHelper.setInternalUserNumber(0);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		UserService userService = new UserService(testModeConfiguration);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		
		List<Provider> providers = userService.getTripDeals(user);
		providers.forEach(p -> System.out.println(p.name));
		testModeConfiguration.tracker.stopTracking();
		
		assertEquals(5, providers.size());
	}
	
	
}
