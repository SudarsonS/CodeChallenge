package backend.code.challenge.n26.banking.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

public class UtilTest {

	 @Test
	    public void shouldReturnTrueIsTransactionIsLessThan60SecondsFromTheCurrentTime() throws Exception {
	        long now = new Date().getTime();

	        boolean result = Util.isLessThan60Seconds(now);

	        assertTrue(result);
	    }

	    @Test
	    public void shouldReturnTrueIsTransactionIsGreaterThan60SecondsFromTheCurrentTime() throws Exception {
	        Date pastTime = new Date(1503108001047L);

	        boolean result = Util.isLessThan60Seconds(pastTime.getTime());

	        assertFalse(result);
	    }
	    
	    @Test
	    public void shouldReturnTrueIfTransactionIsFutureTime() throws Exception{
	    	boolean result = Util.isFutureTime(1512987071000L);
	    	
	    	assertTrue(result);
	    }
	    
	    @Test
	    public void shouldReturnFalseIfTransactionIsNotFutureTime() throws Exception{
	    	boolean result = Util.isFutureTime(System.currentTimeMillis());
	    	
	    	assertFalse(result);
	    }
}
