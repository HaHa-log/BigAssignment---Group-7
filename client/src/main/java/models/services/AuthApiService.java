package models.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.dto.auth.AuthResponse;
import models.dto.auth.LoginRequest;
import models.dto.auth.RegisterRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthApiService {
    private static final String BASE_URL = ApiConfig.baseUrl() + "/api/auth";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = ApiJson.mapper();

    public AuthResponse register(RegisterRequest req) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(req);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return mapper.readValue(response.body(), AuthResponse.class);
            }

            if (response.statusCode() == 400) {
                throw new IllegalArgumentException(extractErrorMessage(response.body()));
            }

            throw new RuntimeException("Server error (" + response.statusCode() + "): " + response.body());

        } catch (java.net.ConnectException | java.net.UnknownHostException e) {
            throw new IOException("Cannot connect to server at " + ApiConfig.baseUrl()
                    + ". Start the Spring Boot server first.", e);
        }
    }

    public AuthResponse login(LoginRequest req) throws IOException, InterruptedException  {
        String json = mapper.writeValueAsString(req);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return mapper.readValue(response.body(), AuthResponse.class);
            }

            if (response.statusCode() == 400) {
                throw new IllegalArgumentException(extractErrorMessage(response.body()));
            }

            throw new RuntimeException("Server error (" + response.statusCode() + "): " + response.body());

        } catch (java.net.ConnectException | java.net.UnknownHostException e) {
            throw new IOException("Cannot connect to server at " + ApiConfig.baseUrl()
                    + ". Start the Spring Boot server first.", e);
        }
    }

    private String extractErrorMessage(String body) {
        try {
            return mapper.readTree(body).path("error").asText("Invalid request.");
        } catch (Exception e) {
            return body;
        }
    }
}
