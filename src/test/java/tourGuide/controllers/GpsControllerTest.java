package tourGuide.controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import tourGuide.Configuration.TestModeConfiguration;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.UserService;
import tourGuide.user.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GpsControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@Ignore
	@Test
	public void test_getLocation() throws Exception {
		InternalTestHelper.setInternalUserNumber(1);
		TestModeConfiguration testModeConfiguration = new TestModeConfiguration();
		UserService userService = new UserService(testModeConfiguration);
		User user = userService.getAllUsers().get(0);
		
		mockMvc.perform(get("/getLocation")
				.param("userName", user.getUserName()))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("latitude")))
			.andExpect(content().string(containsString("longitude")));
		
	}
}
