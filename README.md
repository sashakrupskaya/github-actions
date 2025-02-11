# Task 10: Authorization

**Deadline:** 3 days

### Prerequisites

Before starting this task, make sure to thoroughly review the following resources:

- **[What is REST?](https://restfulapi.net):** Understand the basics of REST architecture and principles.
- **[HTTP Methods in REST API](https://restfulapi.net/http-methods/):** Learn about the different HTTP methods and their appropriate use in RESTful APIs.
- **[HttpClient Tutorial - Chapters 1 and 4](https://hc.apache.org/httpcomponents-client-4.5.x/current/tutorial/html/index.html):** Pay special attention to section 1.1.5 on releasing low-level resources.
- **[Client Credentials Grant](https://www.oauth.com/oauth2-servers/access-tokens/client-credentials/):** Learn how to obtain an access token using the client credentials grant.

### Task Description

In this task, you will implement a client that can obtain bearer tokens with different scopes (read and write) using the client credentials grant type. The task focuses on setting up the client correctly using the Apache HttpClient library, but you will not need to write tests at this stage.

**Tasks:**

1. **Review the Swagger Documentation:**
   - Visit `http://localhost:<port>/swagger-ui/` to review the available endpoints and understand the API's capabilities.

2. **Set Up Maven or Gradle Project:**
   - Create a new Maven or Gradle project.
   - Include the necessary dependencies:
     - Apache HttpClient (required)
     - JUnit 5 (recommended) or TestNG as the testing framework (though tests are not required in this task)
     - Jackson (optional) for JSON/XML serialization and deserialization
     - Any other dependencies you may need for the project.

3. **Develop the Client Code:**
   - Implement a client that can send POST requests to the `/oauth/token` endpoint to obtain bearer tokens.
   - The client must support both read and write scopes, obtaining separate tokens for each.
   - The client code must be implemented using a singleton pattern. Carefully decide whether to use eager or lazy initialization and implement it accordingly.
   - Ensure that the bearer token obtained with the write scope works for any POST, PUT, PATCH, or DELETE methods.
   - Ensure that the bearer token obtained with the read scope works for any GET methods.

### Requirements

**Scenario 1: Obtain Bearer Token with Write Scope**

- **Given**: I am a user.
- **When**: I send a POST request to the `/oauth/token` endpoint with `grant_type=client_credentials` and `scope=write`, and I use the username `0oa157tvtugfFXEhU4x7` and password `X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq` for basic authentication.
- **Then**: I should receive a response with a bearer token that works for any POST, PUT, PATCH, or DELETE methods of the web service.

**Scenario 2: Obtain Bearer Token with Read Scope**

- **Given**: I am a user.
- **When**: I send a POST request to the `/oauth/token` endpoint with `grant_type=client_credentials` and `scope=read`, and I use the username `0oa157tvtugfFXEhU4x7` and password `X7eBCXqlFC7x-mjxG5H91IRv_Bqe1oq7ZwXNA8aq` for basic authentication.
- **Then**: I should receive a response with a bearer token that works for any GET methods of the web service.
