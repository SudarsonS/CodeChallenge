package backend.code.challenge.n26.banking;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * For Starting the Rest
 * @author sudarson
 *
 */
@SpringBootApplication
public class Application 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(Application.class, args);
    	InetAddress ip;
		  try {

			ip = InetAddress.getLocalHost();
			System.out.println("Application Started in the IP Address  : " + ip.getHostAddress());

		  } catch (UnknownHostException e) {

			e.printStackTrace();

		  }
    }
}
