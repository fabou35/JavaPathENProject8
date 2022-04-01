package tourGuide.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.user.User;

@Service
public class GpsService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
	
	private Double attractionProximityRange = 200D;
	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	
	public GpsService(GpsUtil gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
	}
	
	public void setAttractionProximityRange(Double attractionProximityRange) {
		this.attractionProximityRange = attractionProximityRange;
	}
	
	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}
	
	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		
		List<Attraction> nearbyAttractions = new ArrayList<>();
		List<Double> attractionsDistance = new ArrayList<>();
		// retrieves a distance list between the user and all attractions
		for(Attraction attraction : gpsUtil.getAttractions()) {
			attractionsDistance.add(getDistance(attraction, visitedLocation.location));
		}
		// orders the list and retrieves the fifth value of the distances
		Collections.sort(attractionsDistance);
		Double distance = attractionsDistance.get(4);
		setAttractionProximityRange(distance);
		// retrieves the five attractions 
		for(Attraction attraction : gpsUtil.getAttractions()) {
			if(isWithinAttractionProximity(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		return nearbyAttractions;
	}
	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 3963 * angle;
        //double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}
}
