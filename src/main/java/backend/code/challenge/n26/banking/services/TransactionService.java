package backend.code.challenge.n26.banking.services;


import java.util.ArrayList;
import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import backend.code.challenge.n26.banking.entity.Statistics;
import backend.code.challenge.n26.banking.entity.Transaction;

@Component
public class TransactionService {
	private final static Logger logger = LoggerFactory.getLogger(TransactionService.class);
	private volatile ArrayList<Statistics> statisticsList = new ArrayList<Statistics>(60);

	@PostConstruct
	public void initialize() {
		logger.info("Initializing statistics list");
		for(int i=0; i< 60; i++){ 
			statisticsList.add(new Statistics());
		}
    }
	
	public synchronized void addTransaction(Transaction transaction) {
			logger.info("Transaction is less than 60 seconds");
			updateStatisticsList(transaction);
	}

	/**
	 * ArrayList is initialized with 60 Statistics Objects
	 * Each Object represents each seconds from 0 to 59 (i.e.) statisticsList.get(1) represents 1st second, statisticsList.get(2) represents 2nd second and so on
	 * Second value for the transaction time is calculated and Statistics object for that second value is retrieved.
	 * If the time from the last updated stats is equal to the transaction time (Minutes & seconds), the stats is updated
	 * otherwise if the Minutes is larger, then the stats is reset with the transaction values
	 * Transaction Time (Minutes) smaller than the stats time is not taken into consideration (only less than 60 seconds)
	 * @param transaction
	 */
	private void updateStatisticsList(Transaction transaction) {
		String time[] = getTimeFromMilliseconds(transaction.getTimestamp()).split(":");
		int mins = Integer.parseInt(time[1]);
		int seconds = Integer.parseInt(time[2]);
		if(seconds >=0 && seconds <=59){ //Additional Check
			Statistics stats = statisticsList.get(seconds);
			String oldUpdatedTime[] = getTimeFromMilliseconds(stats.getTime()).split(":");
			int oldUpdatedMins = Integer.parseInt(oldUpdatedTime[1]);
			int oldUpdatedSeconds = Integer.parseInt(oldUpdatedTime[2]); 
			compareTime(mins, seconds, oldUpdatedMins, oldUpdatedSeconds, stats, transaction);
		}
	}

	private void compareTime(int mins, int seconds, int oldUpdatedMins, int oldUpdatedSeconds, Statistics stats, Transaction transaction) {
		if(mins == oldUpdatedMins && seconds == oldUpdatedSeconds){
			logger.info("Transaction Time is same with the last updated time");
			updateStats(stats, transaction);
		}else if (mins > oldUpdatedMins){
			logger.info("Transaction Time is greater than with the last updated time");
			resetOldStatsToNew(stats, transaction);
		}
	}

	/**
	 * Reset stats with transaction 
	 * @param stats
	 * @param transaction
	 */
	private void resetOldStatsToNew(Statistics stats, Transaction transaction) {
		stats.setCount(1);
		stats.setMaximum(transaction.getAmount());
		stats.setMinimum(transaction.getAmount());
		stats.setSum(transaction.getAmount());
		stats.setTime(transaction.getTimestamp());
		stats.setAverage(transaction.getAmount()/stats.getCount());
		logger.info("Reseted Stats : "+stats);
	}

	/**
	 * Update stats with transaction 
	 * @param stats
	 * @param transaction
	 */
	private void updateStats(Statistics stats, Transaction transaction) {
		stats.setCount(stats.getCount()+1);
		stats.setSum(stats.getSum()+transaction.getAmount());
		if(stats.getCount() != 0){
			stats.setAverage(stats.getSum()/stats.getCount());
		}
		if(transaction.getAmount() > stats.getMaximum()){
			stats.setMaximum(transaction.getAmount());
		}
		if(transaction.getAmount() < stats.getMinimum()){
			stats.setMinimum(transaction.getAmount());
		}
		logger.info("Updated Stats: "+stats);
	}

	/**
	 * Check whether transaction time is less than 60 seconds
	 * @param timestamp
	 * @return
	 */
	public boolean isLessThan60Seconds(long timestamp) {
		Calendar calendar = Calendar.getInstance();
		if(calendar.getTimeInMillis() - timestamp <= 60000L){
			 return true;
		 }
		return false;
	}

	/**
	 * Getting Hours, Minutes, Seconds from Milliseconds
	 * @param timestamp
	 * @return
	 */
	private String getTimeFromMilliseconds(long timestamp) {
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTimeInMillis(timestamp);
		 return calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND);
	}

	/**
	 * Statistics for each second is calculated 
	 * The Statistics list always contain the last 60 seconds data
	 * So at any point of time overall stats is calculated
	 * @return
	 */
	public synchronized Statistics getOverallStatistics() {
		Statistics overallStats = new Statistics();
		for (int i=0; i<60; i++){
			Statistics stats = statisticsList.get(i);
			calculateStats(overallStats, stats);
		}
		return overallStats;
	}

	/**
	 * Calculate the overall stats from each second
	 * @param overallStats
	 * @param stats
	 */
	private void calculateStats(Statistics overallStats, Statistics stats) {
		overallStats.setCount(overallStats.getCount()+stats.getCount());
		overallStats.setSum(overallStats.getSum()+stats.getSum());
		if(overallStats.getCount() != 0){
			overallStats.setAverage(overallStats.getSum()/overallStats.getCount());
		}
		if(stats.getMaximum() > overallStats.getMaximum()){
			overallStats.setMaximum(stats.getMaximum());
		}
		if(stats.getMinimum() < overallStats.getMinimum()){
			overallStats.setMinimum(stats.getMinimum());
		}
		overallStats.setTime(System.currentTimeMillis());
		logger.info("Overall Statistics : "+ overallStats);
	}

	/**
	 * Every nth second, nth stats is checked
	 * If the value is older than 60 seconds, nth stats is rest to zero
	 */
	@Scheduled(fixedDelay=1000)
	public synchronized void removeTheOldStats() {
		String currentTime = getTimeFromMilliseconds(System.currentTimeMillis());
		int min = Integer.parseInt(currentTime.split(":")[1]);
		int sec = Integer.parseInt(currentTime.split(":")[2]);
		if(sec >=0 && sec <=59){ //Additional Check
			Statistics stats = statisticsList.get(sec);
			int statsMin = Integer.parseInt(getTimeFromMilliseconds(stats.getTime()).split(":")[1]);
			if(statsMin < min){
				resetStatsToZero(stats);
			}
		}
	}

	/**
	 * If the stats time is lesser than 60 seconds, reset to zero
	 * @param stats
	 */
	private void resetStatsToZero(Statistics stats) {
		stats.setCount(0);
		stats.setAverage(0.0);
		stats.setMaximum(0.0);
		stats.setMinimum(0.0);
		stats.setTime(0L);
		stats.setSum(0.0);	
	}
}
