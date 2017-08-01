# Backend Code Challenge for N26
###### Written by Rishikesh Tirumala

I've pasted the entire code challenge at the bottom of this document. [Jump to challenge](#challenge)

## Assumptions Made
* Given my interpretation the requirement below to avoid using [databases (including in-memory databases)](#database), I have built a solution that does not persist data in between instances of the application. 
* Given my interpretation of the [time descrepancy requirement](#time), ~~I am ensuring the POST `/transactions` endpoint rejects any transaction with a future `timestamp`~~, the POST spec doesn't require rejecting transactions with future endpoints; instead, I will ignore these when calculating statistics. I allow future transactions to be "logged" through the endpoint.
* There is no mention of a `/transactions` GET endpoint, so I have omitted it's functionality, given how I am prioritizing the remainder of work to be done. I would likely not do this in a production environment.
* The spec seems to encourage the support JSON parameters to the rest API, so for now I am limiting the scope of input to the `/transactions` POST endpoint.

## Design Decisions and Technologies Used
* I'm using Jersey's standard REST framework to handle API calls, and MOXy on top of that to serialize and deserialize JSON-formatted input/output.
* I considered using a Dependency-Injection framework like Guice, but I figured since that I only really wanted one singleton, `StatisticsCache`, I decided to just make it a singleton in the server, and use the factory pattern. This lead to clunky code, and had there been a need for more interfaces like `StatisticsCache`, I would have used DI.  
* I decided to have the `/statistics` endpoint return a default JSON object in the event that there were no valid transactions. You can see this in `StatisticsResponse`. 
* I implemented an all endpoints in constant time but caching the statistics value, and running an update on a schedule of 1 second. This required ensuring the statistics cache was entirely thread-safe, since all REST endpoints that talked to it could do so synchronously. Though a constantly running process is `O(n)` each second, I consider it a fair trade-off to have constant-time access to the statistical data. 
* In the `StatisticsCache`, two lists represent the entire of transactions sent to the system. The first represents transactions that are not as of yet accounted for in statistics. Once a transaction is accounted for, it is moved to the second list, and once it is older than 60 seconds old, it is removed entirely. The sum and count is updated based on the removed and added transactions, and a new min and max are calculated for the currently represented transactions.
* I considered refactoring `StatisticsCacheImpl.updateCache` to use 2 stacks, to hold both the min and max values and avoid having to scan through multiple previously-seen transactions. However, I kept the implementation as is, given my interpretation of the `O(1)` space constraint.    

## Progress
1. Set up the project, adding the REST endpoints and a stub test, and then working on ensuring my POST `/transactions` endpoint could properly serialize the JSON object that expected to pass in. 
2. Touched up the `/transactions` endpoint such that it validated the timestamp for every request, and added it to an in-memory array in the `StatisticsCache` singleton.
3. Added a basic implementation (`O(n)` runtime) to the `/statistics` endpoint, going through a full round-trip of integration test cases. The work for this was done in `StatisticsCache`. 
4. Wrote tests for all of the classes, endpoints, and utilities I'd written so far
5. Changed the architecture of `StatisticsCache` such that it actually behaved as a cache and returned stats in `O(1)` time. 
6. Solved a major bug of my own creation in the `StatisticsCacheFactory`, where each call to `getInstance` returned a new instance instead of maintaining a singleton.
7. Wrote an integration test suite

## Notes and Limitations 
* I think given more time, I would have gone back and re-implemented the (de)serialization from JSON to use Jackson, and not MOXy. I'm much more familiar with Jackson and its flow for custom types. As it is, I think the current implementation is sufficient yet not ideal.
* I didn't like having to call `Instant.now()` on every request, instead of just getting the timestamp from the HTTP context. However, I found this difficult to do in Jersey, and left it as a limitation of my choice of framework. Looking back, I'd certainly use a different framework, since I found Jersey's documentation out of date and modern support limited. 
* By the end of writing my first pass at testing, I had begun to wish that I'd used Spring for both it's simple REST handling and dependency injection.


## <a name="challenge"></a>Code Challenge
We would like to have a restful API for our statistics. The main use case for our API is to calculate realtime statistic from the last 60 seconds. There will be two APIs, one of them is called every time a transaction is made. It is also the sole input of this rest API. The other one returns the statistic based of the transactions of the last 60 seconds.

### Specs
`POST /transactions`
Every Time a new transaction happened, this endpoint will be called.

Body:
```javascript
{
  "amount": 12.3,
  "timestamp": 1478192204000
}
```
Where:
* `amount` - transaction amount
* `timestamp` - transaction time in epoch in millis in UTC time zone (this is not current timestamp)

Returns: Empty body with either 201 or 204.
* `201` - in case of success
* `204` - if transaction is older than 60 seconds

Where:
* `amount` is a double specifying the amount
* `time` is a long specifying unix time format in milliseconds

`GET /statistics`
This is the main endpoint of this task, this endpoint have to execute in constant time and memory (O(1)). It returns the statistic based on the transactions which happened in the last 60 seconds.

Returns:
```javascript
{
  "sum": 1000,
  "avg": 100,
  "max": 200,
  "min": 50,
  "count": 10
}
```
Where:
* `sum` is a double specifying the total sum of transaction value in the last 60 seconds 
* `avg` is a double specifying the average amount of transaction value in the last 60 seconds
* `max` is a double specifying single highest transaction value in the last 60 seconds
* `min` is a double specifying single lowest transaction value in the last 60 seconds
* `count` is a long specifying the total number of transactions happened in the last 60 seconds

### Requirements
For the rest api, the biggest and maybe hardest requirement is to make the `GET /statistics` execute in constant time and space. The best solution would be `O(1)`. It is very recommended to tackle the `O(1)` requirement as the last thing to do as it is not the only thing which will be rated in the code challenge.

Other requirements, which are obvious, but also listed here explicitly:
* The API have to be threadsafe with concurrent requests
* The API have to function properly, with proper result
* The project should be buildable, and tests should also complete successfully. e.g. If maven is used, then `mvn clean install` should complete successfully.
* <a name="time"></a>The API should be able to deal with time discrepancy, which means, at any point of time, we could receive a transaction which have a timestamp of the past
* <a name="database"></a>Make sure to send the case in memory solution without database (including in-memory database)
* Endpoints have to execute in constant time and memory (`O(1)`)