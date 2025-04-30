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
    protected static final Logger logger = LoggerFactory.getLogger(TestConfig.class);

    static {
        String propsFilePath = System.getProperty("configFilePath", "src/test/resources/conf.properties");
        try (FileInputStream input = new FileInputStream(propsFilePath)) {
            config.load(input);
            logger.info("Properties loaded successfully from " + propsFilePath);
        } catch (IOException e) {
            logger.warn("Could not load properties file from '{}'. Falling back to environment variables. Reason: {}", propsFilePath, e.getMessage());
        }
    }

    public static String getProperty(String key) {
        if (key == null) {
            logger.error("Property key is null.");
            throw new NullPointerException("Property key is null");
        }

        String envValue = dotenv.get(key.toUpperCase());
        if (envValue != null) {
            return envValue;
        }

        String fileValue = config.getProperty(key);
        if (fileValue != null) {
            return fileValue;
        }

        logger.error("Missing required property: {}", key);
        throw new IllegalStateException("Missing required property: " + key);
    }

    // Accessors remain unchanged (examples):
    public static String getTokenEndpoint() { return getProperty("TOKEN_ENDPOINT"); }
    public static String getUserName() { return getProperty("USERNAME2"); }
    public static String getPassword() { return getProperty("PASSWORD"); }
    public static String getGrantType() { return getProperty("grantType"); }
    public static String getZipCodesURL() { return getProperty("zipCodesURL"); }
    public static String getZipCodesExpandURL() { return getProperty("zipCodesExpandURL"); }
    public static String getUsersURL() { return getProperty("usersURL"); }
    public static String getUsersUploadURL() { return getProperty("usersUploadURL"); }
}