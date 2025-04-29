package com.coherentsolutions.java.restapi.testing.client;

import com.coherentsolutions.java.restapi.testing.allure.AllureLogger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static io.restassured.RestAssured.given;

public class RestAssuredClient implements ClientInterface {
    private static RestAssuredClient instance;
    private final ObjectMapper objectMapper;
    private String readToken;
    private String writeToken;
    private static final String READ_SCOPE = "read";
    private static final String WRITE_SCOPE = "write";

    private static final String TOKEN_ENDPOINT = TestConfig.getTokenEndpoint();
    private static final String USERNAME2 = TestConfig.getUserName();
    private static final String PASSWORD = TestConfig.getPassword();
    private static final String GRANT_TYPE = TestConfig.getGrantType();
    private long readTokenExpiry;
    private long writeTokenExpiry;

    protected static final Logger logger = LoggerFactory.getLogger(RestAssuredClient.class);

    RestAssuredClient() {
        this.objectMapper = new ObjectMapper();
        RestAssured.useRelaxedHTTPSValidation();
    }

    // Lazy initialization for singleton instance
    public static synchronized RestAssuredClient getInstance() {
        if (instance == null) {
            instance = new RestAssuredClient();
        }
        return instance;
    }
    public  void refreshTokensAfterReset() {
        logger.info("Refreshing the tokens.");
        readToken = null;
        writeToken = null;
        readTokenExpiry = 0;
        writeTokenExpiry = 0;
    }

    private boolean isTokenValid(String token, long expiryTime) {
        return token != null && System.currentTimeMillis() < expiryTime;
    }

    // Get the read token
    public String getReadToken() throws IOException {
        if (!isTokenValid(readToken, readTokenExpiry)) {
            TokenResponse tokenResponse = fetchToken(READ_SCOPE);
            readToken = tokenResponse.getToken();
            readTokenExpiry = tokenResponse.getExpireTime();
        }
        return readToken;
    }

    // Get the write token
    public String getWriteToken() throws IOException {
        if (!isTokenValid(writeToken, writeTokenExpiry)) {
            TokenResponse tokenResponse = fetchToken(WRITE_SCOPE);
            writeToken = tokenResponse.getToken();
            writeTokenExpiry = tokenResponse.getExpireTime();
        }
        return writeToken;
    }

    @Override
    public void resetHttpClient() {
        logger.info("No need to reset the client for Rest Assured.");
    }

    /**
     Receive a token using the credentials, grant_type
     @param scope is for the token requested ("read" or "write")
     @return access token
     @throws IOException when the HTTP request fails
     */
    private TokenResponse fetchToken(String scope) throws IOException {
        // Set basic authentication header
        String basicAuth = Base64.getEncoder().encodeToString((USERNAME2 + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8));

        Response response = given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Basic " + basicAuth)
                .contentType("application/x-www-form-urlencoded")
                .formParam("grant_type", GRANT_TYPE)
                .formParam("scope", scope)
                .post(TOKEN_ENDPOINT);

        int statusCode = response.getStatusCode();
        if (statusCode == 200) {
            String responseBody = response.getBody().asString();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String token = rootNode.get("access_token").asText();
            int expiresIn = rootNode.has("expires_in") ? rootNode.get("expires_in").asInt() : 3600;
            long expireTime = System.currentTimeMillis() + (expiresIn * 1000L);
            return new TokenResponse(token, expireTime);
        } else {
            throw new IOException("Failed to fetch token. HTTP Status: " + statusCode);
        }
    }

    // Send a GET request with the read token
    @Step("Send GET request")
    public HttpResponse sendGetRequest(String url) throws IOException {

        Response response = given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + getReadToken())
                .get(url);

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();
        AllureLogger.attachPlaneText("Response Status Code", String.valueOf(statusCode));
        AllureLogger.attachJson("Response Body", responseBody);
        return new HttpResponse(statusCode, responseBody);
    }


    // Send a POST, PUT, PATCH, or DELETE request with the write token
    @Step("Send POST/PUT/PATCH/DELETE request")
    public HttpResponse sendWriteRequest(String url, String method, String body) throws IOException {
        Response response;
        var request = given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + getWriteToken())
                .contentType("application/json")
                .body(body);

        switch (method.toUpperCase()) {
            case "POST":
                response = request.post(url);
                break;
            case "PUT":
                response = request.put(url);
                break;
            case "PATCH":
                response = request.patch(url);
                break;
            case "DELETE":
                response = request.delete(url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();
        AllureLogger.attachPlaneText("Response Status Code", String.valueOf(statusCode));
        AllureLogger.attachJson("Response Body", responseBody);
        return new HttpResponse(statusCode, responseBody);
    }
    // Send a POST request to upload file with the write token
    @Step("Send POST request for upload a file")
    public HttpResponse uploadJsonFile(String url, File jsonFile) throws Exception {
        if (!jsonFile.exists()) {
            throw new IOException("No files provided for upload: " + jsonFile.getPath());
        }
        Response response = given()
                .filter(new AllureRestAssured())
                .header("Authorization", "Bearer " + getWriteToken())
                .multiPart("file", jsonFile, "application/json")
                .post(url);

        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();
        AllureLogger.attachPlaneText("Response Status Code", String.valueOf(statusCode));
        AllureLogger.attachJson("Response Body", responseBody);
        return new HttpResponse(statusCode, responseBody);
    }

    @Override
    public void shutdown() throws IOException {
        logger.info("No need to close the client for Rest Assured.");
    }

    public void restartDockerContainer() throws IOException, InterruptedException {
        logger.info("Starting to restart the container.");
        new ProcessBuilder("docker", "stop", "nostalgic_haibt").start().waitFor();
        new ProcessBuilder("docker", "start", "nostalgic_haibt").start().waitFor();
        logger.info("Docker container restarted successfully.");
        Thread.sleep(9000);
    }
}