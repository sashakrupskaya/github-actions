package com.coherentsolutions.java.restapi.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
}
