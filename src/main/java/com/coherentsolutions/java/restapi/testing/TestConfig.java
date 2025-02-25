package com.coherentsolutions.java.restapi.testing;

import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;

/**
 * TestConfig is responsible for loading configuration properties from a file.
 * It allows accessing configuration values.
 */
public class TestConfig {
    private static Properties config = new Properties();
    protected static final Logger logger = LoggerFactory.getLogger(TestConfig.class);

    /**
     * initializes file path, key: value
     * loads the configuration properties from a file
     */
    static {
        String propsFilePath = System.getProperty("configFilePath", "src/main/resources/conf.properties");
        try (FileInputStream input = new FileInputStream(propsFilePath)) {
            config.load(input);
            logger.info("Properties loaded successfully from " + propsFilePath);
        } catch (IOException e) {
            logger.error("Error reading properties file from " + propsFilePath + ": " + e.getMessage());
            throw new IllegalStateException("Error reading properties file.", e);
        }
    }
    /**
     * Retrieves the property value associated with the given key.
     * @param key the property key to look up.
     * @return the property value.
     * @throws NullPointerException if the key is null.
     */
    public static String getProperty(String key) {
        if (key == null) {
            logger.info("Parameters are not set.");
            throw new NullPointerException("Parameters are not set");
        }
        return config.getProperty(key);
    }
    public static String getTokenEndpoint() {
        return getProperty("tokenEndpoint");
    }

    public static String getUserName() {
        return getProperty("userName");
    }
    public static String getPassword() {
        return getProperty("userPassword");
    }
    public static String getGrantType() {
        return getProperty("grantType");
    }
    public static String getZipCodesURL() {
        return getProperty("zipCodesURL");
    }
    public static String getZipCodesExpandURL() {return getProperty("zipCodesExpandURL");}
    public static String getUsersURL() {return getProperty("usersURL");}
}