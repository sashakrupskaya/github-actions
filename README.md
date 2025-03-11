# Task 40: Get Users and Filter Them

**Deadline:** 2 days

### Prerequisites

Before starting this task, refer to the information and insights gained from previous tasks, as well as review the provided Swagger documentation for the relevant endpoints.

### Task Description

Complete the following tasks using the provided requirements and Swagger documentation for the corresponding endpoints:

1. **Write Tests for User Retrieval and Filtering:**
   - Develop automated tests to ensure the functionality for retrieving and filtering users works according to the specified requirements.

2. **Bug Reporting (If Applicable):**
   - If you encounter any bugs, report them using a standard template. Recommended fields include:
     - Steps to reproduce the bug
     - Actual result
     - Expected result
     - Logs from the web service (if available)

### Requirements

**Scenario 1: Retrieve All Users**

- **Given**: You are an authorized user.
- **When**: You send a GET request to the `/users` endpoint.
- **Then**: You should receive a 200 response code and a list of all users currently stored in the application.

**Scenario 2: Retrieve Users Older Than a Specified Age**

- **Given**: You are an authorized user.
- **When**: You send a GET request to the `/users` endpoint and include the `olderThan` parameter.
- **Then**: You should receive a 200 response code and a list of all users older than the specified age.

**Scenario 3: Retrieve Users Younger Than a Specified Age**

- **Given**: You are an authorized user.
- **When**: You send a GET request to the `/users` endpoint and include the `youngerThan` parameter.
- **Then**: You should receive a 200 response code and a list of all users younger than the specified age.

**Scenario 4: Retrieve Users by Sex**

- **Given**: You are an authorized user.
- **When**: You send a GET request to the `/users` endpoint and include the `sex` parameter.
- **Then**: You should receive a 200 response code and a list of all users matching the specified sex.
