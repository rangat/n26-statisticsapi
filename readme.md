# Backend Code Challenge for N26
###### Written by Rishikesh Tirumala

I've pasted the entire code challenge at the bottom of this document. [Jump to challenge](#challenge)

## Assumptions Made
* Given my interpretation the requirement below to avoid using [databases (including in-memory databases)](#database), I have built a solution that does not persist data in between instances of the application. 
* Given my interpretation of the [time descrepancy requirement](#time), ~~I am ensuring the POST `/transactions` endpoint rejects any transaction with a future `timestamp`~~, the POST spec doesn't require rejecting transactions with future endpoints; instead, I will ignore these when calculating statistics.   
* The spec seems to encourage the support JSON parameters to the rest API, so for now I am limiting the scope of input to the `/transactions` POST endpoint.

## Design and Technologies Used
* I'm using Jersey's standard REST framework to handle API calls, and MOXy on top of that to serialize and deserialize JSON-formatted input/output.
* I considered using a Dependency-Injection framework like Guice, but I figured since that I only really wanted one singleton, `StatisticsCache`, I decided to just make it a singleton in the server. 

## Progress
1. I started by setting up the project, adding the REST endpoints and a stub test, and then working on ensuring my POST `/transactions` endpoint could properly serialize the JSON object that expected to pass in. 
2. I touched up the `/transactions` endpoint such that it validated the timestamp for every request, and added it to an in-memory array in the `StatisticsCache` singleton.

## Notes and Limitations 
* I think given more time, I would have gone back and re-implemented the (de)serialization from JSON to use Jackson, and not MOXy. I'm much more familiar with Jackson and its flow for custom types. As it is, I think the current implementation is sufficient yet not ideal.

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
* The project should be buildable, and tests should also complete successfully. e.g. If maven is used, then mvn clean install should complete successfully.
* <a name="time"></a>The API should be able to deal with time discrepancy, which means, at any point of time, we could receive a transaction which have a timestamp of the past
* <a name="database"></a>Make sure to send the case in memory solution without database (including in-memory database)
* Endpoints have to execute in constant time and memory (`O(1)`)