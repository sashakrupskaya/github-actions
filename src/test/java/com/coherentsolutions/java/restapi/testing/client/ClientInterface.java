package com.coherentsolutions.java.restapi.testing.client;

import java.io.File;
import java.io.IOException;

public interface ClientInterface {
    String getReadToken() throws IOException;
    String getWriteToken() throws IOException;
    void resetHttpClient();
    void refreshTokensAfterReset();
    HttpResponse sendGetRequest(String url) throws IOException;
    HttpResponse sendWriteRequest(String url, String method, String body) throws IOException;
    HttpResponse uploadJsonFile(String url, File jsonFile) throws Exception;
    void shutdown() throws IOException;
    void restartDockerContainer() throws IOException, InterruptedException;
}
