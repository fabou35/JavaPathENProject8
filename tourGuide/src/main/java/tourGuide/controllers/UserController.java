package tourGuide.controllers;

import java.util.List;
import java.util.Map;

import javax.money.Monetary;

import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import tourGuide.service.UserService;
import tourGuide.user.UserPreferences;
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
	
	@RequestMapping("/getUserPreferences")
	public String getUserPreferences(@RequestParam String userName) {
		Map<String, Object> userPreferences = userService.getUserPreferences(userService.getUser(userName));
		return JsonStream.serialize(userPreferences.toString());
	}
	
	@RequestMapping("/postUserNewPreferences")
	public String postUserNewPreferences(@RequestParam String userName, @RequestParam int attractionProximity, 
			@RequestParam int lowerPricePoint, @RequestParam int highPricePoint, @RequestParam int tripDuration,
			@RequestParam int ticketQuantity, @RequestParam int numberOfAdults, @RequestParam int numberOfChildren) {
			
		UserPreferences newUserPreferences = new UserPreferences(attractionProximity, 
				Money.of(lowerPricePoint, Monetary.getCurrency("USD")), 
				Money.of(highPricePoint, Monetary.getCurrency("USD")), tripDuration, ticketQuantity, numberOfAdults, 
				numberOfChildren);
		
		Map<String, Object> userPreferences = userService.modifyUserPreferences(userName, newUserPreferences);
		return JsonStream.serialize(userPreferences.toString());
	}
}
