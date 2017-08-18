package backend.code.challenge.n26.banking.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import backend.code.challenge.n26.banking.entity.Transaction;

@Component
public class TransactionService {
	private final static Logger logger = LoggerFactory.getLogger(TransactionService.class);
	
	public void addTransaction(Transaction transaction) {
		logger.info("Input : "+transaction);
	}

}
