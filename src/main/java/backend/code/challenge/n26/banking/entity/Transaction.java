package backend.code.challenge.n26.banking.entity;

public class Transaction {

	//Variable names are given according to the input body
	
	private double amount;
	//timestamp - Transaction Time
	private long timestamp;
	
	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Transaction [amount=" + amount + ", timestamp=" + timestamp + "]";
	}
}
