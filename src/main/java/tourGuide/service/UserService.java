package tourGuide.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import tourGuide.Configuration.TestModeConfiguration;
import tourGuide.user.User;

@Service
public class UserService {
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
}
