# Task 10
## SubTasks:

1. Go to http://localhost:<port>/swagger-ui/ and read information about all endpoints.
2. Create Maven or Gradle project. Dependencies you will need:
- Apache http client (required) as client
- Junit5 (highly recommended) or TestNG as testing framework
- Jackson (optional) as json/xml serialization/deserialization
- All other dependencies you may need
3. Develop client code to get bearer tokens with read and write scopes separately. Access to tokens must be made via singleton class (see requirements). Pay attention to singleton initialization: decide eager or lazy initialization will be better here and implement it
   Please note that tests should not be developed for now in the scope of this task.



## Requirements:

### Scenario #1
Given I am user
When I send POST request to `/oauth/token`
And I put parameters `grant_type=client_credentials` and `scope=write`
And I set `username=0oa157tvtugfFXEhU4x7` and `password=X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq` for basic auth
Then I get response with bearer token, which works for any `POST`, `PUT`, `PATCH`, `DELETE` methods of web-service.

### Scenario #2
Given I am user
When I send POST request to `/oauth/token`
And I put parameters `grant_type=client_credentials` and `scope=read`
And I set `username=0oa157tvtugfFXEhU4x7` and `password=X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq` for basic auth
Then I get response with bearer token, which works for any `GET` methods of web-service.