package msgps;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;
import msuser.configuration.TestModeConfiguration;
import msgps.model.Gps;
import msgps.service.GpsService;
import msrewards.model.Rewards;
import msrewards.service.RewardsService;
import msuser.service.UserService;

@Configuration
public class MsGpsModule {

	@Bean
	public GpsUtil getGpsUtil() {
		return new GpsUtil();
	}
	
	@Bean
	public Rewards getRewards() {
		return new Rewards(getGpsUtil(), getRewardCentral());
	}
	
	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getRewards());
	}
	
	@Bean
	public RewardCentral getRewardCentral() {
		return new RewardCentral();
	}
	
	@Bean
	public Gps getGps() {
		return new Gps(getGpsUtil(), getRewards());
	}
	
	@Bean
	public GpsService getGpsService() {
		return new GpsService(getGps());
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
