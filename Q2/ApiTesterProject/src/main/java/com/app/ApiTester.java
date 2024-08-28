package com.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class ApiTester {

    private static final String API_URL = "https://bfhldevapigw.healthrx.co.in/automation-campus/create/user";

    public static void main(String[] args) {
        try {
            // Test cases
            testSuccessfulAccountCreation();
            testMissingRequiredFields();
            testDuplicatePhoneNumber();
            testDuplicateEmailId();
            testMissingRollNumber();
            testIncorrectRollNumberFormat();
            testPhoneNumberNonNumeric();
            testInvalidEmailIdFormat();
            testLongInputValues();
            testHighVolumeRequests();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendRequest(String rollNumber, JSONObject body) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("roll-number", rollNumber);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = body.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int code = connection.getResponseCode();
        BufferedReader br;
        
        if (code >= 200 && code < 300) {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        } else {
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
        }

        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }

        // Always close the BufferedReader
        br.close();

        System.out.println("Response Code: " + code);
        System.out.println("Response Body: " + response.toString());
    }

    private static void testSuccessfulAccountCreation() throws Exception {
        JSONObject body = new JSONObject();
        body.put("firstName", "Test");
        body.put("lastName", "Test");
        body.put("phoneNumber", 9999999999L);
        body.put("emailId", "test.test@test.com");

        sendRequest("1", body);
    }

    private static void testMissingRequiredFields() throws Exception {
        JSONObject body = new JSONObject();
        body.put("firstName", "Test");
        // Missing other fields

        sendRequest("1", body);
    }

    private static void testDuplicatePhoneNumber() throws Exception {
        JSONObject body = new JSONObject();
        body.put("firstName", "Test");
        body.put("lastName", "Test");
        body.put("phoneNumber", 9999999999L);
        body.put("emailId", "another.test@test.com");

        sendRequest("1", body);
    }

    private static void testDuplicateEmailId() throws Exception {
        JSONObject body = new JSONObject();
        body.put("firstName", "Test");
        body.put("lastName", "Test");
        body.put("phoneNumber", 8888888888L);
        body.put("emailId", "test.test@test.com");

        sendRequest("1", body);
    }

    private static void testMissingRollNumber() throws Exception {
        JSONObject body = new JSONObject();
        body.put("firstName", "Test");
        body.put("lastName", "Test");
        body.put("phoneNumber", 9999999999L);
        body.put("emailId", "test.test@test.com");

        // Omitting roll-number header
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = body.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int code = connection.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        System.out.println("Response Code: " + code);
        System.out.println("Response Body: " + response.toString());
    }

    private static void testIncorrectRollNumberFormat() throws Exception {
        JSONObject body = new JSONObject();
        body.put("firstName", "Test");
        body.put("lastName", "Test");
        body.put("phoneNumber", 9999999999L);
        body.put("emailId", "test.test@test.com");

        sendRequest("invalid-roll-number", body);
    }

    private static void testPhoneNumberNonNumeric() throws Exception {
        JSONObject body = new JSONObject();
        body.put("firstName", "Test");
        body.put("lastName", "Test");
        body.put("phoneNumber", "non-numeric");
        body.put("emailId", "test.test@test.com");

        sendRequest("1", body);
    }

    private static void testInvalidEmailIdFormat() throws Exception {
        JSONObject body = new JSONObject();
        body.put("firstName", "Test");
        body.put("lastName", "Test");
        body.put("phoneNumber", 9999999999L);
        body.put("emailId", "invalid-email");

        sendRequest("1", body);
    }

    private static void testLongInputValues() throws Exception {
        JSONObject body = new JSONObject();
        body.put("firstName", "Test".repeat(100));
        body.put("lastName", "Test".repeat(100));
        body.put("phoneNumber", 9999999999L);
        body.put("emailId", "test.test@test.com".repeat(10));

        sendRequest("1", body);
    }

    private static void testHighVolumeRequests() throws Exception {
        for (int i = 0; i < 100; i++) {
            JSONObject body = new JSONObject();
            body.put("firstName", "Test" + i);
            body.put("lastName", "Test" + i);
            body.put("phoneNumber", 9999999999L + i);
            body.put("emailId", "test" + i + "@test.com");

            sendRequest("1", body);
        }
    }
}