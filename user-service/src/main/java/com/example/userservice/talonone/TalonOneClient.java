package com.example.userservice.talonone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles communication with Talon.One for user registration.
 */
@Component
public class TalonOneClient {
    @Value("${talonone.api.url}")
    private String talonOneApiUrl;
    @Value("${talonone.api.key}")
    private String talonOneApiKey;
    @Value("${talonone.application.id}")
    private String talonOneApplicationId;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Registers a user in Talon.One (creates a customer profile).
     * @param integrationId Unique identifier for the user (e.g., email or user ID)
     * @param attributes Map of user attributes
     */
    public void registerUser(String integrationId, Map<String, Object> attributes) {
        String url = talonOneApiUrl + "/v1/customer_profiles/" + integrationId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "ApiKey-v1 " + talonOneApiKey);
        Map<String, Object> body = new HashMap<>();
        body.put("attributes", attributes);
        body.put("applicationId", talonOneApplicationId);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }
}
