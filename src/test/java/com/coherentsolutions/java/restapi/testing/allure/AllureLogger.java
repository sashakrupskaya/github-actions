package com.coherentsolutions.java.restapi.testing.allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;

public class AllureLogger {

//    @Step("{url} to request {method}")
//    public static void logRequest(String url, String method, String body) {
//        attachText("Request Body", body);
//    }

    @Attachment(value = "{name}", type = "text/plain")
    public static String attachText(String name, String content) {
        return content;
    }

    @Attachment(value = "{name}", type = "application/json")
    public static String attachJson(String name, String json) {
        return json;
    }
}