package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.User;
import services.dto.user.UserResponse;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class UserApiService {
    private static final String BASE_URL = ApiConfig.baseUrl() + "/api/users";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = ApiJson.mapper();

    public List<User> getAll() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();
        List<UserResponse> users = mapper.readValue(
                send(request).body(),
                new TypeReference<List<UserResponse>>() {}
        );
        return users.stream().map(UserMapper::toUser).toList();
    }

    public User block(int id) throws IOException, InterruptedException {
        return postStateChange(id, "block");
    }

    public User unblock(int id) throws IOException, InterruptedException {
        return postStateChange(id, "unblock");
    }

    private User postStateChange(int id, String action) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id + "/" + action))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return UserMapper.toUser(mapper.readValue(send(request).body(), UserResponse.class));
    }

    private HttpResponse<String> send(HttpRequest request) throws IOException, InterruptedException {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return response;
            }
            throw new ApiException(extractErrorMessage(response.body()));
        } catch (ConnectException | UnknownHostException e) {
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
