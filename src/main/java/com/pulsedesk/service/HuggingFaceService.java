package com.pulsedesk.service;

import com.fasterxml.jackson.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class HuggingFaceService {

    @Value("${huggingface.api.token}")
    private String apiToken;

    @Value("${huggingface.api.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiTicketResponse analyzeComment(String comment) {

        String url = "https://api-inference.huggingface.co/models/" + model;

        String prompt = """
        You are a support ticket classifier.
        Respond ONLY with valid JSON in this exact format:

        {
          "isTicket": true or false,
          "title": "short title",
          "category": "BUG | FEATURE | BILLING | ACCOUNT | OTHER",
          "priority": "LOW | MEDIUM | HIGH",
          "summary": "short summary"
        }

        Comment:
        """ + comment;

        Map<String, Object> requestBody = Map.of("inputs", prompt);

        var response = restTemplate.postForObject(
                url,
                new org.springframework.http.HttpEntity<>(requestBody, new org.springframework.http.HttpHeaders() {{
                    set("Authorization", "Bearer " + apiToken);
                }}),
                Map.class
        );

        try {
            assert response != null;
            String generatedText = ((Map<String, String>) ((java.util.List<?>) response).getFirst())
                    .get("generated_text");

            return objectMapper.readValue(generatedText, AiTicketResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }
}
