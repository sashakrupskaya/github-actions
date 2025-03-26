# Task 60: Delete User

**Deadline:** 3 days

### Prerequisites

Before starting this task, refer to the information and insights gained from previous tasks, as well as review the provided Swagger documentation for the relevant endpoints.

### Task Description

Complete the following tasks using the provided requirements and Swagger documentation for the corresponding endpoints:

1. **Write Tests for User Deletion Functionality:**
   - Develop automated tests to ensure the user deletion functionality works according to the specified requirements.

2. **Bug Reporting (If Applicable):**
   - If you encounter any bugs, report them using a standard template. Recommended fields include:
     - Steps to reproduce the bug
     - Actual result
     - Expected result
     - Logs from the web service (if available)

### Requirements

**Scenario 1: Successful User Deletion**

- **Given**: You are an authorized user.
- **When**: You send a DELETE request to the `/users` endpoint with a request body that contains the user to delete.
- **Then**: You should receive a 204 response code, the user should be deleted, and the zip code should be returned to the list of available zip codes.

**Scenario 2: Successful User Deletion with Required Fields Only**

- **Given**: You are an authorized user.
- **When**: You send a DELETE request to the `/users` endpoint with a request body that contains the user to delete, including only the required fields.
- **Then**: You should receive a 204 response code, the user should be deleted, and the zip code should be returned to the list of available zip codes.

**Scenario 3: User Deletion with Missing Required Fields**

- **Given**: You are an authorized user.
- **When**: You send a DELETE request to the `/users` endpoint with a request body that contains the user to delete, but any required field is missing.
- **Then**: You should receive a 409 response code, and the user should not be deleted.
