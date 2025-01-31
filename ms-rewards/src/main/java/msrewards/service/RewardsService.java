package msrewards.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import msgps.service.GpsService;
import rewardCentral.RewardCentral;
import msuser.model.User;
import msuser.model.UserReward;

@Service
public class RewardsService {

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	
	private ExecutorService executor = Executors.newFixedThreadPool(9000);
	
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}
	
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	public void calculateRewards(User user) {
		List<Attraction> attractions = gpsUtil.getAttractions();
		
		List<VisitedLocation> userLocations = new CopyOnWriteArrayList<>();
		
		for(VisitedLocation location : user.getVisitedLocations()) {
			userLocations.add(location);
		}
		
		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : attractions) {
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if(nearAttraction(visitedLocation, attraction)) {
						//user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
						GpsService gpsService = new GpsService(gpsUtil, this);
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
		    //return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
			return getRewardPoints(attraction, user);
		}, executor)
			.thenAccept(points -> { 
				userReward.setRewardPoints(points);
				user.addUserReward(userReward);
			});
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		GpsService gpsService = new GpsService(gpsUtil, this);
		return gpsService.getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
}
