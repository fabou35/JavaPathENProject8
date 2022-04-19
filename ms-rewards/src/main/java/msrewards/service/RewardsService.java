package msrewards.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import msgps.model.Gps;
import msgps.service.GpsService;
import msrewards.model.Rewards;
import msuser.model.User;
import msuser.model.UserReward;

@Service
public class RewardsService {

	private Rewards rewards;
	private Gps gps;
	
	private ExecutorService executor = Executors.newFixedThreadPool(9000);
	
	public RewardsService(Rewards rewards) {
		this.rewards = rewards;
	}
	
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public void calculateRewards(User user) {
		List<Attraction> attractions = rewards.getGpsUtil().getAttractions();
		
		List<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();
		
		for(VisitedLocation location : user.getVisitedLocations()) {
			userLocations.add(location);
		}
		
		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : attractions) {
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if(nearAttraction(visitedLocation, attraction)) {
						GpsService gpsService = new GpsService(gps);
						Double distance = gpsService.getDistance(attraction, visitedLocation.location);
						UserReward userReward = new UserReward(visitedLocation, attraction, distance.intValue());
						submitRewardPoints(userReward, attraction, user);
					}
				}
			}
		}
	}
	
	public void submitRewardPoints(UserReward userReward, Attraction attraction, User user) {
		CompletableFuture.supplyAsync(() -> {
			return getRewardPoints(attraction, user);
		}, executor)
			.thenAccept(points -> { 
				userReward.setRewardPoints(points);
				user.addUserReward(userReward);
			});
		
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		GpsService gpsService = new GpsService(gps);
		return gpsService.getDistance(attraction, visitedLocation.location) > rewards.getProximityBuffer() ? false : true;
	}
	
	private int getRewardPoints(Attraction attraction, User user) {
		return rewards.getRewardCentral().getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
}
