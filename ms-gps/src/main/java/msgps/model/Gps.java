package msgps.model;

import gpsUtil.GpsUtil;
import msrewards.model.Rewards;

public class Gps {
	private GpsUtil gpsUtil;
	private Rewards rewards;
	private Double attractionProximityRange = 200D;
	private int numberOfAttractions = 5;
	
	public GpsUtil getGpsUtil() {
		return gpsUtil;
	}
	public void setGpsUtil(GpsUtil gpsUtil) {
		this.gpsUtil = gpsUtil;
	}
	public Rewards getRewards() {
		return rewards;
	}
	public void setRewards(Rewards rewards) {
		this.rewards = rewards;
	}
	
	public void setAttractionProximityRange(Double attractionProximityRange) {
		this.attractionProximityRange = attractionProximityRange;
	}
	
	public Double getAttractionProximityRange() {
		return attractionProximityRange;
	}
	
	public int getNumberOfAttractions() {
		return numberOfAttractions;
	}
	public void setNumberOfAttractions(int numberOfAttractions) {
		this.numberOfAttractions = numberOfAttractions;
	}
	public Gps(GpsUtil gpsUtil, Rewards rewards) {
		super();
		this.gpsUtil = gpsUtil;
		this.rewards = rewards;
	}
	
}
