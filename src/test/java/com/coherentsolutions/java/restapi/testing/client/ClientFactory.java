package com.coherentsolutions.java.restapi.testing.client;

public class ClientFactory {
    public static <T extends ClientInterface> ClientInterface createClient(Class<T> c) {
        if (c == RestAssuredClient.class) {
            return new RestAssuredClient();
        }
        if (c == ApacheClient.class) {
            return new ApacheClient();
        }
        return null;
    }
}
