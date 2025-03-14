# Task 50: Update User

**Deadline:** 5 days

### Prerequisites

Before starting this task, refer to the information and insights gained from previous tasks, as well as review the provided Swagger documentation for the relevant endpoints.

### Task Description

Complete the following tasks using the provided requirements and Swagger documentation for the corresponding endpoints:

1. **Write Tests for User Update Functionality:**
   - Develop automated tests to ensure the user update functionality works according to the specified requirements.

2. **Bug Reporting (If Applicable):**
   - If you encounter any bugs, report them using a standard template. Recommended fields include:
     - Steps to reproduce the bug
     - Actual result
     - Expected result
     - Logs from the web service (if available)

### Requirements

**Scenario 1: Successful User Update**

- **Given**: You are an authorized user.
- **When**: You send a PUT or PATCH request to the `/users` endpoint with a request body that contains the user to update along with the new values.
- **Then**: You should receive a 200 response code, and the user should be successfully updated.

**Scenario 2: User Update with Incorrect Zip Code**

- **Given**: You are an authorized user.
- **When**: You send a PUT or PATCH request to the `/users` endpoint with a request body that contains the user to update along with new values, but the new zip code is incorrect or unavailable.
- **Then**: You should receive a 424 response code, and the user should not be updated.

**Scenario 3: User Update with Missing Required Fields**

- **Given**: You are an authorized user.
- **When**: You send a PUT or PATCH request to the `/users` endpoint with a request body that contains the user to update along with new values, but some required fields are missing.
- **Then**: You should receive a 409 response code, and the user should not be updated.
