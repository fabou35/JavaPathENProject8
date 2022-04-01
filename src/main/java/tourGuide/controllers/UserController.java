package tourGuide.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import tourGuide.service.UserService;
import tripPricer.Provider;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = userService.getTripDeals(userService.getUser(userName));
    	return JsonStream.serialize(providers);
    }
}
