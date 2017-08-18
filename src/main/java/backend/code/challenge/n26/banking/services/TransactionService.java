package backend.code.challenge.n26.banking.services;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import backend.code.challenge.n26.banking.entity.Transaction;

@Component
public class TransactionService {
	final static Logger logger = Logger.getLogger(TransactionService.class);
	
	public void addTransactionIfTimeIsLastSixtySeconds(Transaction transaction) {
		logger.info("Input : "+transaction);
	}

}
