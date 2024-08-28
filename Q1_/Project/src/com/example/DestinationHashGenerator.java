package com.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <path to JSON file>");
            return;
        }

        String prnNumber = args[0].toLowerCase().replace(" ", "");
        String jsonFilePath = args[1];

        try {
            // Parse the JSON file
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(jsonFilePath));

            // Find the destination value
            String destinationValue = findDestination(jsonObject);
            if (destinationValue == null) {
                System.out.println("No destination key found in the JSON file.");
                return;
            }

            // Generate a random 8-character alphanumeric string
            String randomString = generateRandomString(8);

            // Concatenate PRN, destination value, and random string
            String concatenatedString = prnNumber + destinationValue + randomString;

            // Generate MD5 hash
            String hashValue = generateMD5Hash(concatenatedString);

            // Output the result in the format <hash>;<random string>
            System.out.println(hashValue + ";" + randomString);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static String findDestination(JSONObject jsonObject) {
        Set<Map.Entry<String, Object>> entrySet = jsonObject.entrySet();

        for (Map.Entry<String, Object> entry : entrySet) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if ("destination".equals(key)) {
                return value.toString();
            }

            if (value instanceof JSONObject) {
                String result = findDestination((JSONObject) value);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        Random rnd = new Random();
        while (randomString.length() < length) {
            int index = (int) (rnd.nextFloat() * chars.length());
            randomString.append(chars.charAt(index));
        }
        return randomString.toString();
    }

    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}