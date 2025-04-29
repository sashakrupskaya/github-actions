package com.coherentsolutions.java.restapi.testing.test;

import com.coherentsolutions.java.restapi.testing.client.TestConfig;

import org.junit.jupiter.api.*;


import static org.junit.jupiter.api.Assertions.*;

public class DataTest {
        @Test
    public void testGetToken() {
        String value = TestConfig.getTokenEndpoint();
        assertNotNull(value);
        assertEquals("http://localhost:49000/oauth/token", value);
    }
    @Test
    public void testGetCredentials() {
        String value = TestConfig.getGrantType();
        assertNotNull(value);
        assertEquals("client_credentials", value);
    }
    @Test
    public void testGetZipCodesURL() {
        String value = TestConfig.getZipCodesURL();
        assertNotNull(value);
        assertEquals("http://localhost:49000/zip-codes", value);
    }
    @Test
    public void testGetZipCodesExpandURL() {
        String value = TestConfig.getZipCodesExpandURL();
        assertNotNull(value);
        assertEquals("http://localhost:49000/zip-codes/expand",value);
    }
    @Test
    public void testGet() {
        String value = TestConfig.getUsersURL();
        assertNotNull(value);
        assertEquals("http://localhost:49000/users", value);
    }
}
