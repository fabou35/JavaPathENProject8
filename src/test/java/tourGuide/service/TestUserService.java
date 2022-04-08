package tourGuide.service;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import tourGuide.Configuration.TestModeConfiguration;
import tourGuide.helper.InternalTestHelper;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;

@SpringBootTest
public class TestUserService {

	@Test
	public void getUserPreferences() {
		InternalTestHelper.setInternalUserNumber(1);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		UserService userService = new UserService(testModeConfiguration);
		User user = userService.getAllUsers().get(0);
		UserPreferences userPreferences = user.getUserPreferences();
		
		Map<String, Object> userPreferencesMap = userService.getUserPreferences(user);
		assertEquals(userPreferencesMap.get("tripDuration"), userPreferences.getTripDuration());
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
		assertEquals(userPreferencesMap.get("numberOfChildren"), userPreferences.getNumberOfChildren()+1);
	}
}
