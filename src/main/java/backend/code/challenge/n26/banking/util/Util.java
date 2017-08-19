package backend.code.challenge.n26.banking.util;

import java.util.Calendar;

public class Util {
	/**
	 * Check whether transaction time is less than 60 seconds
	 * @param timestamp
	 * @return
	 */
	public static boolean isLessThan60Seconds(long timestamp) {
		Calendar calendar = Calendar.getInstance();
		if(calendar.getTimeInMillis() - timestamp <= 60000L){
			 return true;
		 }
		return false;
	}

	public static boolean isFutureTime(long timestamp){
		Calendar currentTime = Calendar.getInstance();
		Calendar time = getTimeFromMilliseconds(timestamp);
		if (time.after(currentTime)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Getting Hours, Minutes, Seconds from Milliseconds
	 * @param timestamp
	 * @return
	 */
	public static Calendar getTimeFromMilliseconds(long timestamp) {
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTimeInMillis(timestamp);
		 return calendar;
	}
}
