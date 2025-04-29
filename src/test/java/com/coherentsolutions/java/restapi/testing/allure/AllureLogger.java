package com.coherentsolutions.java.restapi.testing.allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;

public class AllureLogger {
    @Step("Request made to {url} with method {method}")
    public static void logRequest(String url, String method, String body) {
        attachPlaneText("Request URL and Method", url + " - " + method);
        if (body != null && !body.isEmpty()) {
            attachPlaneText("Request Body", body);
        }
    }

    @Attachment(value = "{name}", type = "text/plain")
    public static String attachPlaneText(String name, String content) {
        return content;
    }

    @Attachment(value = "{name}", type = "application/json")
    public static String attachJson(String name, String json) {
        return json;
    }
}