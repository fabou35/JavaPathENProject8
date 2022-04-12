package tourGuide.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import gpsUtil.GpsUtil;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.configuration.TestModeConfiguration;
import tourGuide.helper.InternalTestHelper;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tripPricer.Provider;

@SpringBootTest
public class TestUserService {

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
	
	@Test
	public void getUserPreferences() {
		InternalTestHelper.setInternalUserNumber(1);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		UserService userService = new UserService(testModeConfiguration);
		User user = userService.getAllUsers().get(0);
		UserPreferences userPreferences = user.getUserPreferences();
		
		Map<String, Object> userPreferencesMap = userService.getUserPreferences(user);
		assertEquals(userPreferences.getTripDuration(), userPreferencesMap.get("tripDuration"));
	}
	
	@Test
	public void modifyUserPreferences() {
		InternalTestHelper.setInternalUserNumber(1);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		UserService userService = new UserService(testModeConfiguration);
		User user = userService.getAllUsers().get(0);
		UserPreferences userPreferences = user.getUserPreferences();
		UserPreferences newUserPreferences = new UserPreferences();
		newUserPreferences.setAttractionProximity(userPreferences.getAttractionProximity());
		newUserPreferences.setLowerPricePoint(userPreferences.getLowerPricePoint());
		newUserPreferences.setHighPricePoint(userPreferences.getHighPricePoint());
		newUserPreferences.setTripDuration(userPreferences.getTripDuration());
		newUserPreferences.setTicketQuantity(userPreferences.getTicketQuantity());
		newUserPreferences.setNumberOfAdults(userPreferences.getNumberOfAdults());
		newUserPreferences.setNumberOfChildren(userPreferences.getNumberOfChildren()+1);
		Map<String, Object> userPreferencesMap = userService.modifyUserPreferences(user.getUserName(), newUserPreferences);
		assertEquals(userPreferences.getNumberOfChildren()+1, userPreferencesMap.get("numberOfChildren"));
	}
}
