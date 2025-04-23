package com.coherentsolutions.java.restapi.testing.client;

import com.coherentsolutions.java.restapi.testing.allure.AllureLogger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import org.apache.hc.client5.http.classic.methods.*;

import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
//import org.apache.hc.core5.http.HttpResponse;
import com.coherentsolutions.java.restapi.testing.client.HttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OAuthClient {

    private static OAuthClient instance;
    private CloseableHttpClient httpClient;
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

    protected static final Logger logger = LoggerFactory.getLogger(OAuthClient.class);

    private OAuthClient() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }

    // Lazy initialization for singleton instance
    public static synchronized OAuthClient getInstance() {
        if (instance == null) {
            instance = new OAuthClient();
        }
        return instance;
    }
    public void resetHttpClient() {
        try {
            if (httpClient != null) {
                httpClient.close();
            }
            this.httpClient = HttpClients.createDefault();
            logger.info("HttpClient reset successfully.");

        } catch (IOException e) {
            logger.error("Error while resetting: " + e.getMessage());
        }
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

    /**
     Receive a token using the credentials, grant_type
     @param scope is for the token requested ("read" or "write")
     @return access token
     @throws IOException when the HTTP request fails
     */
    private TokenResponse fetchToken(String scope) throws IOException {
        HttpPost postRequest = new HttpPost(TOKEN_ENDPOINT);
        // Set header
        postRequest.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        // Set basic authentication header
        String basicAuth = Base64.getEncoder().encodeToString((USERNAME2 + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8));
        postRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth);

        // Add form parameters for client credentials grant
        StringEntity requestBody = new StringEntity(String.format("grant_type=%s&scope=%s", GRANT_TYPE, scope));
        postRequest.setEntity(requestBody);

        return httpClient.execute(postRequest, response -> {
            int statusCode = response.getCode();
                        if (statusCode == HttpStatus.SC_OK) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JsonNode rootNode = objectMapper.readTree(responseBody);
                String token = rootNode.get("access_token").asText();
                int expiresIn = rootNode.has("expires_in") ? rootNode.get("expires_in").asInt() : 3600;
                long expireTime = System.currentTimeMillis() + (expiresIn * 1000L);
                return new TokenResponse(token, expireTime);
            } else {
                throw new IOException("Failed to fetch token. HTTP Status: " + statusCode);
            }
        });
    }

    // Send a GET request with the read token
    @Step("Send GET request")
    public HttpResponse sendGetRequest(String url) throws IOException {

        HttpGet getRequest = new HttpGet(url);
        getRequest.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getReadToken());

        return httpClient.execute(getRequest, response -> {
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            AllureLogger.attachText("Response Status Code", String.valueOf(statusCode));
            AllureLogger.attachJson("Response Body", responseBody);
            return new HttpResponse(statusCode, responseBody);
        });
    }


    // Send a POST, PUT, PATCH, or DELETE request with the write token
    @Step("Send POST/PUT/PATCH/DELETE request")
    public HttpResponse sendWriteRequest(String url, String method, String body) throws IOException {
        HttpUriRequestBase request;
        switch (method.toUpperCase()) {
            case "POST":
                request = new HttpPost(url);
                break;
            case "PUT":
                request = new HttpPut(url);
                break;
            case "PATCH":
                request = new HttpPatch(url);
                break;
            case "DELETE":
                request = (body != null && !body.isEmpty()) ? new HttpDeleteWithBody(url) : new HttpDelete(url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getWriteToken());
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        if (body != null && !body.isEmpty()) {
            ((HttpEntityContainer) request).setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        }

        return httpClient.execute(request, response -> {
            int statusCode = response.getCode();
            String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8) : null;
            AllureLogger.attachText("Response Status Code", String.valueOf(statusCode));
            AllureLogger.attachJson("Response Body", responseBody);
            return new HttpResponse(statusCode, responseBody);
        });
    }
    // Send a POST request to upload file with the write token
    @Step("Send POST request for upload a file")
    public HttpResponse uploadJsonFile(String url, File jsonFile) throws Exception {
        if (!jsonFile.exists()) {
            throw new IOException("No files provided for upload: " + jsonFile.getPath());
        }
        HttpPost request = new HttpPost(url);
        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getWriteToken());

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setContentType(ContentType.MULTIPART_FORM_DATA);
        builder.addPart("file", new FileBody(jsonFile, ContentType.APPLICATION_JSON));

        request.setEntity(builder.build());

        return httpClient.execute(request, response -> {
            int statusCode = response.getCode();
            String responseBody = response.getEntity() != null ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8) : null;
            AllureLogger.attachText("Response Status Code", String.valueOf(statusCode));
            AllureLogger.attachJson("Response Body", responseBody);
            return new HttpResponse(statusCode, responseBody);
        });
    }
    public void shutdown() throws IOException {
        httpClient.close();
        logger.info("The client is closed.");
    }
    public void restartDockerContainer() throws IOException, InterruptedException {
        logger.info("Starting to restart the container.");
        new ProcessBuilder("docker", "stop", "nostalgic_haibt").start().waitFor();
        new ProcessBuilder("docker", "start", "nostalgic_haibt").start().waitFor();
        logger.info("Docker container restarted successfully.");
        Thread.sleep(9000);
    }
}