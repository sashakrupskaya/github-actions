package com.coherentsolutions.java.restapi.testing.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TokenResponse {
    private final String token;
    private final long expireTime;
    protected static final Logger logger = LoggerFactory.getLogger(TokenResponse.class);

    public TokenResponse(String token, long expireTime) {
        this.token = token;
        this.expireTime = expireTime;
    }
    public String getToken() {
        if (token == null) {
            logger.error("Attempt to access an uninitialized token.");
            throw new IllegalStateException("Token has not been initialized.");
        }
        return token;
    }
    public long getExpireTime() {
        return expireTime;
    }
}
