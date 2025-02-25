# Task 30: Create User

**Time to complete:** 3 days

### Prerequisites

Before starting this task, you should refer to the information and lessons learned from the previous tasks, as well as review the provided Swagger documentation for the relevant endpoints.

### Task Description

Complete the following tasks using the provided requirements and Swagger documentation for the corresponding endpoints:

1. **Write Auto-Tests for User Creation:**
   - Develop automated tests to ensure the user creation functionality works according to the specified requirements.

2. **Bug Reporting (If Applicable):**
   - If you encounter any bugs, report them using a standard template. Recommended fields include:
     - Steps to reproduce the bug
     - Actual result
     - Expected result
     - Logs from the web service (if available)

### Requirements

**Scenario 1: Successful User Creation with All Fields**

- **Given**: You are an authorized user.
- **When**: You send a POST request to the `/users` endpoint with a user in the request body where all fields are filled in.
- **Then**: You should receive a 201 response code, the user should be added to the application, and the zip code should be removed from the available zip codes in the application.

**Scenario 2: Successful User Creation with Required Fields**

- **Given**: You are an authorized user.
- **When**: You send a POST request to the `/users` endpoint with a user in the request body where only the required fields are filled in.
- **Then**: You should receive a 201 response code, and the user should be added to the application.

**Scenario 3: User Creation with Incorrect Zip Code**

- **Given**: You are an authorized user.
- **When**: You send a POST request to the `/users` endpoint with a user in the request body where all fields are filled in, but the zip code is incorrect or unavailable.
- **Then**: You should receive a 424 response code, and the user should not be added to the application.

**Scenario 4: User Creation with Duplicate Name and Sex**

- **Given**: You are an authorized user.
- **When**: You send a POST request to the `/users` endpoint with a user in the request body that has the same name and sex as an existing user in the system.
- **Then**: You should receive a 400 response code, and the user should not be added to the application.
