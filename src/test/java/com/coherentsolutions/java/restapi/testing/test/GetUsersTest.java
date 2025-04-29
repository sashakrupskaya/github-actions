package com.coherentsolutions.java.restapi.testing.test;

import com.coherentsolutions.java.restapi.testing.client.*;
import com.coherentsolutions.java.restapi.testing.user.Sex;
import com.coherentsolutions.java.restapi.testing.user.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class GetUsersTest {

    private ClientInterface client;

    private static final String USERS_URL = TestConfig.getUsersURL();
    protected static final Logger logger = LoggerFactory.getLogger(GetUsersTest.class);
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    private static final int INITIAL_AGE = 25;
    private static final String INITIAL_NAME = "Tester25";
    private static final Sex INITIAL_SEX = Sex.MALE;
    private static final String INITIAL_ZIP_CODE = "23456";

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        client = ClientFactory.createClient(RestAssuredClient.class);
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
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Get Users - API Test")
    @Description("Gets created users and verifies status code and response")
    public void getUsers() throws IOException {
        User user = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(user);
        User user1 = new User(Optional.of(87), "Tester87", Sex.MALE, Optional.of("12345"));
        createUser(user1);
        User user2 = new User(Optional.of(71), "Tester71", Sex.FEMALE, Optional.of("ABCDE"));
        createUser(user2);
        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("The list of users: " + getUsers.getBody());
        assertAll(
                "Grouped Assertions of getUsers",
                () -> assertEquals(200, getUsers.getStatusCode(), "Incorrect status code: " + getUsers.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"},{\"name\":\"Tester87\",\"age\":87,\"sex\":\"MALE\",\"zipCode\":\"12345\"},{\"name\":\"Tester71\",\"age\":71,\"sex\":\"FEMALE\",\"zipCode\":\"ABCDE\"}]", getUsers.getBody(), "Incorrect body: " + getUsers.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get Users older than parameter - API Test")
    @Description("Gets created users older than 25 and verifies status code and response")
    public void getUsersOlderThanAge() throws IOException {
        String usersOlderThan = USERS_URL + "?olderThan=25";
        HttpResponse getUsers = client.sendGetRequest(usersOlderThan);
        logger.info("The list of users older than 25: " + getUsers.getBody());
        User user = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(user);
        User user1 = new User(Optional.of(87), "Tester87", Sex.MALE, Optional.of("12345"));
        createUser(user1);
        User user2 = new User(Optional.of(71), "Tester71", Sex.FEMALE, Optional.of("ABCDE"));
        createUser(user2);
        HttpResponse getUsers1 = client.sendGetRequest(usersOlderThan);
        logger.info("The list of users older than 25: " + getUsers1.getBody());
        assertAll(
                "Grouped Assertions of getUsers olderThan 25",
                () -> assertEquals(200, getUsers.getStatusCode(), "Incorrect status code: " + getUsers.getStatusCode()),
                () -> assertEquals("[]", getUsers.getBody(), "Incorrect body: " + getUsers.getBody()),
                () -> assertEquals(200, getUsers1.getStatusCode(), "Incorrect status code: " + getUsers1.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester87\",\"age\":87,\"sex\":\"MALE\",\"zipCode\":\"12345\"},{\"name\":\"Tester71\",\"age\":71,\"sex\":\"FEMALE\",\"zipCode\":\"ABCDE\"}]", getUsers1.getBody(), "Incorrect body: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get Users older than border or incorrect parameters - API Test")
    @Description("Tries to get created users older than parameter and verifies status code and response")
    public void getUsersOlderThanAgeWhenParameterOnBorderOrIncorrect() throws IOException {
        String parameterMissed = USERS_URL + "?olderThan=";
        String parameterChar = USERS_URL + "?olderThan=q";
        String parameterNull = USERS_URL + "?olderThan=null";
        String parameterString = USERS_URL + "?olderThan=test";
        String parameterZero = USERS_URL + "?olderThan=0";
        String parameterBeforeBorder = USERS_URL + "?olderThan=2147483646";
        String parameterBorder = USERS_URL + "?olderThan=2147483647";
        String parameterAfterBorder = USERS_URL + "?olderThan=2147483648";

        User user = new User(Optional.of(2147483646), "Tester2147483646", INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(user);
        User user1 = new User(Optional.of(2147483647), "Tester2147483647", INITIAL_SEX, Optional.of("12345"));
        createUser(user1);
        HttpResponse createUsers2 = client.sendWriteRequest(USERS_URL, "POST", "{\n" +
                "  \"age\": 2147483648,\n" +
                "  \"name\": \"Tester2147483648\",\n" +
                "  \"sex\": \"FEMALE\",\n" +
                "  \"zipCode\": \"ABCDE\"\n" + "}"
        );
        assertAll(
                "Grouped assertion for cases olderThan",
                () -> assertEquals(200, client.sendGetRequest(parameterMissed).getStatusCode(), "Incorrect status code when parameter missed: " + client.sendGetRequest(parameterMissed).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester2147483646\",\"age\":2147483646,\"sex\":\"MALE\",\"zipCode\":\"23456\"},{\"name\":\"Tester2147483647\",\"age\":2147483647,\"sex\":\"MALE\",\"zipCode\":\"12345\"}]", client.sendGetRequest(parameterMissed).getBody(), "Incorrect body when parameter missed: " + client.sendGetRequest(parameterMissed).getBody()),
                () -> assertEquals(400, client.sendGetRequest(parameterChar).getStatusCode(), "Incorrect status code when parameter char: " + client.sendGetRequest(parameterChar).getStatusCode()),
                () -> assertEquals(400, client.sendGetRequest(parameterNull).getStatusCode(), "Incorrect status code when parameter null: " + client.sendGetRequest(parameterNull).getStatusCode()),
                () -> assertEquals(400, client.sendGetRequest(parameterString).getStatusCode(), "Incorrect status code when parameter string: " + client.sendGetRequest(parameterString).getStatusCode()),
                () -> assertEquals(200, client.sendGetRequest(parameterZero).getStatusCode(), "Incorrect status code when parameter 0: " + client.sendGetRequest(parameterZero).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester2147483646\",\"age\":2147483646,\"sex\":\"MALE\",\"zipCode\":\"23456\"},{\"name\":\"Tester2147483647\",\"age\":2147483647,\"sex\":\"MALE\",\"zipCode\":\"12345\"}]", client.sendGetRequest(parameterZero).getBody(), "Incorrect body when parameter 0: " + client.sendGetRequest(parameterZero).getBody()),
                () -> assertEquals(200, client.sendGetRequest(parameterBeforeBorder).getStatusCode(), "Incorrect status code when parameter before border: " + client.sendGetRequest(parameterBeforeBorder).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester2147483647\",\"age\":2147483647,\"sex\":\"MALE\",\"zipCode\":\"12345\"}]", client.sendGetRequest(parameterBeforeBorder).getBody(), "Incorrect users for older than 2147483646: " + client.sendGetRequest(parameterBeforeBorder).getBody()),
                () -> assertEquals(200, client.sendGetRequest(parameterBorder).getStatusCode(), "Incorrect status code when parameter on border: " + client.sendGetRequest(parameterBorder).getStatusCode()),
                () -> assertEquals("[]", client.sendGetRequest(parameterBorder).getBody(), "Incorrect body when parameterBorder: " + client.sendGetRequest(parameterBorder).getBody()),
                () -> assertEquals(400, client.sendGetRequest(parameterAfterBorder).getStatusCode(), "Incorrect status code when parameter after border: " + client.sendGetRequest(parameterAfterBorder).getStatusCode()),
                () -> {
                    JsonNode jsonNodeParameterChar = objectMapper.readTree(client.sendGetRequest(parameterChar).getBody());
                    JsonNode jsonNodeParameterNull = objectMapper.readTree(client.sendGetRequest(parameterNull).getBody());
                    JsonNode jsonNodeParameterString = objectMapper.readTree(client.sendGetRequest(parameterString).getBody());
                    JsonNode jsonNodeParameterAfterBorder = objectMapper.readTree(client.sendGetRequest(parameterAfterBorder).getBody());
                    assertEquals("Bad Request", jsonNodeParameterChar.get("error").asText(), "Incorrect text of error for char: " + jsonNodeParameterChar.get("error").asText());
                    assertEquals("Bad Request", jsonNodeParameterNull.get("error").asText(), "Incorrect text of error for null: " + jsonNodeParameterNull.get("error").asText());
                    assertEquals("Bad Request", jsonNodeParameterString.get("error").asText(), "Incorrect text of error for string: " + jsonNodeParameterString.get("error").asText());
                    assertEquals("Bad Request", jsonNodeParameterAfterBorder.get("error").asText(), "Incorrect text of error for ParameterAfterBorder: " + jsonNodeParameterAfterBorder.get("error").asText());

                }
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get Users younger than parameter - API Test")
    @Description("Gets created users younger than 71 and verifies status code and response")
    public void getUsersYoungerThanAge() throws IOException {
        String usersYoungerThan = USERS_URL + "?youngerThan=71";
        HttpResponse getUsers = client.sendGetRequest(usersYoungerThan);
        logger.info("The list of users younger than 71: " + getUsers.getBody());
        User user = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(user);
        User user1 = new User(Optional.of(87), "Tester87", Sex.MALE, Optional.of("12345"));
        createUser(user1);
        User user2 = new User(Optional.of(71), "Tester71", Sex.FEMALE, Optional.of("ABCDE"));
        createUser(user2);
        HttpResponse getUsers1 = client.sendGetRequest(usersYoungerThan);
        logger.info("The list of users younger than 71: " + getUsers1.getBody());
        assertAll(
                "Grouped Assertions of getUsers youngerThan 71",
                () -> assertEquals(200, getUsers.getStatusCode(), "Incorrect status code: " + getUsers.getStatusCode()),
                () -> assertEquals("[]", getUsers.getBody(), "Incorrect body: " + getUsers.getBody()),
                () -> assertEquals(200, getUsers1.getStatusCode(), "Incorrect status code: " + getUsers1.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "Incorrect body: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get Users younger than border or incorrect parameters - API Test")
    @Description("Tries to get created users younger than parameter and verifies status code and response")
    public void getUsersYoungerThanAgeWhenParameterOnBorderOrIncorrect() throws IOException {
        String parameterMissed = USERS_URL + "?youngerThan=";
        String parameterChar = USERS_URL + "?youngerThan=q";
        String parameterNull = USERS_URL + "?youngerThan=null";
        String parameterString = USERS_URL + "?youngerThan=test";
        String parameterZero = USERS_URL + "?youngerThan=0";
        String parameterBeforeBorder = USERS_URL + "?youngerThan=-2147483647";
        String parameterBorder = USERS_URL + "?youngerThan=-2147483648";
        String parameterAfterBorder = USERS_URL + "?youngerThan=-2147483649";

        User user = new User(Optional.of(-2147483647), "Tester-2147483647", INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(user);
        User user1 = new User(Optional.of(-2147483648), "Tester-2147483648", INITIAL_SEX, Optional.of("12345"));
        createUser(user1);
        HttpResponse createUsers2 = client.sendWriteRequest(USERS_URL, "POST", "{\n" +
                "  \"age\": -2147483649,\n" +
                "  \"name\": \"Tester-2147483649\",\n" +
                "  \"sex\": \"FEMALE\",\n" +
                "  \"zipCode\": \"ABCDE\"\n" + "}"
        );

        assertAll(
                "Grouped assertion for cases youngerThan",
                () -> assertEquals(200, client.sendGetRequest(parameterMissed).getStatusCode(), "Incorrect status code when parameter missed: " + client.sendGetRequest(parameterMissed).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester-2147483647\",\"age\":-2147483647,\"sex\":\"MALE\",\"zipCode\":\"23456\"},{\"name\":\"Tester-2147483648\",\"age\":-2147483648,\"sex\":\"MALE\",\"zipCode\":\"12345\"}]", client.sendGetRequest(parameterMissed).getBody(), "Incorrect body when parameter missed: " + client.sendGetRequest(parameterMissed).getBody()),
                () -> assertEquals(400, client.sendGetRequest(parameterChar).getStatusCode(), "Incorrect status code when parameter char: " + client.sendGetRequest(parameterChar).getStatusCode()),
                () -> assertEquals(400, client.sendGetRequest(parameterNull).getStatusCode(), "Incorrect status code when parameter null: " + client.sendGetRequest(parameterNull).getStatusCode()),
                () -> assertEquals(400, client.sendGetRequest(parameterString).getStatusCode(), "Incorrect status code when parameter string: " + client.sendGetRequest(parameterString).getStatusCode()),
                () -> assertEquals(200, client.sendGetRequest(parameterZero).getStatusCode(), "Incorrect status code when parameter 0: " + client.sendGetRequest(parameterZero).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester-2147483647\",\"age\":-2147483647,\"sex\":\"MALE\",\"zipCode\":\"23456\"},{\"name\":\"Tester-2147483648\",\"age\":-2147483648,\"sex\":\"MALE\",\"zipCode\":\"12345\"}]", client.sendGetRequest(parameterZero).getBody(), "Incorrect body when parameter 0: " + client.sendGetRequest(parameterZero).getBody()),
                () -> assertEquals(200, client.sendGetRequest(parameterBeforeBorder).getStatusCode(), "Incorrect status code when parameter before border: " + client.sendGetRequest(parameterBeforeBorder).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester-2147483648\",\"age\":-2147483648,\"sex\":\"MALE\",\"zipCode\":\"12345\"}]", client.sendGetRequest(parameterBeforeBorder).getBody(), "Incorrect users for younger than -2147483647: " + client.sendGetRequest(parameterBeforeBorder).getBody()),
                () -> assertEquals(200, client.sendGetRequest(parameterBorder).getStatusCode(), "Incorrect status code when parameter on border: " + client.sendGetRequest(parameterBorder).getStatusCode()),
                () -> assertEquals("[]", client.sendGetRequest(parameterBorder).getBody(), "Incorrect body when parameterBorder: " + client.sendGetRequest(parameterBorder).getBody()),
                () -> assertEquals(400, client.sendGetRequest(parameterAfterBorder).getStatusCode(), "Incorrect status code when parameter after border: " + client.sendGetRequest(parameterAfterBorder).getStatusCode()),
                () -> {
                    JsonNode jsonNodeParameterChar = objectMapper.readTree(client.sendGetRequest(parameterChar).getBody());
                    JsonNode jsonNodeParameterNull = objectMapper.readTree(client.sendGetRequest(parameterNull).getBody());
                    JsonNode jsonNodeParameterString = objectMapper.readTree(client.sendGetRequest(parameterString).getBody());
                    JsonNode jsonNodeParameterAfterBorder = objectMapper.readTree(client.sendGetRequest(parameterAfterBorder).getBody());
                    assertEquals("Bad Request", jsonNodeParameterChar.get("error").asText(), "Incorrect text of error for char: " + jsonNodeParameterChar.get("error").asText());
                    assertEquals("Bad Request", jsonNodeParameterNull.get("error").asText(), "Incorrect text of error for null: " + jsonNodeParameterNull.get("error").asText());
                    assertEquals("Bad Request", jsonNodeParameterString.get("error").asText(), "Incorrect text of error for string: " + jsonNodeParameterString.get("error").asText());
                    assertEquals("Bad Request", jsonNodeParameterAfterBorder.get("error").asText(), "Incorrect text of error for ParameterAfterBorder: " + jsonNodeParameterAfterBorder.get("error").asText());
                }
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get Users by Sex - API Test")
    @Description("Gets created users by sex (lower case) and verifies status code and response")
    public void getUsersBySex() throws IOException {
        String male = USERS_URL + "?sex=MALE";
        String female = USERS_URL + "?sex=FEMALE";
        User user = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(user);
        User user1 = new User(Optional.of(87), "Tester87", Sex.MALE, Optional.of("12345"));
        createUser(user1);
        User user2 = new User(Optional.of(71), "Tester71", Sex.FEMALE, Optional.of("ABCDE"));
        createUser(user2);
        assertAll(
                "Grouped Assertions of getUsers",
                () -> assertEquals(200, client.sendGetRequest(male).getStatusCode(), "Incorrect status code for male users: " + client.sendGetRequest(male).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"},{\"name\":\"Tester87\",\"age\":87,\"sex\":\"MALE\",\"zipCode\":\"12345\"}]", client.sendGetRequest(male).getBody(), "Incorrect body for male users: " + client.sendGetRequest(male).getBody()),
                () -> assertEquals(200, client.sendGetRequest(female).getStatusCode(), "Incorrect status code for female users: " + client.sendGetRequest(female).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester71\",\"age\":71,\"sex\":\"FEMALE\",\"zipCode\":\"ABCDE\"}]", client.sendGetRequest(female).getBody(), "Incorrect body for female users: " + client.sendGetRequest(female).getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get Users by Sex - API Test")
    @Description("Gets created users by Sex (upper case) and verifies status code and response")
    public void getUsersBySexUpperCaseInParameter() throws IOException {
        String male = USERS_URL + "?Sex=MALE";
        String female = USERS_URL + "?Sex=FEMALE";
        User user = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(user);
        logger.info("Users filtered by male: " + client.sendGetRequest(male).getBody());
        logger.info("Users filtered by female: " + client.sendGetRequest(female).getBody());
        assertAll(
                "Grouped Assertions of getUsers",
                () -> assertEquals(200, client.sendGetRequest(male).getStatusCode(), "Incorrect status code for male users: " + client.sendGetRequest(male).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", client.sendGetRequest(male).getBody(), "Incorrect body: " + client.sendGetRequest(male).getBody()),
                () -> assertEquals(200, client.sendGetRequest(female).getStatusCode(), "Incorrect status code for female users: " + client.sendGetRequest(female).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", client.sendGetRequest(female).getBody(), "Incorrect body: " + client.sendGetRequest(female).getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get 409 status code when try to get users by two parameters of Age - API Test")
    @Description("Verifies status code and response")
    public void getUsersOlderThanAndYoungerThanAge() throws IOException {
        String usersOlderThanAndYoungerThan = USERS_URL + "?olderThan=71&youngerThan=71";
        User user = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(user);
        User user1 = new User(Optional.of(87), "Tester87", Sex.MALE, Optional.of("12345"));
        createUser(user1);
        User user2 = new User(Optional.of(71), "Tester71", Sex.FEMALE, Optional.of("ABCDE"));
        createUser(user2);

        logger.info("The list of users older than 71 and younger than 71: " + client.sendGetRequest(usersOlderThanAndYoungerThan).getBody());
        assertAll(
                "Grouped Assertions of getUsers olderThan 25",
                () -> assertEquals(409, client.sendGetRequest(usersOlderThanAndYoungerThan).getStatusCode(), "Incorrect status code: " + client.sendGetRequest(usersOlderThanAndYoungerThan).getStatusCode()),
                () -> {
                    JsonNode jsoneNode = objectMapper.readTree(client.sendGetRequest(usersOlderThanAndYoungerThan).getBody());
                    assertEquals("Parameters youngerThan and olderThan can't be specified together", jsoneNode.get("message").asText());
                }
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get Users by Age and Sex - API Test")
    @Description("Gets created users by Age and Sex, verifies status code and response")
    public void filterUsersByAgeAndSex() throws IOException {
        String filterMaleUsers = USERS_URL + "?olderThan=10&sex=MALE";
        String filterFemaleUsers = USERS_URL + "?olderThan=10&sex=FEMALE";

        User user = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(user);
        assertAll(
                "Grouped Assertions of getUsers",
                () -> assertEquals(200, client.sendGetRequest(filterMaleUsers).getStatusCode(), "Incorrect status code for male users: " + client.sendGetRequest(filterMaleUsers).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", client.sendGetRequest(filterMaleUsers).getBody(), "Incorrect body for male users: " + client.sendGetRequest(filterMaleUsers).getBody()),
                () -> assertEquals(200, client.sendGetRequest(filterFemaleUsers).getStatusCode(), "Incorrect status code for female users: " + client.sendGetRequest(filterFemaleUsers).getStatusCode()),
                () -> assertEquals("[]", client.sendGetRequest(filterFemaleUsers).getBody(), "Incorrect body for female users: " + client.sendGetRequest(filterFemaleUsers).getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get Users by Sex and Age - API Test")
    @Description("Gets created users by Sex and Age, verifies status code and response")
    public void filterUsersBySexAndAge() throws IOException {
        String filterMaleUsers = USERS_URL + "?sex=MALE&olderThan=10";
        String filterFemaleUsers = USERS_URL + "?sex=FEMALE&olderThan=10";

        User user = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(user);
        assertAll(
                "Grouped Assertions of getUsers",
                () -> assertEquals(200, client.sendGetRequest(filterMaleUsers).getStatusCode(), "Incorrect status code for male users: " + client.sendGetRequest(filterMaleUsers).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", client.sendGetRequest(filterMaleUsers).getBody(), "Incorrect body for male users: " + client.sendGetRequest(filterMaleUsers).getBody()),
                () -> assertEquals(200, client.sendGetRequest(filterFemaleUsers).getStatusCode(), "Incorrect status code for female users: " + client.sendGetRequest(filterFemaleUsers).getStatusCode()),
                () -> assertEquals("[]", client.sendGetRequest(filterFemaleUsers).getBody(), "Incorrect body for female users: " + client.sendGetRequest(filterFemaleUsers).getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get 400 status code when try to get users by sex=male - API Test")
    @Description("Verifies status code and response when try to get users by sex=male")
    public void getUsersBySexIncorrectParameterValue() throws IOException {
        String male = USERS_URL + "?sex=male";
        String female = USERS_URL + "?sex=female";
        User user = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(user);
        assertAll(
                "Grouped Assertions of getUsers",
                () -> assertEquals(400, client.sendGetRequest(male).getStatusCode(), "Incorrect status code for male users: " + client.sendGetRequest(male).getStatusCode()),
                () -> assertEquals(400, client.sendGetRequest(female).getStatusCode(), "Incorrect status code for female users: " + client.sendGetRequest(female).getStatusCode()),
                () -> {
                    JsonNode jsonNodeFemale = objectMapper.readTree(client.sendGetRequest(female).getBody());
                    JsonNode jsonNodeMale = objectMapper.readTree(client.sendGetRequest(male).getBody());
                    assertEquals("Bad Request", jsonNodeFemale.get("error").asText(), "Error field is incorrect: " +jsonNodeFemale.get("error").asText());
                    assertEquals("Bad Request", jsonNodeMale.get("error").asText(), "Error field is incorrect: " + jsonNodeMale.get("error").asText());
                }
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Get 400 status code when try to get users by incorrect parameter - API Test")
    @Description("Verifies status code and response when try to get users by incorrect parameter")
    public void getUsersBySexNegativeCases() throws IOException {
        String parameterMissed = USERS_URL + "?sex=";
        String parameterNull = USERS_URL + "?sex=null";
        String parameterZero = USERS_URL + "?sex=0";
        User user = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(user);
        assertAll(
                "Grouped assertion for cases youngerThan",
                () -> assertEquals(200, client.sendGetRequest(parameterMissed).getStatusCode(), "Incorrect status code when parameter missed: " + client.sendGetRequest(parameterMissed).getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", client.sendGetRequest(parameterMissed).getBody()),
                () -> assertEquals(400, client.sendGetRequest(parameterNull).getStatusCode(), "Incorrect status code when parameter null: " + client.sendGetRequest(parameterNull).getStatusCode()),
                () -> assertEquals(400, client.sendGetRequest(parameterZero).getStatusCode(), "Incorrect status code when parameter zero: " + client.sendGetRequest(parameterZero).getStatusCode()),
                () -> {
                    JsonNode jsoneNodeParameterNull = objectMapper.readTree(client.sendGetRequest(parameterNull).getBody());
                    JsonNode jsoneNodeParameterZero = objectMapper.readTree(client.sendGetRequest(parameterZero).getBody());
                    assertEquals("Bad Request", jsoneNodeParameterNull.get("error").asText(), "Incorrect body: " + jsoneNodeParameterNull.get("error").asText());
                    assertEquals("Bad Request", jsoneNodeParameterZero.get("error").asText(), "Incorrect body: " + jsoneNodeParameterZero.get("error").asText());
                }
        );
    }
}
