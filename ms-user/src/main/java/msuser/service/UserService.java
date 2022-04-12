package msuser.service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import msuser.configuration.TestModeConfiguration;
import msuser.model.User;
import msuser.model.UserPreferences;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class UserService {
	private final TripPricer tripPricer = new TripPricer();
	private static final String tripPricerApiKey = "test-server-api-key";

	private TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
	
	public UserService(TestModeConfiguration testModeConfiguration) {
		this.testModeConfiguration = testModeConfiguration;
	}

	public User getUser(String userName) {
		return testModeConfiguration.getInternalUserMap().get(userName);
	}
	
	public List<User> getAllUsers() {
		return testModeConfiguration.getInternalUserMap().values().stream().collect(Collectors.toList());
	}
	
	public void addUser(User user) {
		if(!testModeConfiguration.getInternalUserMap().containsKey(user.getUserName())) {
			testModeConfiguration.getInternalUserMap().put(user.getUserName(), user);
		}
	}
	
	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(), 
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}
	
	/**
	 * retrieves user's preferences
	 * @param user : User whose preferences are to be retrieved
	 * @return Map of user's preferences
	 */
	public Map<String, Object> getUserPreferences(User user){
		Map<String, Object> userPreferencesMap = new TreeMap<String, Object>();
		UserPreferences userPreferences = user.getUserPreferences();
		userPreferencesMap.put("attractionProximity", userPreferences.getAttractionProximity());
		userPreferencesMap.put("lowerPricePoint", userPreferences.getLowerPricePoint());
		userPreferencesMap.put("highPricePoint", userPreferences.getHighPricePoint());
		userPreferencesMap.put("tripDuration", userPreferences.getTripDuration());
		userPreferencesMap.put("ticketQuantity", userPreferences.getTicketQuantity());
		userPreferencesMap.put("numberOfAdults", userPreferences.getNumberOfAdults());
		userPreferencesMap.put("numberOfChildren", userPreferences.getNumberOfChildren());

		return userPreferencesMap;
	}
	
	/**
	 * modifies user's preferences
	 * @param userName : username of the User whose preferences are to be modified
	 * @param userNewPreferences : modified preferences 
	 * @return
	 */
	public Map<String, Object> modifyUserPreferences(String userName, UserPreferences userNewPreferences){
		Map<String, Object> userPreferencesMap = new TreeMap<String, Object>();
		User user = getUser(userName);
		user.setUserPreferences(userNewPreferences);
		userPreferencesMap = getUserPreferences(user);
		return userPreferencesMap;
	}
}
