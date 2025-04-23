# Task 100: Final Task

**Deadline:** 1 day

### Prerequisites

Before starting this task, make sure to thoroughly review the following resources:

- **[GitHub Actions Documentation](https://docs.github.com/en/actions/writing-workflows):** Learn how to create and manage workflows using GitHub Actions, automate CI/CD processes, and understand workflow syntax.
- **[GitHub Actions - Введение в CI/CD (Video)](https://www.youtube.com/watch?v=e0A2hDObLmg):** Watch this video to get an introduction to CI/CD with GitHub Actions.

### Task Description

This is the final task of your training, and it involves setting up a CI/CD pipeline using GitHub Actions. Follow the steps below to complete the task:

1. **Create a GitHub Account:**
   - If you don't already have a GitHub account, create one. If you have an existing account, log in.

2. **Provide Collaborator Access:**
   - Grant your trainer the "Collaborator" role on your GitHub repository.

3. **Create a New Repository:**
   - Create a new repository on GitHub and upload your existing code with the automated tests.

4. **Set Up a Workflow:**
   - Navigate to the "Actions" tab in your repository and select "New workflow" followed by "Set up a workflow yourself." This will create a YAML file in your repository where you will define the pipeline steps.

5. **Configure the Pipeline:**
   - In the YAML file, describe the pipeline with the following steps:
     - Deploy the API image to a GitHub-hosted runner using Ubuntu (`ubuntu-latest`).
     - Install Java.
     - Install Maven or Gradle.
     - Check out your code from the repository.
     - Build your framework.
     - Execute the tests against the deployed application.
     - Publish the HTML test results using a suitable GitHub Action.

6. **Run Tests:**
   - Execute your current Apache HttpClient tests and ensure all tests pass (green).
   - Switch the implementation to use RestAssured, run the tests again, and ensure all tests pass (green).

7. **Create a Pull Request:**
   - Create a pull request that includes a `README.md` file with links to the green workflow results from both test executions.

### YAML Information

In step 5, the pipeline described in the YAML file should include:

- Deploy API image:
  ```bash
  docker pull coherentsolutions/rest-training:2.0
  docker run -d -p 8082:8082 coherentsolutions/rest-training:2.0
```
(Ensure the port number matches the one used in your framework)

- Check out the code from the GitHub repository.
- Build the testing framework.
- Execute tests against the deployed application.
- Publish HTML test results:
   - For TestNg/JUnit: Publish Test Results
   - For Allure Report: Allure GitHub Integration