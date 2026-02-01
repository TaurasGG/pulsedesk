package com.pulsedesk;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestHuggingFace {

    public static void main(String[] args) {
        String token = "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";
        
        // Test 1: Phi-3
        testOpenAI(token, "microsoft/Phi-3-mini-4k-instruct", "https://router.huggingface.co/v1/chat/completions");
        
        // Test 2: Qwen
        testOpenAI(token, "Qwen/Qwen2.5-7B-Instruct", "https://router.huggingface.co/v1/chat/completions");
    }

    private static void testOpenAI(String token, String modelName, String urlString) {
        String prompt = "Test comment";
        // OpenAI format
        String jsonInputString = String.format(
            "{\"model\": \"%s\", \"messages\": [{\"role\": \"user\", \"content\": \"%s\"}], \"max_tokens\": 50}",
            modelName, prompt
        );

        try {
            System.out.println("Testing URL: " + urlString + " with model: " + modelName);
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + token);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = con.getResponseCode();
            System.out.println("Response Code: " + code);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(code >= 400 ? con.getErrorStream() : con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Response Body: " + response.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("--------------------------------------------------");
    }
}
