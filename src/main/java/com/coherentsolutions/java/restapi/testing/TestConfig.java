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
     * try ... catch block
     * input gets initialized
     * (if (input != null)), else unsuccessful message is shown
     * loads the configuration properties from a file
     */
    static {
        try (FileInputStream input = new FileInputStream("src/main/resources/conf.properties")) {
            if (input != null) {
                config.load(input);
            } else {
                logger.error("Properties file not found.");
            }
        } catch (IOException e) {
            logger.error("Error reading properties file: " + e.getMessage());
            throw new IllegalStateException("Error reading properties file.", e);
        }
    }

    /**
     * initializes file path, key: value
     * loads the configuration properties from a file
     */
    static {
        String propsFilePath = System.getProperty("configFilePath", "src/main/resources/conf.properties");
        try (FileInputStream input = new FileInputStream(propsFilePath)) {
            config.load(input);
            if (input != null) {
                logger.info("Properties loaded successfully from " + propsFilePath);
            } else {
                logger.error("Properties file not found at " + propsFilePath);
            }
        } catch (IOException e) {
            logger.error("Error reading properties file: " + e.getMessage());
            throw new IllegalStateException("Error reading properties file.", e);
        }
    }

    /**
     * (if(key == null))
     * @param key
     * @return value
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
    public static String getContentTypeValue() {
        return getProperty("contentTypeValue");
    }
    public static String getGrantType() {
        return getProperty("grantType");
    }
}