package com.coherentsolutions.java.restapi.testing.client;

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
    private static final Properties config = new Properties();
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final Logger logger = LoggerFactory.getLogger(TestConfig.class);
    static {
        String propsFilePath = System.getProperty("configFilePath", "src/test/resources/conf.properties");
        try (FileInputStream input = new FileInputStream(propsFilePath)) {
            config.load(input);
            logger.info("Properties loaded successfully from: {}", propsFilePath);
        } catch (IOException e) {
            logger.warn("Could not load properties file at {}: {}", propsFilePath, e.getMessage());
        }
    }
    public static String getProperty(String key) {
        if (key == null) {
            logger.error("Key is null.");
            throw new NullPointerException("Property key is null");
        }
        String envKey = key.toUpperCase();
        String value = dotenv.get(envKey);
        if (value != null) {
            logger.info("Loaded '{}' from .env", envKey);
            return value;
        }
        value = System.getenv(envKey);
        if (value != null) {
            logger.info("Loaded '{}' from system environment", envKey);
            return value;
        }
        value = System.getProperty(key);
        if (value != null) {
            logger.info("Loaded '{}' from system properties", key);
            return value;
        }
        value = config.getProperty(key);
        if (value != null) {
            logger.info("Loaded '{}' from conf.properties", key);
            return value;
        }
        logger.error("Property '{}' not found in any configuration source.", key);
        throw new IllegalStateException("Missing configuration for key: " + key);
    }
    public static String getZipCodesURL() { return getProperty("zipCodesURL"); }
    public static String getZipCodesExpandURL() { return getProperty("zipCodesExpandURL"); }
    public static String getUsersURL() { return getProperty("usersURL"); }
    public static String getUsersUploadURL() { return getProperty("usersUploadURL"); }
    public static String getTokenEndpoint() { return getProperty("TOKEN_ENDPOINT"); }
    public static String getUserName() { return getProperty("USERNAME2"); }
    public static String getPassword() { return getProperty("PASSWORD"); }
    public static String getGrantType() { return getProperty("grantType"); }
}