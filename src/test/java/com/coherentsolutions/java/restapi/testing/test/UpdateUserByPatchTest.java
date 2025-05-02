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
import java.util.Map;
import java.util.Optional;




public class UpdateUserByPatchTest {
    private ClientInterface client;

    private static final String USERS_URL = TestConfig.getUsersURL();
    protected static final Logger logger = LoggerFactory.getLogger(UpdateUserByPatchTest.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
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
    public void closeUp() throws IOException {client.shutdown();}
    private void createUser(User user) throws IOException {
        String requestBody = objectMapper.writeValueAsString(user);
        client.sendWriteRequest(USERS_URL, "POST", requestBody);
    }
    @Test
    @Tag("smoke")
    @Issue("12")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Update User's all values - API Test")
    @Description("Updates a user and verifies status code and response")
    public void updateUserAllValues() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        User anotherUser = new User(Optional.of(25), "Tester25", Sex.FEMALE, Optional.of("ABCDE"));

        createUser(userToChange);
        createUser(anotherUser);

        User userNewValues = new User(Optional.of(26), "Tester26", Sex.FEMALE, Optional.of("12345"));
        String updateUserBody = objectMapper.writeValueAsString(Map.of("userNewValues", userNewValues, "userToChange", userToChange));

        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH", updateUserBody);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertEquals(200, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertEquals("{}", updateUser.getBody(), "The response body is incorrect: " + updateUser.getBody()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"FEMALE\",\"zipCode\":\"ABCDE\"},{\"name\":\"Tester26\",\"age\":26,\"sex\":\"FEMALE\",\"zipCode\":\"12345\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    //zip code is missed
    @Test
    @Tag("regression")
    @Issue("13")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Update User's Age, zip code is missed in userNewValues - API Test")
    @Description("Updates a user and verifies status code and response")
    public void updateAge() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH",
                "{\n" +
                        "  \"userNewValues\": {\n" +
                        "    \"age\": 26,\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\"\n" +
                        "  },\n" +
                        "  \"userToChange\": {\n" +
                        "    \"age\": 25,\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\",\n" +
                        "    \"zipCode\": \"23456\"\n" +
                        "  }\n" +
                        "}"
        );
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertEquals(200, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertEquals("{}", updateUser.getBody(), "The response body is incorrect: " + updateUser.getBody()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":26,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    //age and zip code are missed
    @Test
    @Tag("regression")
    @Issue("13")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Update User's Name, age and zip code are missed in userNewValues - API Test")
    @Description("Updates a user and verifies status code and response")
    public void updateName() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH",
                "{\n" +
                        "  \"userNewValues\": {\n" +
                        "    \"name\": \"Tester26\",\n" +
                        "    \"sex\": \"MALE\"\n" +
                        "  },\n" +
                        "  \"userToChange\": {\n" +
                        "    \"age\": 25,\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\",\n" +
                        "    \"zipCode\": \"23456\"\n" +
                        "  }\n" +
                        "}"
        );

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertEquals(200, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertEquals("{}", updateUser.getBody(), "The response body is incorrect: " + updateUser.getBody()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    //age and zip code are missed
    @Test
    @Tag("regression")
    @Issue("13")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Update User's Sex, age and zip code are missed in userNewValues - API Test")
    @Description("Updates a user and verifies status code and response")
    public void updateSex() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH",
                "{\n" +
                        "  \"userNewValues\": {\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"FEMALE\"\n" +
                        "  },\n" +
                        "  \"userToChange\": {\n" +
                        "    \"age\": 25,\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\",\n" +
                        "    \"zipCode\": \"23456\"\n" +
                        "  }\n" +
                        "}"
        );

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertEquals(200, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertEquals("{}", updateUser.getBody(), "The response body is incorrect: " + updateUser.getBody()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"FEMALE\",\"zipCode\":\"23456\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    //age is missed
    @Test
    @Tag("regression")
    @Issue("13")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Update User's Sex, age is missed in userNewValues - API Test")
    @Description("Updates a user and verifies status code and response")
    public void updateZipCode() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH",
                "{\n" +
                        "  \"userNewValues\": {\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\", \n" +
                        "    \"zipCode\": \"12345\"\n" +
                        "  },\n" +
                        "  \"userToChange\": {\n" +
                        "    \"age\": 25,\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\",\n" +
                        "    \"zipCode\": \"23456\"\n" +
                        "  }\n" +
                        "}"
        );

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertEquals(200, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertEquals("{}", updateUser.getBody(), "The response body is incorrect: " + updateUser.getBody()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"12345\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }

    @Test
    @Tag("regression")
    @Issue("12")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Update User, when userToChange goes first in the request body - API Test")
    @Description("Updates a user and verifies status code and response")
    public void updateUserWhenChangeUsersOrderInRequestBody() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        User userNewValues = new User(Optional.of(26), "Tester26", Sex.FEMALE, Optional.of("12345"));
        createUser(userToChange);
        String updateUserBody = objectMapper.writeValueAsString(Map.of("userToChange", userToChange, "userNewValues", userNewValues));
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH", updateUserBody);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertEquals(200, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertEquals("{}", updateUser.getBody(), "The response body is incorrect: " + updateUser.getBody()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"age\":26,\"sex\":\"FEMALE\",\"zipCode\":\"12345\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("12")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Update User, when changed fields order of userNewValues - API Test")
    @Description("Updates a user and verifies status code and response")
    public void updateUserWhenChangeTheFieldsOrderInNewValues() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH",
                "{\n" +
                        "  \"userNewValues\": {\n" +
                        "    \"name\": \"Tester26\",\n" +
                        "    \"age\": 26,\n" +
                        "    \"zipCode\": \"12345\",\n" +
                        "    \"sex\": \"FEMALE\"\n" +
                        "  },\n" +
                        "  \"userToChange\": {\n" +
                        "    \"age\": 25,\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\",\n" +
                        "    \"zipCode\": \"23456\"\n" +
                        "  }\n" +
                        "}"
        );
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                () -> assertEquals(200, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertEquals("{}", updateUser.getBody(), "The response body is incorrect: " + updateUser.getBody()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"age\":26,\"sex\":\"FEMALE\",\"zipCode\":\"12345\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("12")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Update User, when changed fields order of userToChange - API Test")
    @Description("Updates a user and verifies status code and response")
    public void updateUserWhenChangeTheFieldsOrderInToChangeValues() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH",
                "{\n" +
                        "  \"userNewValues\": {\n" +
                        "    \"age\": 26,\n" +
                        "    \"name\": \"Tester26\",\n" +
                        "    \"sex\": \"FEMALE\",\n" +
                        "    \"zipCode\": \"12345\"\n" +
                        "  },\n" +
                        "  \"userToChange\": {\n" +
                        "    \"zipCode\": \"23456\",\n" +
                        "    \"age\": 25,\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\"\n" +
                        "  }\n" +
                        "}"
        );
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                () -> assertEquals(200, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertEquals("{}", updateUser.getBody(), "The response body is incorrect: " + updateUser.getBody()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"age\":26,\"sex\":\"FEMALE\",\"zipCode\":\"12345\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("14")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to update a user, when incorrect Zip code in userNewValues - API Test")
    @Description("Gets 424 status code and verifies initial user is not updated")
    public void updateUserWhenIncorrectZipCode() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        User userNewValues = new User(Optional.of(26), "Tester26", Sex.FEMALE, Optional.of(""));

        createUser(userToChange);
        String updateUserBody = objectMapper.writeValueAsString(Map.of("userToChange", userToChange, "userNewValues", userNewValues));
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH", updateUserBody);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body after the update: " + updateUser.getBody());
        logger.info("Get users after the update: " + client.sendGetRequest(USERS_URL));
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertEquals(424, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertTrue( updateUser.getBody().contains("Specified zip code is not available"), "The response body is incorrect: " + updateUser.getBody().contains("Specified zip code is not available")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("14")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to update a user, when unavailable Zip code in userNewValues - API Test")
    @Description("Gets 424 status code and verifies initial user is not updated")
    public void updateUserWhenUnavailableZipCode() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        User anotherUser = new User(Optional.of(21), "Tester21", Sex.MALE, Optional.of("12345"));
        createUser(userToChange);
        createUser(anotherUser);

        User userNewValues = new User(Optional.of(26), "Tester26", Sex.FEMALE, Optional.of("12345"));
        String updateUserBody = objectMapper.writeValueAsString(Map.of("userNewValues", userNewValues, "userToChange", userToChange));
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH", updateUserBody);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertEquals(424, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertTrue( updateUser.getBody().contains("Specified zip code is not available"), "The response body is incorrect: " + updateUser.getBody().contains("Specified zip code is not available")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"},{\"name\":\"Tester21\",\"age\":21,\"sex\":\"MALE\",\"zipCode\":\"12345\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("14")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to update a user, when Name is missed in userNewValues - API Test")
    @Description("Gets 409 status code and verifies initial user is not updated")
    public void updateUserWhenNameIsMissed() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH",
                "{\n" +
                        "  \"userNewValues\": {\n" +
                        "    \"age\": 26,\n" +
                        "    \"sex\": \"FEMALE\",\n" +
                        "    \"zipCode\": \"12345\"\n" +
                        "  },\n" +
                        "  \"userToChange\": {\n" +
                        "    \"age\": 25,\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\",\n" +
                        "    \"zipCode\": \"23456\"\n" +
                        "  }\n" +
                        "}"
        );
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertEquals(409, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertTrue( updateUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + updateUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("14")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to update a user, when Sex is missed in userNewValues - API Test")
    @Description("Gets 409 status code and verifies initial user is not updated")
    public void updateUserWhenSexIsMissed() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH",
                "{\n" +
                        "  \"userNewValues\": {\n" +
                        "    \"age\": 26,\n" +
                        "    \"name\": \"Tester26\",\n" +
                        "    \"zipCode\": \"12345\"\n" +
                        "  },\n" +
                        "  \"userToChange\": {\n" +
                        "    \"age\": 25,\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\",\n" +
                        "    \"zipCode\": \"23456\"\n" +
                        "  }\n" +
                        "}"
        );
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertEquals(409, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertTrue( updateUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + updateUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("14")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to update a user, when Name and Sex are missed in userNewValues - API Test")
    @Description("Gets 409 status code and verifies initial user is not updated")
    public void updateUserWhenNameAndSexAreMissed() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH",
                "{\n" +
                        "  \"userNewValues\": {\n" +
                        "    \"age\": 26,\n" +
                        "    \"zipCode\": \"12345\"\n" +
                        "  },\n" +
                        "  \"userToChange\": {\n" +
                        "    \"age\": 25,\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\",\n" +
                        "    \"zipCode\": \"23456\"\n" +
                        "  }\n" +
                        "}"
        );
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertEquals(409, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertTrue( updateUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + updateUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("14")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to update a user, when all values are null in userNewValues - API Test")
    @Description("Gets 409 status code and verifies initial user is not updated")
    public void updateUserWhenAllNewValuesAreNull() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH",
                "{\n" +
                        "  \"userNewValues\": {\n" +
                        "    \"age\": null,\n" +
                        "    \"name\": null,\n" +
                        "    \"sex\": null,\n" +
                        "    \"zipCode\": null \n" +
                        "  },\n" +
                        "  \"userToChange\": {\n" +
                        "    \"age\": 25,\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\",\n" +
                        "    \"zipCode\": \"23456\"\n" +
                        "  }\n" +
                        "}"
        );
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                () -> assertEquals(409, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertTrue(updateUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + updateUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("12")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Update User's all values are the same - API Test")
    @Description("Updates a user and verifies status code and response")
    public void updateUserWithTheSameData() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        String updateUserBody = objectMapper.writeValueAsString(Map.of("userNewValues", userToChange, "userToChange", userToChange));
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH", updateUserBody);
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                "Grouped Assertions of createUser",
                () -> assertEquals(200, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertEquals("{}", updateUser.getBody(), "The response body is incorrect: " + updateUser.getBody()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("12")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to update a user, when userToChange does not exist - API Test")
    @Description("Gets 400 status code and verifies the body")
    public void updateNonExistingUser() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        User userNewValues = new User(Optional.of(26), "Tester26", Sex.FEMALE, Optional.of("12345"));
        String updateUserBody = objectMapper.writeValueAsString(Map.of("userNewValues", userNewValues, "userToChange", userToChange));
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH", updateUserBody);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                () -> assertEquals(400, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertTrue(updateUser.getBody().contains("User to change is not found or is null or new values are null"), "The response body is incorrect: " + updateUser.getBody().contains("User to change is not found or is null or new values are null")),
                () -> assertEquals("{}", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to update a user, when userToChange is null - API Test")
    @Description("Gets 400 status code and verifies the body")
    public void updateUserWhenUserToChangeIsNull() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH",
                "{\n" +
                        "  \"userNewValues\": {\n" +
                        "    \"age\": 26,\n" +
                        "    \"name\": \"Tester26\",\n" +
                        "    \"sex\": \"FEMALE\",\n" +
                        "    \"zipCode\": \"12345\"\n" +
                        "  },\n" +
                        "  \"userToChange\": null\n" +
                        "  }\n" +
                        "}"
        );
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                () -> assertEquals(400, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertTrue(updateUser.getBody().contains("User to change is not found or is null or new values are null"), "The response body is incorrect: " + updateUser.getBody().contains("User to change is not found or is null or new values are null")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("12")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to update a user, when all values are null in userToChange - API Test")
    @Description("Gets 400 status code and verifies the body")
    public void updateUserWhenUsersToChangeValuesAreNull() throws IOException {
        User userToChange = new User(null, null, null, null);
        User userNewValues = new User(Optional.of(26), "Tester26", Sex.FEMALE, Optional.of("12345"));
        createUser(userToChange);
        String updateUserBody = objectMapper.writeValueAsString(Map.of("userNewValues", userNewValues, "userToChange", userToChange));
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH", updateUserBody);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                () -> assertEquals(400, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertTrue(updateUser.getBody().contains("User to change is not found or is null or new values are null"), "The response body is incorrect: " + updateUser.getBody().contains("User to change is not found or is null or new values are null")),
                () -> assertEquals("{}", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to update a user, when all values are null in userNewValues - API Test")
    @Description("Gets 400 status code and verifies the body")
    public void updateUserWhenUserNewValuesAreNull() throws IOException {
        User userToChange = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToChange);
        HttpResponse updateUser = client.sendWriteRequest(USERS_URL, "PATCH",
                "{\n" +
                        "  \"userNewValues\": null \n" +
                        "  },\n" +
                        "  \"userToChange\": {\n" +
                        "    \"age\": 25,\n" +
                        "    \"name\": \"Tester25\",\n" +
                        "    \"sex\": \"MALE\",\n" +
                        "    \"zipCode\": \"23456\"\n" +
                        "  }\n" +
                        "}"
        );
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Response body: " + updateUser.getBody());
        assertAll(
                () -> assertEquals(400, updateUser.getStatusCode(), "Status code is incorrect: " + updateUser.getStatusCode()),
                () -> assertTrue(updateUser.getBody().contains("User to change is not found or is null or new values are null"), "The response body is incorrect: " + updateUser.getBody().contains("User to change is not found or is null or new values are null")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody())
        );
    }
}
