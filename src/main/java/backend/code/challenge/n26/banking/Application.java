package backend.code.challenge.n26.banking;


import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * For Starting the Rest
 * @author sudarson
 *
 */
@SpringBootApplication
@EnableScheduling
public class Application 
{
	final static Logger logger = Logger.getLogger(Application.class);
    public static void main( String[] args )
    {
    	SpringApplication.run(Application.class, args);
    }
}
