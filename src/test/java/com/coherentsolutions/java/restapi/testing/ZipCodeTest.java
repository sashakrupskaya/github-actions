package com.coherentsolutions.java.restapi.testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ZipCodeTest {
    private OAuthClient client;

    private static final String ZIP_CODES_URL = TestConfig.getZipCodesURL();
    private static final String ZIP_CODES_EXPAND_URL = TestConfig.getZipCodesExpandURL();
    private static final String USERS_URL = TestConfig.getUsersURL();

    @BeforeEach
    public void setUp() {
        client = OAuthClient.getInstance();
    }
    //need to turn off and turn on the server for now
    //need to return to the default data

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
                () -> assertEquals("[\"12345\",\"23456\",\"ABCDE\",\"9\",\"qwerty\",\"zxcv\"]", addDuplicatedZipCodes.getBody(), "Expected response body should contain only unique zip codes.")
        );
    }
    @Test
    public void addDuplicatedAlreadyUsedZipCodes() throws IOException, InterruptedException {
        HttpResponse getZipCodes = client.sendGetRequest(ZIP_CODES_URL);
        assertTrue(getZipCodes.getBody().contains("23456"), "Existing zip code.");
        assertFalse(getZipCodes.getBody().contains("bnb"), "Not existing zip code.");

        HttpResponse postUsers = client.sendWriteRequest(USERS_URL, "POST","{\n" +
                    "  \"age\": 255,\n" +
                    "  \"name\": \"Tester22\",\n" +
                    "  \"sex\": \"MALE\",\n" +
                    "  \"zipCode\": \"23456\"\n" + "}"
        );

        HttpResponse getUsers = client.sendGetRequest(USERS_URL);
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
                () -> assertEquals(500, response.getStatusCode(), "Zip code is required."),
                () -> assertEquals("Zip code is required.", response.getBody())
        );
    }
    @Test
    public void sendIntToAddNewZipCode() throws IOException {
        HttpResponse response = client.sendWriteRequest(ZIP_CODES_EXPAND_URL, "POST", "[777]");
        assertAll(
                "Grouped Assertions of sendIntToAddNewZipCode",
                () -> assertEquals(500, response.getStatusCode(), "Invalid format of zip code."),
                () -> assertEquals("Invalid format of zip code.", response.getBody(), "String to add is only available.")
        );
    }

    @Test
    public void sendNullToAddNewZipCode() throws IOException {
        HttpResponse response = client.sendWriteRequest(ZIP_CODES_EXPAND_URL, "POST", "[null]");
        assertAll(
                "Grouped Assertions of sendIntToAddNewZipCode",
                () -> assertEquals(500, response.getStatusCode(), "Invalid format of zip code."),
                () -> assertEquals("Invalid format of zip code.", response.getBody(), "String to add is only available.")
        );
    }
    @Test
    public void sendEmptyObjectToAddNewZipCode() throws IOException {
        HttpResponse response = client.sendWriteRequest(ZIP_CODES_EXPAND_URL, "POST", "{}");
         assertEquals(400, response.getStatusCode(), "Invalid format is sent.");
    }
}
