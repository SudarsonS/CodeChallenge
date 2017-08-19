package backend.code.challenge.n26.banking.services;

import backend.code.challenge.n26.banking.entity.Statistics;
import backend.code.challenge.n26.banking.entity.Transaction;
import backend.code.challenge.n26.banking.util.Util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        transactionService.initialize();
    }


    @Test
    public void shouldReturnOverallStatsAsZeroWhenThereIsNoTransactionInLast60Seconds() throws Exception {

        Statistics overallStatistics = transactionService.getOverallStatistics();

        assertThat(overallStatistics.getCount(), is(0));
        assertEquals(0.0D, overallStatistics.getAverage(), 0.0001D);
        assertEquals(0.0D, overallStatistics.getSum(), 0.0001D);
        assertEquals(Double.NEGATIVE_INFINITY, overallStatistics.getMaximum(), 0.0001D);
        assertEquals(Double.POSITIVE_INFINITY, overallStatistics.getMinimum(), 0.0001D);
    }

     
    @Test
    public void shouldReturnOverallStatsAsBasedOnTransactionsInLastSixtySeconds() throws Exception {
        Transaction transaction = new Transaction();
       
        transaction.setAmount(20.0D);
        transaction.setTimestamp(System.currentTimeMillis()-20000L);
        transactionService.addTransaction(transaction);
        
        transaction.setAmount(30.0D);
        transaction.setTimestamp(System.currentTimeMillis()-21000L);
        transactionService.addTransaction(transaction);
        
        transaction.setAmount(10.0D);
        transaction.setTimestamp(System.currentTimeMillis()-20000L);
        transactionService.addTransaction(transaction);
        
        transaction.setAmount(5.0D);
        transaction.setTimestamp(System.currentTimeMillis()-21000L);
        transactionService.addTransaction(transaction);
        
        transaction.setAmount(35.0D);
        transaction.setTimestamp(System.currentTimeMillis()-58000L);
        transactionService.addTransaction(transaction);

        Statistics overallStatistics = transactionService.getOverallStatistics();

        assertThat(overallStatistics.getCount(), is(5));
        assertEquals(20.0D, overallStatistics.getAverage(),  0.0001D);
        assertEquals(100.0D, overallStatistics.getSum(),  0.0001D);
        assertEquals(35.0D, overallStatistics.getMaximum(),  0.0001D);
        assertEquals(5.0D, overallStatistics.getMinimum(),  0.0001D);
    }
    
    
}