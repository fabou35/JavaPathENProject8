package msuser.configuration;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import msuser.helper.InternalTestHelper;
import msuser.tracker.Tracker;
import msuser.model.User;

public class TestModeConfiguration {
	private Logger logger = LoggerFactory.getLogger(TestModeConfiguration.class);
	public final Tracker tracker;
	boolean testMode = true;
	//Locale.setDefault(Locale.ENGLAND);
	private Locale locale = Locale.ENGLISH;

	public TestModeConfiguration() {
		
		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}
	
	public Map<String, User> getInternalUserMap(){
		return internalUserMap;
	}
	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}
	
	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		Locale.setDefault(locale);
		NumberFormat newFormatter = new DecimalFormat();
		double leftLimit = -180;
	    double rightLimit = 180;
	    double test = leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	    return Double.parseDouble(newFormatter.format(test));
	}
	
	private double generateRandomLatitude() {
		Locale.setDefault(locale);
		NumberFormat newFormatter = new DecimalFormat();
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    double test = leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	    return Double.parseDouble(newFormatter.format(test));
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
}
