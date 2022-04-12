package msrewards.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import msuser.service.UserService;
import rewardCentral.RewardCentral;
import msuser.configuration.TestModeConfiguration;

@Configuration
public class RewardsModule {

	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}
	
	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}
	
	@Bean
	public TestModeConfiguration getTestModeConfiguration() {
		return new TestModeConfiguration();
	}
	
	@Bean
	public UserService getUserService() {
		return new UserService(getTestModeConfiguration());
	}
}
