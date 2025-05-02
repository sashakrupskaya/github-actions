package com.coherentsolutions.java.restapi.testing.test;

import com.coherentsolutions.java.restapi.testing.client.*;
import com.coherentsolutions.java.restapi.testing.user.Sex;
import com.coherentsolutions.java.restapi.testing.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import io.qameta.allure.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;




public class DeleteUserTest {
    private ClientInterface client;
    private static final String ZIP_CODES_URL = TestConfig.getZipCodesURL();
    private static final String USERS_URL = TestConfig.getUsersURL();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    protected static final Logger logger = LoggerFactory.getLogger(DeleteUserTest.class);
    private static final int INITIAL_AGE = 25;
    private static final String INITIAL_NAME = "Tester25";
    private static final Sex INITIAL_SEX = Sex.MALE;
    private static final String INITIAL_ZIP_CODE = "23456";

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        client = ClientFactory.createClient(ApacheClient.class);
        client.restartDockerContainer();
        client.resetHttpClient();
        client.refreshTokensAfterReset();
    }

    @AfterEach
    public void closeUp() throws IOException {
        client.shutdown();
    }
    private void createUser(User user) throws IOException {
        String requestBody = objectMapper.writeValueAsString(user);
        client.sendWriteRequest(USERS_URL, "POST", requestBody);
    }

    @Test
    @Tag("smoke")
    @Issue("16")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Delete User - API Test")
    @Description("Deletes a specified user and verifies status code and response")
    public void testDeleteUser() throws IOException {
        User userToDelete = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        User userToRemain = new User(Optional.of(26), "Tester26", Sex.FEMALE, Optional.of("12345"));
        createUser(userToDelete);
        String userToDeleteBody = objectMapper.writeValueAsString(userToDelete);
        createUser(userToRemain);
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        logger.info("Users: " + getUsers.getBody());

        HttpResponse deleteUser = client.sendWriteRequest(USERS_URL, "DELETE", userToDeleteBody);
        logger.info("User is deleted: " + deleteUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers1.getBody());
        HttpResponse getZipCodes1 = client.sendGetRequest(ZIP_CODES_URL);
        assertAll(
                "Grouped Assertions of deleteUser",
                () -> assertFalse(getZipCodes.getBody().contains("23456"), "The response body is not correct. Get zip codes: " + getZipCodes.getBody()),
                () -> assertEquals(204, deleteUser.getStatusCode(), "Status code is incorrect: " + deleteUser.getStatusCode()),
                () -> assertEquals("{}", deleteUser.getBody(), "The response body is incorrect: " + deleteUser.getBody()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"age\":26,\"sex\":\"FEMALE\",\"zipCode\":\"12345\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertTrue(getZipCodes1.getBody().contains("23456"), "The response body is not correct. Get zip codes: " + getZipCodes1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("18")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Delete User when only required fields are provided in the request - API Test")
    @Description("Deletes a user (only required fields are sent) and verifies status code and response")
    public void testDeleteUserWithOnlyRequiredFields() throws IOException {
        User userToDelete = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToDelete);
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        logger.info("Users: " + getUsers.getBody());

        HttpResponse deleteUser = client.sendWriteRequest(USERS_URL, "DELETE", "{\n" +
                "  \"name\": \"Tester25\",\n" +
                "  \"sex\": \"MALE\"\n" + "}"
        );
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        HttpResponse getZipCodes1 = client.sendGetRequest(ZIP_CODES_URL);
        assertAll(
                "Grouped Assertions of deleteUser",
                () -> assertFalse(getZipCodes.getBody().contains("23456"), "The response body is not correct. Get zip codes: " + getZipCodes.getBody()),
                () -> assertEquals(204, deleteUser.getStatusCode(), "Status code is incorrect: " + deleteUser.getStatusCode()),
                () -> assertEquals("{}", deleteUser.getBody(), "The response body is incorrect: " + deleteUser.getBody()),
                () -> assertEquals("{}", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertTrue(getZipCodes1.getBody().contains("23456"), "The response body is not correct. Get zip codes: " + getZipCodes1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("18")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Delete User when Age is missed in request - API Test")
    @Description("Deletes a user (Age is missed) and verifies status code and response")
    public void testDeleteUserWithMissingAge() throws IOException {
        User userToDelete = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToDelete);
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        logger.info("Users: " + getUsers.getBody());

        HttpResponse deleteUser = client.sendWriteRequest(USERS_URL, "DELETE", "{\n" +
                "  \"name\": \"Tester25\",\n" +
                "  \"sex\": \"MALE\",\n" +
                "  \"zipCode\": \"23456\"\n" + "}"
        );
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        HttpResponse getZipCodes1 = client.sendGetRequest(ZIP_CODES_URL);
        assertAll(
                "Grouped Assertions of deleteUser",
                () -> assertFalse(getZipCodes.getBody().contains("23456"), "The response body is not correct. Get zip codes: " + getZipCodes.getBody()),
                () -> assertEquals(204, deleteUser.getStatusCode(), "Status code is incorrect: " + deleteUser.getStatusCode()),
                () -> assertEquals("{}", deleteUser.getBody(), "The response body is incorrect: " + deleteUser.getBody()),
                () -> assertEquals("{}", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertTrue(getZipCodes1.getBody().contains("23456"), "The response body is not correct. Get zip codes: " + getZipCodes1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("18")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Delete User when Zip code is missed in request - API Test")
    @Description("Deletes a user (Zip cod is missed) and verifies status code and response")
    public void testDeleteUserWithMissingZipCode() throws IOException {
        User userToDelete = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToDelete);
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        logger.info("Users: " + getUsers.getBody());

        HttpResponse deleteUser = client.sendWriteRequest(USERS_URL, "DELETE", "{\n" +
                "  \"age\": 25,\n" +
                "  \"name\": \"Tester25\",\n" +
                "  \"sex\": \"MALE\"\n" + "}"
        );
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        HttpResponse getZipCodes1 = client.sendGetRequest(ZIP_CODES_URL);
        assertAll(
                "Grouped Assertions of deleteUser",
                () -> assertFalse(getZipCodes.getBody().contains("23456"), "The response body is not correct. Get zip codes: " + getZipCodes.getBody()),
                () -> assertEquals(204, deleteUser.getStatusCode(), "Status code is incorrect: " + deleteUser.getStatusCode()),
                () -> assertEquals("{}", deleteUser.getBody(), "The response body is incorrect: " + deleteUser.getBody()),
                () -> assertEquals("{}", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertTrue(getZipCodes1.getBody().contains("23456"), "The response body is not correct. Get zip codes: " + getZipCodes1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("17")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get 404 status code when try to delete not created user - API Test")
    @Description("Verifies the status code and response when tries to delete not existing user")
    public void testDeleteUserWhenNoUsersCreated() throws IOException {
        User userToDelete = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));

        String userToDeleteBody = objectMapper.writeValueAsString(userToDelete);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        HttpResponse deleteUser = client.sendWriteRequest(USERS_URL, "DELETE", userToDeleteBody);
        logger.info("Status code: " + deleteUser.getStatusCode());
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of deleteUser",
                () -> assertEquals("{}", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody()),
                () -> assertEquals(404, deleteUser.getStatusCode(), "Status code is incorrect: " + deleteUser.getStatusCode()),
                () -> assertTrue(deleteUser.getBody().contains("Not Found"), "The response body is incorrect: " + deleteUser.getBody().contains("Not Found")),
                () -> assertEquals("{}", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("17")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get 409 status code when try to delete user, provided empty body request - API Test")
    @Description("Verifies the status code and response when sends DELETE request with an empty body")
    public void testDeleteUserWithEmptyBodyRequest() throws IOException {
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Created users: " + getUsers.getBody());

        HttpResponse deleteUser = client.sendWriteRequest(USERS_URL, "DELETE", "{}");
        logger.info("Client error: " + deleteUser.getStatusCode());
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of deleteUser",
                () -> assertEquals(409, deleteUser.getStatusCode(), "Status code is incorrect: " + deleteUser.getStatusCode()),
                () -> assertTrue(deleteUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + deleteUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("{}", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("19")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get 409 status code when try to delete user, Name is missed in request - API Test")
    @Description("Verifies the status code and response when sends DELETE request with no Name")
    public void testDeleteUserWithMissingName() throws IOException {
        User userToDelete = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToDelete);
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        HttpResponse deleteUser = client.sendWriteRequest(USERS_URL, "DELETE", "{\n" +
                "{\n" +
                "  \"age\": 25,\n" +
                "  \"sex\": \"MALE\",\n" +
                "  \"zipCode\": \"23456\"\n" + "}"
        );
        logger.info("Client error: " + deleteUser.getStatusCode());
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of deleteUser",
                () -> assertEquals(409, deleteUser.getStatusCode(), "Status code is incorrect: " + deleteUser.getStatusCode()),
                () -> assertTrue(deleteUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + deleteUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("19")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get 409 status code when try to delete user, Sex is missed in request - API Test")
    @Description("Verifies the status code and response when sends DELETE request with no Sex")
    public void testDeleteUserWithMissingSex() throws IOException {
        User userToDelete = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToDelete);
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        HttpResponse deleteUser = client.sendWriteRequest(USERS_URL, "DELETE", "{\n" +
                "{\n" +
                "  \"age\": 25,\n" +
                "  \"name\": \"Tester25\",\n" +
                "  \"zipCode\": \"23456\"\n" + "}"
        );
        logger.info("Client error: " + deleteUser.getStatusCode());
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of deleteUser",
                () -> assertEquals(409, deleteUser.getStatusCode(), "Status code is incorrect: " + deleteUser.getStatusCode()),
                () -> assertTrue( deleteUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + deleteUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get 409 status code when try to delete user, Name and Sex are missed in request - API Test")
    @Description("Verifies the status code and response when sends DELETE request with no Name and Sex")
    public void testDeleteUserWithMissingNameAndSex() throws IOException {
        User userToDelete = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToDelete);
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        HttpResponse deleteUser = client.sendWriteRequest(USERS_URL, "DELETE", "{\n" +
                "  \"age\": 25,\n" +
                "  \"zipCode\": \"23456\"\n" + "}"
        );
        logger.info("Client error: " + deleteUser.getStatusCode());
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of deleteUser",
                () -> assertEquals(409, deleteUser.getStatusCode(), "Status code is incorrect: " + deleteUser.getStatusCode()),
                () -> assertTrue(deleteUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + deleteUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
}
