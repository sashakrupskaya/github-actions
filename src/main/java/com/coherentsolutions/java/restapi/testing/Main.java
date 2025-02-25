package com.coherentsolutions.java.restapi.testing;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            OAuthClient client = OAuthClient.getInstance();

            //GET request
            HttpResponse getUsers = client.sendGetRequest("http://localhost:49000/users");
            System.out.println("GET Users: " + getUsers);
            //GET request
            HttpResponse getZipCodes = client.sendGetRequest("http://localhost:49000/zip-codes");
            System.out.println("GET Response: " + getZipCodes);
////
//
//            //POST request
//            String postUsers = client.sendWriteRequest("http://localhost:49000/users", "POST", "{\n" +
//                    "  \"age\": 255,\n" +
//                    "  \"name\": \"Tester22\",\n" +
//                    "  \"sex\": \"MALE\",\n" +
//                    "  \"zipCode\": \"test\"\n" +
//                    "}");
//

            //PUT request
//            String putUsers = client.sendWriteRequest("http://localhost:49000/users", "PUT", "{\n" +
//                    "  \"age\": 100,\n" +
//                    "  \"name\": \"Tester\",\n" +
//                    "  \"sex\": \"FEMALE\",\n" +
//                    "  \"zipCode\": \"ABCDE\"\n" +
//                    "}");
//

//            //DELETE request
//            String deleteUsers = client.sendWriteRequest("http://localhost:49000/users", "DELETE", "{\n" +
//                    "  \"age\": 255,\n" +
//                    "  \"name\": \"Tester22\",\n" +
//                    "  \"sex\": \"MALE\",\n" +
//                    "  \"zipCode\": \"ABCDE\"\n" +
//                    "}");


//            //PATCH request
//            String patchUsers = client.sendWriteRequest("http://localhost:49000/users", "PATCH", "{\n" +
//                    "  \"userNewValues\": {\n" +
//                    "    \"age\": 33,\n" +
//                    "    \"name\": \"updated\",\n" +
//                    "    \"sex\": \"FEMALE\",\n" +
//                    "    \"zipCode\": \"12345\"\n" +
//                    "  },\n" +
//                    "  \"userToChange\": {\n" +
//                    "    \"age\": 34,\n" +
//                    "    \"name\": \"Tester\",\n" +
//                    "    \"sex\": \"FEMALE\",\n" +
//                    "    \"zipCode\": \"12345\"\n" +
//                    "  }\n" +
//                    "}");
//

//            //POST request
//            String postUsersUpload = client.sendWriteRequest("http://localhost:49000/users", "POST", "{[]}");
//

//

//            //POST request
//            String postUsersUpload = client.sendWriteRequest("http://localhost:49000/zip-codes/expand", "POST", "[]");
//            System.out.println("POST Response: " + postUsersUpload); //POST Response: ["12345","23456","ABCDE","test"]


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}