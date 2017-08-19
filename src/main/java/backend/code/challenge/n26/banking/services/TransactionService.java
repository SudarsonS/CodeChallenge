package backend.code.challenge.n26.banking.services;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import backend.code.challenge.n26.banking.entity.Statistics;
import backend.code.challenge.n26.banking.entity.Transaction;
import backend.code.challenge.n26.banking.util.Util;

@Component
public class TransactionService {
	private final static Logger logger = LoggerFactory.getLogger(TransactionService.class);
	private volatile ArrayList<Statistics> statisticsList = new ArrayList<Statistics>(60);

	@PostConstruct
	public void initialize() {
		logger.info("Initializing statistics list");
		Calendar cal = Calendar.getInstance();
		
		for(int i=0; i< 60; i++){ 
			cal.set(Calendar.SECOND, i);
			cal.set(Calendar.MILLISECOND, 0);
			statisticsList.add(new Statistics(0, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0.0, 0.0, cal.getTimeInMillis()));
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
		Calendar time = Util.getTimeFromMilliseconds(transaction.getTimestamp());
		int mins = time.get(Calendar.MINUTE);
		int seconds = time.get(Calendar.SECOND);
		Statistics stats = statisticsList.get(seconds);
		Calendar oldUpdatedTime = Util.getTimeFromMilliseconds(stats.getTime());
		logger.info("Time :::: {} :::: {}", new Date(time.getTimeInMillis()), new Date(oldUpdatedTime.getTimeInMillis()));
		compareTime(mins, seconds, oldUpdatedTime.get(Calendar.MINUTE), oldUpdatedTime.get(Calendar.SECOND), stats, transaction);
	}

	private void compareTime(int mins, int seconds, int oldUpdatedMins, int oldUpdatedSeconds, Statistics stats, Transaction transaction) {
		logger.info("Transaction Time = {}:{}, LastUpdated Time = {}:{}", mins, seconds, oldUpdatedMins, oldUpdatedSeconds);
		if(mins == oldUpdatedMins && seconds == oldUpdatedSeconds){
			logger.info("Transaction Time is same with the last updated time, Transaction Time = {}:{}, LastUpdated Time = {}:{}", mins, seconds, oldUpdatedMins, oldUpdatedSeconds);
			updateStats(stats, transaction);
		}else if (mins > oldUpdatedMins){
			logger.info("Transaction Time is greater than with the last updated time, Transaction Time = {}:{}, LastUpdated Time = {}:{}", mins, seconds, oldUpdatedMins, oldUpdatedSeconds);
			resetOldStatsToNew(stats, transaction);
		}
		logger.info("goint out compare time");
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
		logger.info("Reseted Stats : {} ",stats);
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
		logger.info("Updated Stats: {}",stats);
	}

	/**
	 * Statistics for each second is calculated 
	 * The Statistics list always contain the last 60 seconds data
	 * So at any point of time overall stats is calculated
	 * @return
	 */
	public synchronized Statistics getOverallStatistics() {
		Statistics overallStats = new Statistics(0, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 0.0, 0.0, System.currentTimeMillis());
		for (int i=0; i<60; i++){
			Statistics stats = statisticsList.get(i);
			calculateStats(overallStats, stats);
		}
		logger.info("Overall Statistics : {}", overallStats);
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
	}

	/**
	 * Every nth second, nth stats is checked
	 * If the value is older than 60 seconds, nth stats is rest to zero
	 * 
	 */
	@Scheduled(fixedDelay=1000)
	public synchronized void removeTheOldStats() {
		//calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),calendar.get(Calendar.SECOND)
		Calendar currentTime = Util.getTimeFromMilliseconds(System.currentTimeMillis());
		int min = currentTime.get(Calendar.MINUTE);
		int sec = currentTime.get(Calendar.SECOND);
		Statistics stats = statisticsList.get(sec);
		Calendar statsMin = Util.getTimeFromMilliseconds(stats.getTime());
		if(statsMin.get(Calendar.MINUTE) < min){
			resetStatsToZero(stats, sec);
		}
	}

	/**
	 * If the stats time is lesser than 60 seconds, reset to zero
	 * @param stats
	 */
	private void resetStatsToZero(Statistics stats, int sec) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.SECOND, sec);
		cal.set(Calendar.MILLISECOND, 0);
		stats.setCount(0);
		stats.setAverage(0.0);
		stats.setMaximum(Double.NEGATIVE_INFINITY);
		stats.setMinimum(Double.POSITIVE_INFINITY);
		stats.setTime(cal.getTimeInMillis());
		stats.setSum(0.0);	
	}
}
