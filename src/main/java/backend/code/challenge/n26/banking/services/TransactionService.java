package backend.code.challenge.n26.banking.services;

import org.springframework.stereotype.Component;

import backend.code.challenge.n26.banking.entity.Transaction;

@Component
public class TransactionService {

	public void addTransactionIfTimeIsLastSixtySeconds(Transaction transaction) {
		System.out.println("Input : "+transaction);
	}

}
