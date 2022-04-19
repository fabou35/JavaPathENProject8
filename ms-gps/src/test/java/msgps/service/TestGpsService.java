package msgps.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import msgps.model.Gps;
import msrewards.model.Rewards;
import msuser.configuration.TestModeConfiguration;
import msuser.helper.InternalTestHelper;
import msuser.model.User;
import msuser.service.UserService;

@SpringBootTest
public class TestGpsService {

	@Test
	public void isWithinAttractionProximity() {
		GpsUtil gpsUtil = new GpsUtil();
		Rewards rewards = new Rewards(gpsUtil, new RewardCentral());
		Gps gps = new Gps(gpsUtil, rewards);
		GpsService gpsService = new GpsService(gps);
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(gpsService.isWithinAttractionProximity(attraction, attraction));
	}
	
	@Test
	public void getUserLocation() {
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		GpsUtil gpsUtil = new GpsUtil();
		Rewards rewards = new Rewards(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(0);
		Gps gps = new Gps(gpsUtil, rewards);
		GpsService gpsService = new GpsService(gps);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = gpsService.getUserLocation(user);
		testModeConfiguration.tracker.stopTracking();
		assertTrue(visitedLocation.userId.equals(user.getUserId()));
	}
	
	@Test
	public void getNearbyAttractions() {
		GpsUtil gpsUtil = new GpsUtil();
		Rewards rewards = new Rewards(gpsUtil, new RewardCentral());
		
		InternalTestHelper.setInternalUserNumber(0);
		Gps gps = new Gps(gpsUtil, rewards);
		GpsService gpsService = new GpsService(gps);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		VisitedLocation visitedLocation = gpsService.getUserLocation(user);
		
		List<Attraction> attractions = gpsService.getNearByAttractions(visitedLocation);
		
		testModeConfiguration.tracker.stopTracking();
		
		assertEquals(5, attractions.size());
	}
	
	@Test
	public void getNearByAttractionsListForDisplay() {
		GpsUtil gpsUtil = new GpsUtil();
		Rewards rewards = new Rewards(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(1);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		Gps gps = new Gps(gpsUtil, rewards);
		GpsService gpsService = new GpsService(gps);
		UserService userService = new UserService(testModeConfiguration);
		VisitedLocation visitedLocation = userService.getUser(userService.getAllUsers().get(0).getUserName())
				.getLastVisitedLocation();
		
		Map<String, Object> userLocation = new TreeMap<>();
		userLocation.put("userLatitude", visitedLocation.location.latitude);
		userLocation.put("userLongitude", visitedLocation.location.longitude);
		
		List<Map<String, Object>> nearByAttractionsList = gpsService.getNearByAttractionsForDisplay(
				visitedLocation);
		testModeConfiguration.tracker.stopTracking();
		
		assertEquals(6, nearByAttractionsList.size());
		assertThat(userLocation).isIn(nearByAttractionsList);
	}
	
	@Test
	public void getAllCurrentLocations() {
		GpsUtil gpsUtil = new GpsUtil();
		Rewards rewards = new Rewards(gpsUtil, new RewardCentral());
		InternalTestHelper.setInternalUserNumber(10);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		UserService userService = new UserService(testModeConfiguration);
		User user = userService.getAllUsers().get(0);
		Gps gps = new Gps(gpsUtil, rewards);
		GpsService gpsService = new GpsService(gps);
		List<Map<String, Object>> allCurrentLocations = new ArrayList<>();
		allCurrentLocations = gpsService.getAllCurrentLocations(userService.getAllUsers());
		Map<String, Object> currentLocation = new TreeMap<String, Object>();
		Map<String, Double> location = new TreeMap<String, Double>();
		location.put("longitude", user.getLastVisitedLocation().location.longitude);
		location.put("latitude", user.getLastVisitedLocation().location.latitude);
		currentLocation.put(user.getUserId().toString(), location);
		
		assertThat(currentLocation).isIn(allCurrentLocations);
		assertEquals(10, allCurrentLocations.size());
	}
}
