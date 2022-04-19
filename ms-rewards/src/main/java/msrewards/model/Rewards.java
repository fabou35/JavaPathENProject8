package msrewards.model;

import gpsUtil.GpsUtil;
import rewardCentral.RewardCentral;

public class Rewards {

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	
	private GpsUtil gpsUtil;
	private RewardCentral rewardCentral;
	
	public int getProximityBuffer() {
		return proximityBuffer;
	}
	
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}
	
	public GpsUtil getGpsUtil() {
		return gpsUtil;
	}
	public void setGpsUtil(GpsUtil gpsUtil) {
		this.gpsUtil = gpsUtil;
	}
	public RewardCentral getRewardCentral() {
		return rewardCentral;
	}
	public void setRewardCentral(RewardCentral rewardCentral) {
		this.rewardCentral = rewardCentral;
	}
	
	public Rewards(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		super();
		this.gpsUtil = gpsUtil;
		this.rewardCentral = rewardCentral;
	}
}
