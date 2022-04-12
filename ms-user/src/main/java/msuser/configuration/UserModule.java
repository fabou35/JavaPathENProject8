package msuser.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import msuser.service.UserService;

@Configuration
public class UserModule {

	@Bean
	public TestModeConfiguration getTestModeConfiguration() {
		return new TestModeConfiguration();
	}
	
	@Bean
	public UserService getUserService() {
		return new UserService(getTestModeConfiguration());
	}
}
