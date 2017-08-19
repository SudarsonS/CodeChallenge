package backend.code.challenge.n26.banking.entity;

public class Statistics {
	private volatile int count;
	private volatile double minimum;
	private volatile double maximum;
	private volatile double average;
	private volatile double sum;
	private volatile long time;
	
	
	public Statistics(int count, double minimum, double maximum, double average, double sum, long time) {
		super();
		this.count = count;
		this.minimum = minimum;
		this.maximum = maximum;
		this.average = average;
		this.sum = sum;
		this.time = time;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getMinimum() {
		return minimum;
	}
	public void setMinimum(double minimum) {
		this.minimum = minimum;
	}
	public double getMaximum() {
		return maximum;
	}
	public void setMaximum(double maximum) {
		this.maximum = maximum;
	}
	public double getAverage() {
		return average;
	}
	public void setAverage(double average) {
		this.average = average;
	}
	public double getSum() {
		return sum;
	}
	public void setSum(double sum) {
		this.sum = sum;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return "Statistics [count=" + count + ", minimum=" + minimum + ", maximum=" + maximum + ", average=" + average
				+ ", sum=" + sum + ", time=" + time + "]";
	}
}
