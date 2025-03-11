package com.coherentsolutions.java.restapi.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ZipCodeTest {
    private OAuthClient client;

    private static final String ZIP_CODES_URL = TestConfig.getZipCodesURL();
    private static final String ZIP_CODES_EXPAND_URL = TestConfig.getZipCodesExpandURL();
    private static final String USERS_URL = TestConfig.getUsersURL();
    private final ObjectMapper objectMapper = new ObjectMapper();
    protected static final Logger logger = LoggerFactory.getLogger(ZipCodeTest.class);


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
    public void getAvailableZipCodes() throws IOException {
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        assertAll(
                "Grouped Assertions of getAvailableZipCodes",
                () -> assertEquals(200, getZipCodes.getStatusCode(), "200 status code is a valid code."),
                () -> assertEquals("[\"12345\",\"23456\",\"ABCDE\"]", getZipCodes.getBody(), "Expected response body should be [\"12345\",\"23456\",\"ABCDE\"]")
        );
    }
    @Test
    public void addNewZipCodes() throws IOException {
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        assertFalse(getZipCodes.getBody().contains("9"), "Not existing code.");
        assertFalse(getZipCodes.getBody().contains("qwerty"), "Not existing code.");
        HttpResponse addNewZipCodes = client.sendWriteRequest(ZIP_CODES_EXPAND_URL, "POST", "[\"9\",\"qwerty\"]");
        assertAll(
                "Grouped Assertions of addNewZipCodes",
                () -> assertEquals(201, addNewZipCodes.getStatusCode(), "201 is expected status code."),
                () -> assertTrue(addNewZipCodes.getBody().contains("9"), "Expected response body should contain \"9\"" + addNewZipCodes.getBody()),
                () -> assertTrue(addNewZipCodes.getBody().contains("qwerty"), "Expected response body should contain \"qwerty\"" + addNewZipCodes.getBody())
        );
    }
    @Test
    public void addDuplicatedZipCodes() throws IOException {
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        assertTrue(getZipCodes.getBody().contains("12345"), "Existing code.");
        assertFalse(getZipCodes.getBody().contains("zxcv"), "Not existing zip code");
        HttpResponse addDuplicatedZipCodes = client.sendWriteRequest(ZIP_CODES_EXPAND_URL, "POST", "[\"12345\",\"zxcv\"]");//use duplicates among the codes
        assertAll(
                "Grouped Assertions of addDuplicatedZipCodes",
                () -> assertEquals(201, addDuplicatedZipCodes.getStatusCode(), "201 is expected status code."),
                () -> assertTrue(addDuplicatedZipCodes.getBody().contains("zxcv")),
                () -> assertEquals("[\"12345\",\"23456\",\"ABCDE\",\"zxcv\"]", addDuplicatedZipCodes.getBody(), "Expected response body should contain only unique zip codes.")
        );
    }
    @Test
    public void addDuplicatedAlreadyUsedZipCodes() throws IOException, InterruptedException {
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        logger.info("List of zip codes: " + getZipCodes.getBody());
        assertTrue(getZipCodes.getBody().contains("23456"), "Existing zip code.");
        assertFalse(getZipCodes.getBody().contains("bnb"), "Not existing zip code.");

        HttpResponse postUsers = client.sendWriteRequest(USERS_URL, "POST","{\n" +
                    "  \"age\": 255,\n" +
                    "  \"name\": \"Tester22\",\n" +
                    "  \"sex\": \"MALE\",\n" +
                    "  \"zipCode\": \"23456\"\n" + "}"
        );

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
        logger.info("The user with credentials: " + getUsers.getBody());
        assertEquals(200,getUsers.getStatusCode(),"200 is valid status code.");
        assertTrue(getUsers.getBody().contains("23456"), "Zip code is assigned to user.");

        HttpResponse addDuplicatedInUseZipCodes = client.sendWriteRequest(ZIP_CODES_EXPAND_URL, "POST", "[\"23456\",\"bnb\"]");
        assertTrue(addDuplicatedInUseZipCodes.getBody().contains("bnb"));
        assertFalse(addDuplicatedInUseZipCodes.getBody().contains("23456"), "Zip code should not be added because it is in use.");
    }
    @Test
    public void sendEmptyArrayToAddNewZipCode() throws IOException {
        HttpResponse response = client.sendWriteRequest(ZIP_CODES_EXPAND_URL, "POST", "[]");
        assertAll(
                "Grouped Assertions of sendEmptyArrayToAddNewZipCode",
                () -> assertEquals(400, response.getStatusCode(), "Bad request. Status code is incorrect."),
                () -> assertNotEquals("[\"12345\",\"23456\",\"ABCDE\"]", response.getBody(), "In case of Bad Request the list should not be shown."),
                () -> {
                    JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    assertTrue(jsonNode.has("timestamp"), "The timestamp field is missed.");
                    assertTrue(jsonNode.get("timestamp").asLong() > 0, "Timestamp is incorrect.");
                    assertEquals(400, jsonNode.get("status").asInt(), "Status code is incorrect." );
                    assertEquals("Bad Request", jsonNode.get("error").asText(), "Error message is incorrect.");
                    assertEquals("", jsonNode.get("message").asText(), "Message field is incorrect.");
                    assertEquals("/zip-codes/expand", jsonNode.get("path").asText(), "Path is incorrect.");
                }
        );
    }
    @Test
    public void sendIntToAddNewZipCode() throws IOException {
        HttpResponse response = client.sendWriteRequest(ZIP_CODES_EXPAND_URL, "POST", "[777]");
        assertAll(
                "Grouped Assertions of sendIntToAddNewZipCode",
                () -> assertEquals(400, response.getStatusCode(), "Bad request. Invalid format."),
                () -> assertNotEquals("[\"12345\",\"23456\",\"ABCDE\"]", response.getBody(), "In case of Bad Request the list should not be shown."),
                () -> {
                    JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    assertTrue(jsonNode.has("timestamp"), "The timestamp field is missed.");
                    assertTrue(jsonNode.get("timestamp").asLong() > 0, "Timestamp is incorrect.");
                    assertEquals(400, jsonNode.get("status").asInt(), "Status code is incorrect." );
                    assertEquals("Bad Request", jsonNode.get("error").asText(), "Error message is incorrect.");
                    assertEquals("", jsonNode.get("message").asText(), "Message field is incorrect.");
                    assertEquals("/zip-codes/expand", jsonNode.get("path").asText(), "Path is incorrect.");
                }
        );
    }

    @Test
    public void sendNullToAddNewZipCode() throws IOException {
        HttpResponse response = client.sendWriteRequest(ZIP_CODES_EXPAND_URL, "POST", "[null]");
        assertAll(
                "Grouped Assertions of sendIntToAddNewZipCode",
                () -> assertEquals(400, response.getStatusCode(), "Bad request. Invalid format."),
                () -> assertNotEquals("[\"12345\",\"23456\",\"ABCDE\"]", response.getBody(), "In case of Bad Request the list should not be shown."),
                () -> {
                    JsonNode jsonNode = objectMapper.readTree(response.getBody());
                    assertTrue(jsonNode.has("timestamp"), "The timestamp field is missed.");
                    assertTrue(jsonNode.get("timestamp").asLong() > 0, "Timestamp is incorrect.");
                    assertEquals(400, jsonNode.get("status").asInt(), "Status code is incorrect." );
                    assertEquals("Bad Request", jsonNode.get("error").asText(), "Error message is incorrect.");
                    assertEquals("", jsonNode.get("message").asText(), "Message field is incorrect.");
                    assertEquals("/zip-codes/expand", jsonNode.get("path").asText(), "Path is incorrect.");
                }
        );
    }
    @Test
    public void sendEmptyObjectToAddNewZipCode() throws IOException {
        HttpResponse response = client.sendWriteRequest(ZIP_CODES_EXPAND_URL, "POST", "{}");
         assertAll(
                 "Grouped Assertions of sendEmptyObjectToAddNewZipCode",
                 () -> assertEquals(400, response.getStatusCode(), "Bad request. Array of strings should be sent."),
                 () -> assertNotEquals("[\"12345\",\"23456\",\"ABCDE\"]", response.getBody(), "In case of Bad Request the list should not be shown."),
                 () -> {
                     JsonNode jsonNode = objectMapper.readTree(response.getBody());
                     assertTrue(jsonNode.has("timestamp"), "The timestamp field is missed.");
                     assertTrue(jsonNode.get("timestamp").asLong() > 0, "Timestamp is incorrect.");
                     assertEquals(400, jsonNode.get("status").asInt(), "Status code is incorrect." );
                     assertEquals("Bad Request", jsonNode.get("error").asText(), "Error message is incorrect.");
                     assertEquals("", jsonNode.get("message").asText(), "Message field is incorrect.");
                     assertEquals("/zip-codes/expand", jsonNode.get("path").asText(), "Path is incorrect.");
                 }
         );
    }
}
