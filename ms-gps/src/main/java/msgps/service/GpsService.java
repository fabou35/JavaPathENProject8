package msgps.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import msgps.model.Gps;
import rewardCentral.RewardCentral;
import msrewards.model.Rewards;
import msrewards.service.RewardsService;
import msuser.service.UserService;
import msuser.model.User;

@Service
public class GpsService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	
	private Rewards rewards= new Rewards(new GpsUtil(), new RewardCentral());
	private Gps gps = new Gps(new GpsUtil(), new Rewards(new GpsUtil(), new RewardCentral()));
	private RewardsService rewardsService = new RewardsService(rewards);
	
	private ExecutorService executor = Executors.newFixedThreadPool(9000);
	
	@Autowired
	private UserService userService;
	
	public GpsService(Gps gps) {
		this.gps = gps;
	}
	
	public void submitLocation(User user, UserService userService) {
		CompletableFuture.supplyAsync(() -> {
		    return gps.getGpsUtil().getUserLocation(user.getUserId());
		}, executor)
			.thenAccept(visitedLocation -> { finalizeLocation(user, visitedLocation); });
	}
	
	public VisitedLocation getUserLocation(User user) {
		return gps.getGpsUtil().getUserLocation(user.getUserId());
	}
	
	public void trackUserLocation(User user) {
		submitLocation(user, userService);
	}
	
	public void finalizeLocation(User user, VisitedLocation visitedLocation) {
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
	}
	
	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		List<Attraction> nearbyAttractions = new ArrayList<>();
		List<Double> attractionsDistance = new ArrayList<>();
		
		// retrieves a distance list between the user and all attractions
		attractionsDistance = getAllAttractionsDistance(visitedLocation);
		
		// retrieves the five nearest attractions 
		Double distance = attractionsDistance.get(gps.getNumberOfAttractions()-1);
		gps.setAttractionProximityRange(distance);
		for(Attraction attraction : rewards.getGpsUtil().getAttractions()) {
			if(isWithinAttractionProximity(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		return nearbyAttractions;
	}
	
	/**
	 * retrieves a list of the five nearest attractions to a user<br/>
	 * gives :<br/> - The user's location lat/long <br/>
	 * 		   		- Name of Tourist attraction<br/>
	 * 		  		- Tourist attractions lat/long<br/>
	 * 		   		- The distance in miles between the user's location and each of the attractions<br/>
	 * 		   		- The reward points for visiting each Attraction 
	 * @param userName : the user's name
	 * @return a list of five attractions Map
	 */
	public List<Map<String, Object>> getNearByAttractionsForDisplay(VisitedLocation visitedLocation){
		RewardCentral rewardCentral = new RewardCentral();
		List<Attraction> nearbyAttractions = getNearByAttractions(visitedLocation);
    	Map<String, Object> userMap = new TreeMap<String, Object>();
    	List<Map<String, Object>> nearbyAttractionsList = new ArrayList<>();
    	
    	userMap.put("userLatitude", visitedLocation.location.latitude);
		userMap.put("userLongitude", visitedLocation.location.longitude);
		nearbyAttractionsList.add(userMap);
		
    	for(Attraction attraction : nearbyAttractions) {
    		Map<String, Object> nearbyAttractionsMap = new TreeMap<String, Object>();
    		
    		nearbyAttractionsMap.put("attractionName", attraction.attractionName);
    		nearbyAttractionsMap.put("latitude", attraction.latitude);
    		nearbyAttractionsMap.put("longitude", attraction.longitude);
    		nearbyAttractionsMap.put("distance", getDistance(attraction, visitedLocation.location));
    		nearbyAttractionsMap.put("reward", rewardCentral.getAttractionRewardPoints(attraction.attractionId, visitedLocation.userId));
    		
    		nearbyAttractionsList.add(nearbyAttractionsMap);
    	}
    	
    	return nearbyAttractionsList;
	}
	
	/**
	 * retrieves a list of every user's most recent location</br>
	 * gives :</br> - the userId</br>
	 * 				- the user's location lat/long</br>
	 * @return a list of Map
	 */
	public List<Map<String, Object>> getAllCurrentLocations(List<User> users) {
		List<Map<String, Object>> allCurrentLocations = new ArrayList<>();
		for(User user : users) {
			Map<String, Double> location = new TreeMap<String, Double>();
			Map<String, Object> currentLocation = new TreeMap<String, Object>();
			location.put("longitude", user.getLastVisitedLocation().location.longitude);
			location.put("latitude", user.getLastVisitedLocation().location.latitude);
			currentLocation.put(user.getUserId().toString(), location);
			allCurrentLocations.add(currentLocation);
		}

		return allCurrentLocations;
		
	}
	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > gps.getAttractionProximityRange() ? false : true;
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 3963 * angle;
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}
	
	private List<Double> getAllAttractionsDistance(VisitedLocation visitedLocation){
		List<Double> attractionsDistance = new ArrayList<>();
		// retrieves a distance list between the user and all attractions
		for(Attraction attraction : rewards.getGpsUtil().getAttractions()) {
			attractionsDistance.add(getDistance(attraction, visitedLocation.location));
		}
		Collections.sort(attractionsDistance);
		return attractionsDistance;
	}
	
}
