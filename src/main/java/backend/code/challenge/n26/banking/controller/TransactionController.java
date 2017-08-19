package backend.code.challenge.n26.banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import backend.code.challenge.n26.banking.entity.Statistics;
import backend.code.challenge.n26.banking.entity.Transaction;
import backend.code.challenge.n26.banking.services.TransactionService;
import backend.code.challenge.n26.banking.util.Util;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController {

	@Autowired
	private TransactionService transactionService;
	
	@RequestMapping(value={"/transactions"}, method=RequestMethod.POST)
    public ResponseEntity addTransaction(@RequestBody Transaction transaction) {
		if(transaction == null || Util.isFutureTime(transaction.getTimestamp())){
			return new ResponseEntity(HttpStatus.BAD_REQUEST); //400 status code
		}
			if(Util.isLessThan60Seconds(transaction.getTimestamp())){
				transactionService.addTransaction(transaction);
				return new ResponseEntity(HttpStatus.CREATED); //201 status code
			}else{
				return new ResponseEntity(HttpStatus.NO_CONTENT); //204 status code
			}
    }
	
	@RequestMapping(value={"/statistics"}, method=RequestMethod.GET)
    public ResponseEntity<Statistics> getStatistics() {
		Statistics stats = transactionService.getOverallStatistics();
		return new ResponseEntity<Statistics>(stats,HttpStatus.OK); //200 status code
	}
}
