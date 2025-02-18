# Task 20: Get Available Zip Codes and Add More to the List

**Time to complete:** 3 days

### Prerequisites

Before starting this task, you should thoroughly review the following resources:

[HttpClient Tutorial](https://hc.apache.org/httpcomponents-client-4.5.x/current/tutorial/html/index.html)
  - Preface
  - Chapter 1: Fundamentals
    - Section 1.1: Request Execution
      - 1.1.1. HTTP Request
      - 1.1.2. HTTP Response
      - 1.1.3. Working with Message Headers
      - 1.1.4. HTTP Entity
        - 1.1.4.1. Repeatable Entities
        - 1.1.4.2. Using HTTP Entities
      - 1.1.5. Ensuring Release of Low-Level Resources
      - 1.1.6. Consuming Entity Content
      - 1.1.7. Producing Entity Content
        - 1.1.7.1. HTML Forms
        - 1.1.7.2. Content Chunking
      - 1.1.8. Response Handlers

[Posting with Apache HttpClient](https://www.baeldung.com/httpclient-post-http-request)
  - Overview
  - Basic POST
  - POST with Authorization
  - POST with JSON
  - POST with HttpClient Fluent API
  - POST Multipart Request
  - Uploading Files with HttpClient
  - Monitoring File Upload Progress

### Task Description

Complete the following tasks using the provided requirements and Swagger documentation for the corresponding endpoints:

1. **Write Tests for Zip Codes Functionality:**
   - Write comprehensive tests to cover all requirements for zip codes functionality.

2. **Bug Reporting (If Applicable):**
   - If you encounter any bugs, report them using a standard template. Recommended fields include:
     - Steps to reproduce the bug
     - Actual result
     - Expected result
     - Logs from the web service (if available)

### Requirements

**Scenario 1: Get Available Zip Codes**

- **Given**: You are an authorized user.
- **When**: You send a GET request to the `/zip-codes` endpoint.
- **Then**: You should receive a 200 response code and a list of all available zip codes in the application.

**Scenario 2: Add New Zip Codes**

- **Given**: You are an authorized user.
- **When**: You send a POST request to the `/zip-codes/expand` endpoint with a list of zip codes in the request body.
- **Then**: You should receive a 201 response code, and the zip codes from the request body should be added to the available zip codes in the application.

**Scenario 3: Add Zip Codes with Duplications**

- **Given**: You are an authorized user.
- **When**: You send a POST request to the `/zip-codes/expand` endpoint with a list of zip codes that include duplications of available zip codes.
- **Then**: You should receive a 201 response code, and the zip codes should be added to the available zip codes without any duplications.

**Scenario 4: Add Zip Codes with Duplications of Already Used Zip Codes**

- **Given**: You are an authorized user.
- **When**: You send a POST request to the `/zip-codes/expand` endpoint with a list of zip codes that include duplications of already used zip codes.
- **Then**: You should receive a 201 response code, and the zip codes should be added to the available zip codes without any duplications between available and already used zip codes.
