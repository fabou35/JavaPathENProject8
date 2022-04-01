package tourGuide.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import tourGuide.Configuration.TestModeConfiguration;
import tourGuide.user.User;
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
}
