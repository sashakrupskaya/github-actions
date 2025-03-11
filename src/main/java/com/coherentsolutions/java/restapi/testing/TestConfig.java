package com.coherentsolutions.java.restapi.testing;

import io.github.cdimascio.dotenv.Dotenv;
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
    private static final Dotenv dotenv = Dotenv.load();
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
            logger.error("Parameters are not set.");
            throw new NullPointerException("Parameters are not set");
        }
        String envValue = dotenv.get(key.toUpperCase());
        if (envValue != null) {
            return envValue;
        }
        return config.getProperty(key);
    }

    public static String getTokenEndpoint() {
        if (getProperty("TOKEN_ENDPOINT") == null) {
            logger.error("TOKEN_ENDPOINT is null.");
            throw new NullPointerException("TOKEN_ENDPOINT is null.");
        }
        return getProperty("TOKEN_ENDPOINT");
    }
    public static String getUserName() {
        if (getProperty("USERNAME2") == null) {
            logger.error("USERNAME2 is null.");
            throw new NullPointerException("USERNAME2 is null");
        }
        return getProperty("USERNAME2");
    }
    public static String getPassword() {
        if (getProperty("PASSWORD") == null) {
            logger.error("PASSWORD is null.");
            throw new NullPointerException("PASSWORD is null.");
        }
        return getProperty("PASSWORD");
    }
    public static String getGrantType() {
        if (getProperty("grantType") == null) {
            logger.error("grantType is null.");
            throw new NullPointerException("grantType is null.");
        }
        return getProperty("grantType");
    }
    public static String getZipCodesURL() {
        if (getProperty("zipCodesURL") == null) {
            logger.error("zipCodesURL is null.");
            throw new NullPointerException("zipCodesURL is null.");
        }
        return getProperty("zipCodesURL");
    }
    public static String getZipCodesExpandURL() {
        if (getProperty("zipCodesExpandURL") == null) {
            logger.error("zipCodesExpandURL is null.");
            throw new NullPointerException("zipCodesExpandURL is null.");
        }
        return getProperty("zipCodesExpandURL");}
    public static String getUsersURL() {
        if (getProperty("usersURL") == null) {
            logger.error("usersURL is null.");
            throw new NullPointerException("usersURL.");
        }
        return getProperty("usersURL");}
}