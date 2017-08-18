package backend.code.challenge.n26.banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import backend.code.challenge.n26.banking.entity.Transaction;
import backend.code.challenge.n26.banking.services.TransactionService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@RequestMapping(value={"/transactions"}, method=RequestMethod.POST)
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction transaction) {
        transactionService.addTransaction(transaction);
        return new ResponseEntity<Transaction>(new Transaction(),HttpStatus.CREATED);
    }
}
