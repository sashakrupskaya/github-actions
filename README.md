# Task 70: Upload Users

**Deadline:** 2 days

### Prerequisites

Before starting this task, make sure to review the coding examples provided in the "Multipart Upload with Apache HttpClient" tutorial, as well as any related information from previous tasks. This will give you a solid foundation for understanding how to handle multipart uploads with HttpClient.

### Task Description

Complete the following tasks using the provided requirements and Swagger documentation for the corresponding endpoints:

1. **Write Tests for User Upload Functionality:**
   - Develop automated tests to ensure the user upload functionality works according to the specified requirements.

2. **Bug Reporting (If Applicable):**
   - If you encounter any bugs, report them using a standard template. Recommended fields include:
     - Steps to reproduce the bug
     - Actual result
     - Expected result
     - Logs from the web service (if available)

### Requirements

**Scenario 1: Successful User Upload**

- **Given**: You are an authorized user.
- **When**: You send a POST request to the `/users/upload` endpoint with a request body containing a JSON file with an array of users to upload.
- **Then**: You should receive a 201 response code, all existing users should be replaced with the users from the file, and the response should contain the number of uploaded users.

**Scenario 2: User Upload with Incorrect Zip Code**

- **Given**: You are an authorized user.
- **When**: You send a POST request to the `/users/upload` endpoint with a request body containing a JSON file with an array of users to upload, and at least one user has an incorrect or unavailable zip code.
- **Then**: You should receive a 424 response code, and the users should not be uploaded.

**Scenario 3: User Upload with Missing Required Fields**

- **Given**: You are an authorized user.
- **When**: You send a POST request to the `/users/upload` endpoint with a request body containing a JSON file with an array of users to upload, and at least one user has a missing required field.
- **Then**: You should receive a 409 response code, and the users should not be uploaded.
