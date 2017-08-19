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
  * `services` Business logic
  * `util` package contains helper functions