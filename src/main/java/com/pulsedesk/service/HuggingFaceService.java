package com.pulsedesk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Service to interact with Hugging Face Inference API for comment analysis.
 * Uses OpenAI-compatible endpoint format.
 */
@Service
public class HuggingFaceService {

    @Value("${huggingface.api.token}")
    private String apiToken;

    @Value("${huggingface.api.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    public HuggingFaceService() {
        this.objectMapper = new ObjectMapper()
                .enable(com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
        
        // Configure coercion for empty strings to null (especially for Enums)
        this.objectMapper.coercionConfigDefaults()
                .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull);
    }

    /**
     * Analyzes a user comment to determine if it requires a support ticket.
     *
     * @param comment The user's input text.
     * @return An AiTicketResponse containing classification details (isTicket, category, priority, etc.).
     */
    public AiTicketResponse analyzeComment(String comment) {

        // Use the router endpoint with OpenAI compatibility
        String url = "https://router.huggingface.co/v1/chat/completions";

        String systemPrompt = """
            You are a support ticket classifier for PulseDesk.
            Analyze the user comment and determine if it should be a support ticket.
            Respond ONLY with a valid JSON object (no markdown, no extra text).
            JSON schema:
            {
              "isTicket": boolean,
              "title": "string",
              "category": "BUG | FEATURE | BILLING | ACCOUNT | OTHER",
              "priority": "LOW | MEDIUM | HIGH",
              "summary": "string"
            }
            """;

        Map<String, Object> requestBody = Map.of(
            "model", model,
            "messages", java.util.List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", comment)
            ),
            "max_tokens", 500,
            "temperature", 0.1
        );

        try {
            Object responseObj = restTemplate.postForObject(
                    url,
                    new org.springframework.http.HttpEntity<>(requestBody, new org.springframework.http.HttpHeaders() {{
                        set("Authorization", "Bearer " + apiToken);
                        set("Content-Type", "application/json");
                    }}),
                    Object.class
            );

            String generatedText = "";
            
            // OpenAI format response parsing: choices[0].message.content
            if (responseObj instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) responseObj;
                if (map.containsKey("choices")) {
                    Object choicesObj = map.get("choices");
                    if (choicesObj instanceof java.util.List) {
                        java.util.List<?> choices = (java.util.List<?>) choicesObj;
                        if (!choices.isEmpty() && choices.get(0) instanceof Map) {
                            Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                            if (firstChoice.get("message") instanceof Map) {
                                Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                                generatedText = (String) message.get("content");
                            }
                        }
                    }
                }
            }

            if (generatedText == null || generatedText.isBlank()) {
                throw new RuntimeException("Empty response from AI");
            }

            // Clean up content (remove markdown code blocks if present)
            generatedText = generatedText.replaceAll("```json", "").replaceAll("```", "").trim();

            return objectMapper.readValue(generatedText, AiTicketResponse.class);

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("AI Analysis Failed (HTTP Error): " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            e.printStackTrace();
            
            AiTicketResponse fallback = new AiTicketResponse();
            fallback.setTicket(false);
            return fallback;
        } catch (Exception e) {
            System.err.println("AI Analysis Failed: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback
            AiTicketResponse fallback = new AiTicketResponse();
            fallback.setTicket(false);
            return fallback;
        }
    }
}
