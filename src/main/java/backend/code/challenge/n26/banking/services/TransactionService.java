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
	 * Transaction time 'seconds' value is calculated and Statistics object for that 'seconds' value is retrieved.
	 * If the time from the last updated stats is less than 60 seconds to the transaction time, the stats is updated
	 * otherwise if it is greater than 1 minute, then the stats is reset with the transaction values
	 * Transaction Time smaller than the stats time is not taken into consideration (only less than 60 seconds)
	 * @param transaction
	 */
	private void updateStatisticsList(Transaction transaction) {
		Calendar time = Util.getTimeFromMilliseconds(transaction.getTimestamp());
		Statistics stats = statisticsList.get(time.get(Calendar.SECOND));
		Calendar oldUpdatedTime = Util.getTimeFromMilliseconds(stats.getTime());
		logger.debug("Time :::: {} :::: {}", new Date(time.getTimeInMillis()), new Date(oldUpdatedTime.getTimeInMillis()));
		compareTime(time, oldUpdatedTime, stats, transaction);
	}

	private void compareTime(Calendar time, Calendar oldUpdatedTime, Statistics stats, Transaction transaction) {
		Calendar transactionTime = Util.removeMillisecondsFromTime(time);
		Calendar updatedTime = Util.removeMillisecondsFromTime(oldUpdatedTime);
		logger.debug("Time CompareTime :::: {} :::: {}", new Date(time.getTimeInMillis()), new Date(updatedTime.getTimeInMillis()));
		if(Util.isLessThan60Seconds(transactionTime.getTimeInMillis())){
			updateStats(stats, transaction);
		}else if ((transactionTime.getTimeInMillis() - updatedTime.getTimeInMillis()) > 60000L){
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
		currentTime = Util.removeMillisecondsFromTime(currentTime);
		int sec = currentTime.get(Calendar.SECOND);
		Statistics stats = statisticsList.get(sec);
		Calendar statsTime = Util.getTimeFromMilliseconds(stats.getTime());
		statsTime = Util.removeMillisecondsFromTime(statsTime);
		if((currentTime.getTimeInMillis() - statsTime.getTimeInMillis()) > 60000L){
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
