package backend.code.challenge.n26.banking.controller;

import backend.code.challenge.n26.banking.entity.Statistics;
import backend.code.challenge.n26.banking.entity.Transaction;
import backend.code.challenge.n26.banking.services.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class TransactionControllerTest {

    @InjectMocks
    private TransactionController controller;

    @Mock
    private TransactionService transactionService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldReturnBadRequestWhenTransactionIsNull() throws Exception {

        ResponseEntity responseEntity = controller.addTransaction(null);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        verifyZeroInteractions(transactionService);
    }

    @Test
    public void shouldReturnBadRequestWhenTransactionIsInFutureTime() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(System.currentTimeMillis() + 10000L);

        ResponseEntity responseEntity = controller.addTransaction(transaction);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        verifyZeroInteractions(transactionService);
    }

    @Test
    public void shouldAddTransactionWhenTransactionTimeLessThan60SecondsFromCurrentTime() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(System.currentTimeMillis());
        transaction.setAmount(20D);

        ResponseEntity responseEntity = controller.addTransaction(transaction);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        verify(transactionService).addTransaction(transaction);
    }
  
    @Test
    public void shouldReturnEntityWithStatus204WhenTransactionIsOlderThan60Seconds() throws Exception {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(System.currentTimeMillis() - 100000L);
        transaction.setAmount(20D);

        ResponseEntity responseEntity = controller.addTransaction(transaction);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.NO_CONTENT));
        verifyZeroInteractions(transactionService);
    }
    
    @Test
    public void shouldReturnStatisticsWhenGettingTheStatistics() throws Exception {
        ResponseEntity<Statistics> responseEntity = controller.getStatistics();
        
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        verify(transactionService).getOverallStatistics();
    }
}