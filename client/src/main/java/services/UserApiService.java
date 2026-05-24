package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ApiConfig;
import exceptions.ApiException;
import models.Member;
import com.group7.dto.user.UserResponse;
import utils.ApiJson;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class UserApiService {
    private static final String BASE_URL = ApiConfig.baseUrl() + "/api/users";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = ApiJson.mapper();

    public List<Member> getAll() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();
        List<UserResponse> users = mapper.readValue(
                send(request).body(),
                new TypeReference<List<UserResponse>>() {}
        );
        return users.stream().map(UserMapper::toMember).toList();
    }

    public Member getByEmail(String email) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/email/" + email))
                .GET()
                .build();
        return UserMapper.toMember(mapper.readValue(send(request).body(), UserResponse.class));
    }

    public Member getById(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();
        return UserMapper.toMember(mapper.readValue(send(request).body(), UserResponse.class));
    }

    public Member block(int id) throws IOException, InterruptedException {
        return postStateChange(id, "block");
    }

    public Member unblock(int id) throws IOException, InterruptedException {
        return postStateChange(id, "unblock");
    }

    public Member deposit(int id, double amount) throws IOException, InterruptedException {
        return postFinanceAction(id, "deposit", amount);
    }

    public Member withdraw(int id, double amount) throws IOException, InterruptedException {
        return postFinanceAction(id, "withdraw", amount);
    }

    public Member freeze(int id, double amount) throws IOException, InterruptedException {
        return postFinanceAction(id, "freeze", amount);
    }

    public Member unfreeze(int id, double amount) throws IOException, InterruptedException {
        return postFinanceAction(id, "unfreeze", amount);
    }

    public Member spendFrozen(int id, double amount) throws IOException, InterruptedException {
        return postFinanceAction(id, "spend-frozen", amount);
    }

    private Member postFinanceAction(int id, String action, double amount)
            throws IOException, InterruptedException {
        String body = mapper.writeValueAsString(Map.of("amount", amount));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id + "/" + action))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return UserMapper.toMember(mapper.readValue(send(request).body(), UserResponse.class));
    }

    private Member postStateChange(int id, String action) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id + "/" + action))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return UserMapper.toMember(mapper.readValue(send(request).body(), UserResponse.class));
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