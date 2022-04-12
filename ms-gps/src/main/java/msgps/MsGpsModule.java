package msgps;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import msuser.configuration.TestModeConfiguration;
import msgps.service.GpsService;
import msrewards.service.RewardsService;
import msuser.service.UserService;

@Configuration
public class MsGpsModule {

	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}
	
	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getGpsUtil(), getRewardCentral());
	}
	
	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}
	
	@Bean
	public GpsService getGpsService() {
		return new GpsService(getGpsUtil(), getRewardsService());
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
