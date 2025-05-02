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

import java.io.File;
import java.io.IOException;
import java.util.Optional;




public class UploadUsersTest {
    private ClientInterface client;
    private static final String USERS_URL = TestConfig.getUsersURL();
    private static final String USERS_UPLOAD_URL = TestConfig.getUsersUploadURL();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    protected static final Logger logger = LoggerFactory.getLogger(UploadUsersTest.class);
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
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Upload a user - API Test")
    @Description("Uploads a user and verifies status code and response")
    public void testUploadOneUser() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case1File = new File("src/test/resources/case1.json");
        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case1File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(201, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"age\":26,\"sex\":\"FEMALE\",\"zipCode\":\"12345\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertEquals("Number of users = 1", uploadUser.getBody(), "The response body is not correct. Number of uploaded users: " + uploadUser.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Upload a user, when the code is in one row - API Test")
    @Description("Uploads a user and verifies status code and response")
    public void testUploadOneUserWrittenInOneRowInsideJsonFile() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case11File = new File("src/test/resources/case1.1.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case11File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(201, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"age\":26,\"sex\":\"FEMALE\",\"zipCode\":\"12345\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertEquals("Number of users = 1", uploadUser.getBody(), "The response body is not correct. Number of uploaded users: " + uploadUser.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Upload a user, when fields are reordered inside .json file - API Test")
    @Description("Uploads a user and verifies status code and response")
    public void testUploadOneUserWithChangedFieldsOrder() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case12File = new File("src/test/resources/case1.2.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case12File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(201, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"age\":26,\"sex\":\"FEMALE\",\"zipCode\":\"12345\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertEquals("Number of users = 1", uploadUser.getBody(), "The response body is not correct. Number of uploaded users: " + uploadUser.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Upload a user, when no user is created before uploading - API Test")
    @Description("Uploads a user and verifies status code and response")
    public void testUploadUserNoUsersCreatedPreviously() throws Exception {
        File case1File = new File("src/test/resources/case1.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case1File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(201, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"age\":26,\"sex\":\"FEMALE\",\"zipCode\":\"12345\"}]", getUsers.getBody(), "The body is incorrect. Get users: " + getUsers.getBody()),
                () -> assertEquals("Number of users = 1", uploadUser.getBody(), "The response body is not correct. Number of uploaded users: " + uploadUser.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Upload several users - API Test")
    @Description("Uploads several users and verifies status code and response")
    public void testUploadSeveralUsers() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case2File = new File("src/test/resources/case2.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case2File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(201, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"age\":26,\"sex\":\"FEMALE\",\"zipCode\":\"12345\"},{\"name\":\"Tester28\",\"age\":28,\"sex\":\"FEMALE\",\"zipCode\":\"ABCDE\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertEquals("Number of users = 2", uploadUser.getBody(), "The response body is not correct. Number of uploaded users: " + uploadUser.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Upload a user when several users were created previously - API Test")
    @Description("Uploads a user and verifies status code and response")
    public void testUploadOneUserWhenTwoCreatedPreviously() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);
        User userToReplace1 = new User(Optional.of(30), "Tester30", Sex.FEMALE, Optional.of("12345"));
        createUser(userToReplace1);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        File case1File = new File("src/test/resources/case1.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL,  case1File);
        logger.info("User is uploaded: " + uploadUser.getStatusCode());
        logger.info("Body: " + uploadUser.getBody());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(201, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"age\":26,\"sex\":\"FEMALE\",\"zipCode\":\"12345\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertEquals("Number of users = 1", uploadUser.getBody(), "The response body is not correct. Number of uploaded users: " + uploadUser.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("22")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Upload a user with no Age - API Test")
    @Description("Uploads a user and verifies status code and response")
    public void testUploadUserWithoutAge() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case15File = new File("src/test/resources/case15.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case15File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(201, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"sex\":\"FEMALE\",\"zipCode\":\"12345\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertEquals("Number of users = 1", uploadUser.getBody(), "The response body is not correct. Number of uploaded users: " + uploadUser.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("22")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Upload a user with no Zip code - API Test")
    @Description("Uploads a user and verifies status code and response")
    public void testUploadUserWithoutZipCode() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case16File = new File("src/test/resources/case16.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case16File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(201, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"age\":26,\"sex\":\"FEMALE\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertEquals("Number of users = 1", uploadUser.getBody(), "The response body is not correct. Number of uploaded users: " + uploadUser.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("22")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Upload a user with no Age and Zip code - API Test")
    @Description("Uploads a user and verifies status code and response")
    public void testUploadUserWithoutAgeAndZipCode() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case17File = new File("src/test/resources/case17.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case17File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(201, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester26\",\"sex\":\"FEMALE\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertEquals("Number of users = 1", uploadUser.getBody(), "The response body is not correct. Number of uploaded users: " + uploadUser.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to upload an empty .json file - API Test")
    @Description("Gets 400 status code and verifies response")
    public void testUploadAnEmptyFile() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case3File = new File("src/test/resources/case3.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case3File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(400, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertTrue( uploadUser.getBody().contains("Can't parse file with users"), "The response body is not correct." + uploadUser.getBody().contains("Can't parse file with users"))
        );
    }
    @Test
    @Tag("regression")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to upload a .json file, when a coma is missed - API Test")
    @Description("Gets 400 status code and verifies response")
    public void testUploadUserWithMissedComaInJson() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case19File = new File("src/test/resources/case19.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case19File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(400, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertTrue( uploadUser.getBody().contains("Can't parse file with users"), "The response body is not correct." + uploadUser.getBody().contains("Can't parse file with users"))
        );
    }
    @Test
    @Tag("smoke")
    @Issue("23")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to upload a .txt file - API Test")
    @Description("Gets 400 status code and verifies response")
    public void testUploadNonJsonFile() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case18File = new File("src/test/resources/case18.txt");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case18File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(400, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody()),
                () -> assertTrue( uploadUser.getBody().contains("Can't parse file with users"), "The response body is not correct." + uploadUser.getBody().contains("Can't parse file with users"))
        );
    }
    @Test
    @Tag("smoke")
    @Issue("20")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to upload a user, when Zip code is incorrect - API Test")
    @Description("Gets 424 status code and verifies response")
    public void testUploadUserWithIncorrectZipCode() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        File case4File = new File("src/test/resources/case4.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL,  case4File);
        logger.info("User is uploaded: " + uploadUser.getStatusCode());
        logger.info("Body: " + uploadUser.getBody());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(424, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Specified zip code is not available"), "The response body is incorrect: " + uploadUser.getBody().contains("Specified zip code is not available")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("20")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to upload two users, when Zip code is incorrect for one user - API Test")
    @Description("Gets 424 status code and verifies response")
    public void testUploadTwoUsersWithIncorrectZipCode() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        File case6File = new File("src/test/resources/case6.json");


        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL,  case6File);
        logger.info("User is uploaded: " + uploadUser.getStatusCode());
        logger.info("Body: " + uploadUser.getBody());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(424, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Specified zip code is not available"), "The response body is incorrect: " + uploadUser.getBody().contains("Specified zip code is not available")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("20")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to upload two users, when Zip code is incorrect for both of them - API Test")
    @Description("Gets 424 status code and verifies response")
    public void testUploadTwoUsersWithIncorrectTwoZipCodes() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        File case7File = new File("src/test/resources/case7.json");


        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL,  case7File);
        logger.info("User is uploaded: " + uploadUser.getStatusCode());
        logger.info("Body: " + uploadUser.getBody());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(424, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Specified zip code is not available"), "The response body is incorrect: " + uploadUser.getBody().contains("Specified zip code is not available")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("20")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to upload a user, when Zip code is unavailable - API Test")
    @Description("Gets 424 status code and verifies response")
    public void testUploadUserWithUnavailableZipCode() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        File case5File = new File("src/test/resources/case5.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL,  case5File);
        logger.info("User is uploaded: " + uploadUser.getStatusCode());
        logger.info("Body: " + uploadUser.getBody());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(424, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Specified zip code is not available"), "The response body is incorrect: " + uploadUser.getBody().contains("Specified zip code is not available")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("20")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to upload two users, when Zip code is unavailable for one user - API Test")
    @Description("Gets 424 status code and verifies response")
    public void testUploadTwoUsersWithUnavailableZipCode() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        File case8File = new File("src/test/resources/case8.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL,  case8File);
        logger.info("User is uploaded: " + uploadUser.getStatusCode());
        logger.info("Body: " + uploadUser.getBody());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(424, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Specified zip code is not available"), "The response body is incorrect: " + uploadUser.getBody().contains("Specified zip code is not available")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("20")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to upload two users, when Zip code is unavailable for both of them - API Test")
    @Description("Gets 424 status code and verifies response")
    public void testUploadTwoUsersWithTheSameUnavailableZipCodeForTwo() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        File case9File = new File("src/test/resources/case9.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL,  case9File);
        logger.info("User is uploaded: " + uploadUser.getStatusCode());
        logger.info("Body: " + uploadUser.getBody());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(424, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Specified zip code is not available"), "The response body is incorrect: " + uploadUser.getBody().contains("Specified zip code is not available")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("20")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to upload two users, when two different Zip codes are unavailable - API Test")
    @Description("Gets 424 status code and verifies response")
    public void testUploadTwoUsersWithTwoDifferentUnavailableZipCodes() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);
        User userToReplace1 = new User(Optional.of(30), "Tester30", Sex.FEMALE, Optional.of("12345"));
        createUser(userToReplace1);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        File case10File = new File("src/test/resources/case10.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL,  case10File);
        logger.info("User is uploaded: " + uploadUser.getStatusCode());
        logger.info("Body: " + uploadUser.getBody());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(424, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Specified zip code is not available"), "The response body is incorrect: " + uploadUser.getBody().contains("Specified zip code is not available")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}, {\"name\":\"Tester30\",\"age\":30,\"sex\":\"MALE\",\"zipCode\":\"12345\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("regression")
    @Issue("20")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Try to upload two users, when one Zip code is incorrect another is unavailable - API Test")
    @Description("Gets 424 status code and verifies response")
    public void testUploadTwoUsersWithOneIncorrectAnotherUnavailableZipCode() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users: " + getUsers.getBody());

        File case11File = new File("src/test/resources/case11.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL,  case11File);
        logger.info("User is uploaded: " + uploadUser.getStatusCode());
        logger.info("Body: " + uploadUser.getBody());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(424, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Specified zip code is not available"), "The response body is incorrect: " + uploadUser.getBody().contains("Specified zip code is not available")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("21")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to upload a user with no Name - API Test")
    @Description("Gets 409 status code and verifies response")
    public void testUploadOneUserWithoutName() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case12File = new File("src/test/resources/case12.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case12File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(409, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + uploadUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("21")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to upload two users, when one is with no Name - API Test")
    @Description("Gets 409 status code and verifies response")
    public void testUploadTwoUsersWhereOneWithoutName() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case121File = new File("src/test/resources/case12.1.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case121File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(409, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + uploadUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("21")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to upload two users, when both are with no Names - API Test")
    @Description("Gets 409 status code and verifies response")
    public void testUploadTwoUsersWhereTwoWithoutNames() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case122File = new File("src/test/resources/case12.2.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case122File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(409, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + uploadUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("21")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to upload a user with no Sex - API Test")
    @Description("Gets 409 status code and verifies response")
    public void testUploadOneUserWithoutSex() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case13File = new File("src/test/resources/case13.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case13File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(409, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + uploadUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("21")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to upload two users, when one is with no Sex - API Test")
    @Description("Gets 409 status code and verifies response")
    public void testUploadTwoUsersWhereOneWithoutSex() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case131File = new File("src/test/resources/case13.1.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case131File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(409, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + uploadUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("21")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to upload two users, when both are with no Sex - API Test")
    @Description("Gets 409 status code and verifies response")
    public void testUploadTwoUsersWhereBothWithoutSex() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case132File = new File("src/test/resources/case13.2.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case132File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(409, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + uploadUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
    @Test
    @Tag("smoke")
    @Issue("21")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Try to upload a user, when Name and Sex are missed - API Test")
    @Description("Gets 409 status code and verifies response")
    public void testUploadOneUserWithoutNameAndSex() throws Exception {
        User userToReplace = new User(Optional.of(INITIAL_AGE), INITIAL_NAME, INITIAL_SEX, Optional.of(INITIAL_ZIP_CODE));
        createUser(userToReplace);

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("Users before upload: " + getUsers.getBody());

        File case14File = new File("src/test/resources/case14.json");

        HttpResponse uploadUser = client.uploadJsonFile(USERS_UPLOAD_URL, case14File);
        logger.info("User upload response: " + uploadUser.getStatusCode());

        HttpResponse getUsers1 = client.sendGetRequest(USERS_URL);
        logger.info(uploadUser.getBody());

        assertAll(
                "Grouped Assertions of uploadUser",
                () -> assertEquals(409, uploadUser.getStatusCode(), "Status code is incorrect: " + uploadUser.getStatusCode()),
                () -> assertTrue(uploadUser.getBody().contains("Some required fields are missed"), "The response body is incorrect: " + uploadUser.getBody().contains("Some required fields are missed")),
                () -> assertEquals("[{\"name\":\"Tester25\",\"age\":25,\"sex\":\"MALE\",\"zipCode\":\"23456\"}]", getUsers1.getBody(), "The body is incorrect. Get users: " + getUsers1.getBody())
        );
    }
}

