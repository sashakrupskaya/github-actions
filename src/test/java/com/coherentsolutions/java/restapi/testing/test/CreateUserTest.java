package com.coherentsolutions.java.restapi.testing.test;


import com.coherentsolutions.java.restapi.testing.client.HttpResponse;
import com.coherentsolutions.java.restapi.testing.client.OAuthClient;
import com.coherentsolutions.java.restapi.testing.client.TestConfig;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Issue;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;


public class CreateUserTest {
    private OAuthClient client;

    private static final String ZIP_CODES_URL = TestConfig.getZipCodesURL();
    private static final String USERS_URL = TestConfig.getUsersURL();
    private final ObjectMapper objectMapper = new ObjectMapper();
    protected static final Logger logger = LoggerFactory.getLogger(CreateUserTest.class);
    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        client = OAuthClient.getInstance();
        client.restartDockerContainer();
        client.resetHttpClient();
        client.refreshTokensAfterReset();
    }
    @AfterEach
    public void closeUp() throws IOException {client.shutdown();}

    @Test
    @Tag("smoke")
    @Issue("8")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Create User - API Test")
    @Description("Creates a user and verifies status code and response")
    public void createUser() throws IOException {
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        HttpResponse createUser = client.sendWriteRequest(USERS_URL, "POST", "{\n" +
                "  \"age\": 25,\n" +
                "  \"name\": \"Tester25\",\n" +
                "  \"sex\": \"MALE\",\n" +
                "  \"zipCode\": \"23456\"\n" + "}"
        );
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        HttpResponse getZipCodes1 = client.sendGetRequest(ZIP_CODES_URL);
        logger.info("Response body: " + createUser.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertFalse(getUsers.getBody().contains("name"), "The body is incorrect. Get users: " + getUsers.getBody()),
                () -> assertTrue(getZipCodes.getBody().contains("23456")),
                () -> assertEquals(201, createUser.getStatusCode(), "Status code is incorrect: " + createUser.getStatusCode()),
                () -> assertEquals("{}", createUser.getBody(), "The response body is incorrect: " + createUser.getBody()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertFalse(getZipCodes1.getBody().contains("23456"), "The body is incorrect. 23456 remains: " + getZipCodes1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Create User when change fields order in the request - API Test")
    @Description("Creates a user and verifies the user is created when change the fields order inside request")
    public void createUserWhenChangeTheFieldsOrder() throws IOException {
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        HttpResponse createUser = client.sendWriteRequest(USERS_URL, "POST", "{\n" +

                "  \"sex\": \"MALE\",\n" +
                "  \"age\": 99999999,\n" +
                "  \"name\": \"qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm\",\n" +
                "  \"zipCode\": \"23456\"\n" + "}"
        );
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        HttpResponse getZipCodes1 = client.sendGetRequest(ZIP_CODES_URL);
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertFalse(getUsers.getBody().contains("name"), "The body is incorrect. Get users: " + getUsers.getBody()),
                () -> assertTrue(getZipCodes.getBody().contains("23456")),
                () -> assertEquals(201, createUser.getStatusCode(), "Status code is incorrect: " + createUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm\",\"age\":99999999,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertFalse(getZipCodes1.getBody().contains("23456"), "The body is incorrect. 23456 remains: " + getZipCodes1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("5")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get 409 status code when send only Name inside request - API Test")
    @Description("Verifies the status code and response when only Name is sent")
    public void sendNameOnly() throws IOException {
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        HttpResponse createUser = client.sendWriteRequest(USERS_URL, "POST", "{\n" +

                "  \"name\": \"Tester25\",\n" + "}"
        );
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertFalse(getUsers.getBody().contains("name"), "The body is incorrect. Get users: " + getUsers.getBody()),
                () -> assertEquals(409, createUser.getStatusCode(), "Status code is incorrect: " + createUser.getStatusCode()),
                () -> assertFalse(getUsers1.getBody().contains("name"), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> {
                    JsonNode jsonNode = objectMapper.readTree(createUser.getBody());
                    assertEquals("Some required fields are missed", jsonNode.get("message").asText(), "Message field is incorrect.");
                }
        );
    }
    @Test
    @Tag("smoke")
    @Issue("5")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get 409 status code when send only Sex inside request - API Test")
    @Description("Verifies the status code and response when only Sex is sent")
    public void sendSexOnly() throws IOException {
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        HttpResponse createUser = client.sendWriteRequest(USERS_URL, "POST", "{\n" +

                "  \"sex\": \"MALE\",\n" + "}"
        );
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertFalse(getUsers.getBody().contains("sex"), "The body is incorrect. Get users: " + getUsers.getBody()),
                () -> assertEquals(409, createUser.getStatusCode(), "Status code is incorrect: " + createUser.getStatusCode()),
                () -> assertFalse(getUsers1.getBody().contains("sex"), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> {
                    JsonNode jsonNode = objectMapper.readTree(createUser.getBody());
                    assertEquals("Some required fields are missed", jsonNode.get("message").asText(), "Message field is incorrect.");
                }
        );
    }
    @Test
    @Tag("smoke")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get 409 status code when send an empty body in request - API Test")
    @Description("Verifies the status code and response when an empty body is sent")
    public void sendEmptyBody() throws IOException {
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        HttpResponse createUser = client.sendWriteRequest(USERS_URL, "POST", "{}");
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertFalse(getUsers.getBody().contains("name"), "The body is incorrect. Get users: " + getUsers.getBody()),
                () -> assertEquals(409, createUser.getStatusCode(), "Status code is incorrect: " + createUser.getStatusCode()),
                () -> assertFalse(getUsers1.getBody().contains("name"), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> {
                    JsonNode jsonNode = objectMapper.readTree(createUser.getBody());
                    assertEquals("Some required fields are missed", jsonNode.get("message").asText(), "Message field is incorrect.");
                }
        );
    }
    @Test
    @Tag("smoke")
    @Issue("6")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Create User by sending only required fields in request - API Test")
    @Description("Creates a user and verifies the user is created when only required fields were sent")
    public void createUserWhenSendOnlyRequiredFields() throws IOException {
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        HttpResponse createUser = client.sendWriteRequest(USERS_URL, "POST", "{\n" +
                "  \"name\": \"Tester25\",\n" +
                "  \"sex\": \"FEMALE\",\n"
        );
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertFalse(getUsers.getBody().contains("name"), "The body is incorrect. Get users: " + getUsers.getBody()),
                () -> assertEquals(201, createUser.getStatusCode(), "Status code is incorrect: " + createUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"sex\":\"FEMALE\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get 424 status code by sending incorrect Zip code in request - API Test")
    @Description("Verifies the status code and response when incorrect Zip code is sent")
    public void createUserWhenIncorrectZipCode() throws IOException {
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("No users added so far: " + getUsers.getBody());
        HttpResponse createUser = client.sendWriteRequest(USERS_URL, "POST", "{\n" +
                "  \"age\": 25,\n" +
                "  \"name\": \"Tester25\",\n" +
                "  \"sex\": \"MALE\",\n" +
                "  \"zipCode\": 1212 }"
        );
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info("No users added because of invalid request: " + getUsers1.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertFalse(getUsers.getBody().contains("name"), "The body is incorrect. Get users: " + getUsers.getBody()),
                () -> assertEquals(424, createUser.getStatusCode(), "Status code is incorrect: " + createUser.getStatusCode()),
                () -> assertFalse(getUsers1.getBody().contains("name"), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> {
                    JsonNode jsonNode = objectMapper.readTree(createUser.getBody());
                    assertEquals("Specified zip code is not available", jsonNode.get("message").asText(), "Message field is incorrect.");
                }
        );
    }
    @Test
    @Tag("smoke")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get 424 status code by sending unavailable Zip code in request - API Test")
    @Description("Verifies the status code and response when unavailable Zip code is sent")
    public void createUserWhenUnavailableZipCode() throws IOException {
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("No users added so far: " + getUsers.getBody());
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        logger.info("The list of zip codes: " + getZipCodes.getBody());
        HttpResponse createUser = client.sendWriteRequest(USERS_URL, "POST", "{\n" +
                "  \"age\": 25,\n" +
                "  \"name\": \"Tester25\",\n" +
                "  \"sex\": \"MALE\",\n" +
                "  \"zipCode\": \"55555\"\n" + "}"
        );
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info("No users added because of invalid request: " + getUsers1.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertFalse(getUsers.getBody().contains("name"), "The body is incorrect. Get users: " + getUsers.getBody()),
                () -> assertFalse(getZipCodes.getBody().contains("55555")),
                () -> assertEquals(424, createUser.getStatusCode(), "Status code is incorrect: " + createUser.getStatusCode()),
                () -> assertFalse(getUsers1.getBody().contains("name"), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> {
                    JsonNode jsonNode = objectMapper.readTree(createUser.getBody());
                    assertEquals("Specified zip code is not available", jsonNode.get("message").asText(), "Message field is incorrect.");
                }
        );
    }
    @Test
    @Tag("smoke")
    @Issue("7")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get 400 status code by trying to create a user with the same credentials - API Test")
    @Description("Verifies the status code and response when trying to create a user with the same credentials")
    public void createUsersWithEqualCredentials() throws IOException {
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        logger.info("The list of zip codes: " + getZipCodes.getBody());
        HttpResponse createUser = client.sendWriteRequest(USERS_URL, "POST", "{\n" +
                "  \"age\": 25,\n" +
                "  \"name\": \"Tester25\",\n" +
                "  \"sex\": \"MALE\",\n" +
                "  \"zipCode\": \"23456\"\n" + "}"
        );
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("The user with credentials: " + getUsers.getBody());
        HttpResponse createUser1 = client.sendWriteRequest(USERS_URL, "POST", "{\n" +
                "  \"age\": 25,\n" +
                "  \"name\": \"Tester25\",\n" +
                "  \"sex\": \"MALE\",\n" +
                "  \"zipCode\": \"23456\"\n" + "}"
        );
        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info("The user with credentials: " + getUsers1.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertTrue(getZipCodes.getBody().contains("23456")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody()),
                () -> assertEquals(400, createUser1.getStatusCode(), "Status code is incorrect: " + createUser1.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("9")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Create several users at a time - API Test")
    @Description("Creates several users and verifies the POST and GET responses (GET response is for confirmation user is created)")
    public void createSeveralUsersAtOnce() throws IOException {
        HttpResponse createUsers = client.sendWriteRequest(USERS_URL, "POST",
                "[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"},{\"name\":\"Tester87\",\"age\":87,\"sex\":\"MALE\",\"zipCode\":\"12345\"},{\"name\":\"Tester71\",\"age\":71,\"sex\":\"FEMALE\",\"zipCode\":\"ABCDE\"}]"
        );
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Create status code: " + createUsers.getStatusCode());
        logger.info("The list of users: " + getUsers.getBody());
        assertAll(
                "Grouped Assertions of getUsers",
                () -> assertEquals(201, createUsers.getStatusCode(), "Status code is incorrect: " + createUsers.getStatusCode()),
                () -> assertEquals("{}", createUsers.getBody(), "The response body is incorrect: " + createUsers.getBody()),
                () -> assertEquals(200, getUsers.getStatusCode(), "Incorrect status code: " + getUsers.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"},{\"name\":\"Tester87\",\"age\":87,\"sex\":\"MALE\",\"zipCode\":\"12345\"},{\"name\":\"Tester71\",\"age\":71,\"sex\":\"FEMALE\",\"zipCode\":\"ABCDE\"}]", getUsers.getBody(), "Incorrect body: " + getUsers.getBody())
        );
    }
}
