package msrewards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import msrewards.service.RewardsService;
import msuser.service.UserService;

@RestController
public class RewardsController {
	
	@Autowired
	private RewardsService rewardsService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(rewardsService.getUserRewards(userService.getUser(userName)));
    }
}
