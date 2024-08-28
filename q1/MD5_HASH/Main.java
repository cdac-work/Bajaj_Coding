import java.io.*;
import java.util.stream.Collectors;
import java.security.MessageDigest;
import java.util.Random;


public class Main {
    public static String readJson(String jsonPath) throws IOException {
        String jsonString;
        try (InputStream is = new FileInputStream(jsonPath)) {
            jsonString = new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .collect(Collectors.joining("\n"));
        }
        return jsonString;
    }
    
    
    public static String findDestination(String jsonString) {
        int startIndex = jsonString.indexOf("destination");

        if (startIndex == -1) {
            return "Not Found";
        }
        startIndex += 11; // length of "destination":
        int endIndex = startIndex;
        int bracketCount = 0;

        while (endIndex < jsonString.length()) {
            char currentChar = jsonString.charAt(endIndex);
            if (currentChar == '"') {
                bracketCount++;

                if (bracketCount == 2) {
                    startIndex = endIndex;
                }
            }


            else if (bracketCount == 3) {
                break;
            }

            endIndex++;
        }
        
        return jsonString.substring(startIndex+1, endIndex-1).trim();
    }

    
    public static String generateHash(String prn, String destination) {
        String randomString = generateRandomString(8);
        String concatenatedString = prn + destination + randomString;
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(concatenatedString.getBytes());
            byte[] digest = md.digest();
            
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString() + ";" + randomString;
        } catch (Exception e) {
            throw new IllegalStateException("MD5 algorithm not available", e);
        }
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
    

    public static void main(String[] args) throws IOException {
        // Check if the correct number of arguments are provided
        if (args.length != 2) {
            System.out.println("Usage: java Main <name> <phone_number>");
            return;
        }

        // Retrieve command line arguments
        String prn = args[0];
        String json_path = args[1];
        String destinationValue = "Not Found";
        String finalString = "Not Found";

        try {
            destinationValue = readJson(json_path);
        } catch (Exception e) {
            System.out.println("Error reading JSON file: " + e);
        }

        if (destinationValue != "Not Found") {
            finalString = findDestination(destinationValue);
        }

        if (finalString != "Not Found") {
            System.out.println(generateHash(prn, finalString));
        }

        System.out.println();
    }
}
