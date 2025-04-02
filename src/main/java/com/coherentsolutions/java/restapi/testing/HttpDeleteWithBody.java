package com.coherentsolutions.java.restapi.testing;

import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;

import java.net.URI;

public class HttpDeleteWithBody extends HttpUriRequestBase {
    private static final String METHOD_NAME = "DELETE";
    public HttpDeleteWithBody(final String uri) {
        super(METHOD_NAME, URI.create(uri));
    }
    @Override
    public String getMethod() {
        return METHOD_NAME;
    }
}
