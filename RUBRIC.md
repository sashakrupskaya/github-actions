# Grading Rubric for Task 10

### Total Points: 100

## **1. Understanding Endpoints (10 points)**
- 10 points: Thoroughly reads and understands all endpoints from the Swagger UI.
- 7 points: Reads and understands most endpoints, minor misunderstandings.
- 4 points: Reads but only partially understands the endpoints.
- 0 points: Does not read or understand the endpoints.

## **2. Project Setup (15 points)**
- 15 points: Correctly sets up Maven or Gradle project with all required dependencies.
- 10 points: Sets up the project but misses one non-essential dependency.
- 5 points: Sets up the project but misses multiple dependencies or includes incorrect ones.
- 0 points: Fails to set up the project correctly.

## **3. Dependency Management (10 points)**
- 10 points: Includes all required dependencies: Apache HttpClient, Junit5/TestNG, Jackson, and any other necessary dependencies.
- 7 points: Includes most required dependencies but misses one.
- 4 points: Includes some dependencies but misses more than one.
- 0 points: Includes few or none of the required dependencies.

## **4. Singleton Class Implementation (15 points)**
- 15 points: Correctly implements a singleton class for accessing tokens with appropriate initialization (eager or lazy) based on justified decision.
- 10 points: Implements a singleton class with correct initialization but lacks justification.
- 7 points: Implements a singleton class but with improper initialization.
- 4 points: Partially implements the singleton class.
- 0 points: Does not implement the singleton class.

## **5. Token Retrieval Logic (15 points)**
- 15 points: Develops robust client code to retrieve bearer tokens for both read and write scopes.
- 10 points: Develops client code that retrieves tokens but has minor issues.
- 7 points: Develops client code that retrieves tokens but has significant issues.
- 4 points: Develops incomplete client code for token retrieval.
- 0 points: Does not develop client code for token retrieval.

## **6. Scenario #1 Implementation (10 points)**
- 10 points: Correctly sends POST request to `/oauth/token` with all specified parameters for write scope and retrieves token.
- 7 points: Sends POST request with minor issues in parameters or token retrieval.
- 4 points: Sends POST request with major issues in parameters or token retrieval.
- 0 points: Fails to implement Scenario #1 correctly.

## **7. Scenario #2 Implementation (10 points)**
- 10 points: Correctly sends POST request to `/oauth/token` with all specified parameters for read scope and retrieves token.
- 7 points: Sends POST request with minor issues in parameters or token retrieval.
- 4 points: Sends POST request with major issues in parameters or token retrieval.
- 0 points: Fails to implement Scenario #2 correctly.

## **8. Code Quality and Documentation (5 points)**
- 5 points: Code is well-written, clean, and includes comments/documentation explaining the logic.
- 3 points: Code is mostly clean but lacks some comments/documentation.
- 1 point: Code is poorly written and lacks comments/documentation.
- 0 points: Code is difficult to read and understand with no comments/documentation.

## **9. Adherence to Requirements (10 points)**
- 10 points: Fully adheres to all specified requirements of the task.
- 7 points: Adheres to most requirements but misses one or two minor details.
- 4 points: Adheres to some requirements but misses several details.
- 0 points: Fails to adhere to most requirements.