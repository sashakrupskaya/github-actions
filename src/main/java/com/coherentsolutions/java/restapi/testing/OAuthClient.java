package com.coherentsolutions.java.restapi.testing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.*;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OAuthClient {

    private static OAuthClient instance;
    private final CloseableHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private String readToken;
    private String writeToken;

    private static final String TOKEN_ENDPOINT = TestConfig.getTokenEndpoint();
    private static final String USERNAME = TestConfig.getUserName();
    private static final String PASSWORD = TestConfig.getPassword();
    private static final String CONTENT_TYPE_VALUE = TestConfig.getContentTypeValue();
    private static final String GRANT_TYPE = TestConfig.getGrantType();

    private OAuthClient() {
        this.httpClient = HttpClients.createDefault();
        this.objectMapper = new ObjectMapper();
    }

    // Lazy initialization for singleton instance
    public static OAuthClient getInstance() {
        if (instance == null) {
            instance = new OAuthClient();
        }
        return instance;
    }
    // Gets the read token
    public String getReadToken() throws IOException {
        if (readToken == null) {
            readToken = fetchToken("read");
        }
        return readToken;
    }

    // Gets the write token
    public String getWriteToken() throws IOException {
        if (writeToken == null) {
            writeToken = fetchToken("write");
        }
        return writeToken;
    }

    /**
     Receive a token using the credentials, grant_type
     @param scope is for the token is requested ("read" or "write")
     @return access token
     @throws IOException when the HTTP request fails
     */
    private String fetchToken(String scope) throws IOException {
        HttpPost postRequest = new HttpPost(TOKEN_ENDPOINT);
        // Set header
        postRequest.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_VALUE);

        // Set basic authentication header
        String basicAuth = Base64.getEncoder().encodeToString((USERNAME + ":" + PASSWORD).getBytes(StandardCharsets.UTF_8));
        postRequest.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth);

        // Add form parameters for client credentials grant
        StringEntity requestBody = new StringEntity(GRANT_TYPE + scope);
        postRequest.setEntity(requestBody);

        // instead of try (CloseableHttpResponse response = httpClient.execute(postRequest))
        return httpClient.execute(postRequest, response -> {
            int statusCode = response.getCode();
            if (statusCode == HttpStatus.SC_OK) {
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JsonNode rootNode = objectMapper.readTree(responseBody);
                return rootNode.get("access_token").asText();
            } else {
                throw new IOException("Failed to fetch token. HTTP Status: " + statusCode);
            }
        });
    }

    // Send a GET request with the read token
    public String sendGetRequest(String url) throws IOException {

        HttpGet getRequest = new HttpGet(url);
        getRequest.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getReadToken());

        return httpClient.execute(getRequest, response -> {
            int statusCode = response.getCode();
            if (statusCode == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity());
            } else {
                throw new IOException("GET request failed. HTTP Status: " + statusCode);
            }
        });
    }

    // Send a POST, PUT, PATCH, or DELETE request with the write token
    public String sendWriteRequest(String url, String method, String body) throws IOException {
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
                request = new HttpDelete(url);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getWriteToken());
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        if (body != null) {
            ((HttpEntityContainer) request).setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        }

        return httpClient.execute(request, response -> {
            int statusCode = response.getCode();
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                return EntityUtils.toString(response.getEntity());
            } else {
                throw new IOException("Write request failed. HTTP Status: " + statusCode);
            }
        });
    }
}