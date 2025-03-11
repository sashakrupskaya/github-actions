package com.coherentsolutions.java.restapi.testing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
    private final int statusCode;
    private final String body;
    protected static final Logger logger = LoggerFactory.getLogger(HttpResponse.class);


    public HttpResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        logger.info("Status code is returning.");
        return statusCode;
    }

    public String getBody() {
        if (body == null) {
            logger.info("Body is incorrect.");
            throw new NullPointerException("Body is null.");
        }
        logger.info("Body is returning.");
        return body;
    }

    @Override
    public String toString() {
        return "HttpResponse {" +
                "statusCode=" + statusCode + ", body='" + body + '\'' + '}';
    }
}
