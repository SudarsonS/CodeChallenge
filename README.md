# CodeChallenge
A simple REST application to calculate the statistics of last 60 seconds transactions.

## How to run the project? 
To run the project with Maven.

```
git clone https://github.com/SudarsonS/CodeChallenge.git
cd CodeChallenge-master
mvn clean install
mvn spring-boot:run
```
To run the project with Jar.

```
git clone https://github.com/SudarsonS/CodeChallenge.git
cd CodeChallenge-master
mvn clean package
java -jar CodeChallenge-master/target/banking-1.0.jar
```
To run the test.

```
mvn test
```

## Project Structure 
Maven Project
* `banking` package to hold everything.
  * `controller` package contains API Endpoints for getting statistics (\statistics) and adding transaction (\transactions)
  * `entity` package contains Models (Statistics and Transaction) 
  * `services` package contains Business logic
  * `util` package contains helper functions
  
##API Documentation

There are two endpoints \transactions and \statistics.

#### Transactions
This endpoint will update the `statisticsList` for that particular second of transaction timestamp. The `statisticsList` is a ArrayList of 60 objects which represents 60 seconds. Each object calculates the statistics of that particular second.

```java
import backend.code.challenge.n26.banking.entity.Statistics;
import backend.code.challenge.n26.banking.entity.Transaction;
import backend.code.challenge.n26.banking.util.Util;

@Component
public class TransactionService {
	private final static Logger logger = LoggerFactory.getLogger(TransactionService.class);
	private volatile ArrayList<Statistics> statisticsList = new ArrayList<Statistics>(60);
    //...
    }
```

**Request**
```
POST /transactions
```
**Response**
Returns Empty body with 201, 204 or 400 status code
```
201 CREATED     - In case of Success
204 NO_CONTENT  - In case the transactions is older than 60 seconds
400 BAD_REQUEST - In case the transaction is null or contains future time
```
 
#### Statistics
This endpoint will calculate the overall statistics from `statisticsList`. `statisticsList` always contains last 60 seconds transaction statistics.  
**Request**
```
GET /statistics
```
**Response**
Returns statistics json with 200 status code
```
200 OK  
{
"sum": 50, "avg": 25, "max": 40, "min": 10, "count": 2, "time":1478192204000
}
```   