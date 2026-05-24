package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ApiConfig;
import exceptions.ApiException;
import com.group7.dto.user.UserResponse;
import utils.ApiJson;
import models.User; // ĐỒNG BỘ: Import lớp thực thể User mới

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class UserApiService {
    private final String BASE_URL = ApiConfig.baseUrl() + "/api/users";
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

    public User getByEmail(String email) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/email/" + email))
                .GET()
                .build();
        return UserMapper.toUser(mapper.readValue(send(request).body(), UserResponse.class));
    }

    public User getById(int id) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();
        return UserMapper.toUser(mapper.readValue(send(request).body(), UserResponse.class));
    }

    public User uploadAvatar(int userId, File avatarFile) throws IOException, InterruptedException {
        String boundary = "----Boundary" + System.currentTimeMillis();

        byte[] fileBytes = java.nio.file.Files.readAllBytes(avatarFile.toPath());
        String header = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"" + avatarFile.getName() + "\"\r\n"
                + "Content-Type: application/octet-stream\r\n\r\n";
        String footer = "\r\n--" + boundary + "--\r\n";

        byte[] h = header.getBytes(StandardCharsets.UTF_8);
        byte[] f = footer.getBytes(StandardCharsets.UTF_8);
        byte[] body = new byte[h.length + fileBytes.length + f.length];

        System.arraycopy(h, 0, body, 0, h.length);
        System.arraycopy(fileBytes, 0, body, h.length, fileBytes.length);
        System.arraycopy(f, 0, body, h.length + fileBytes.length, f.length);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + userId + "/avatar"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        return UserMapper.toUser(mapper.readValue(send(request).body(), UserResponse.class));
    }

    public User block(int id) throws IOException, InterruptedException {
        return postStateChange(id, "block");
    }

    public User unblock(int id) throws IOException, InterruptedException {
        return postStateChange(id, "unblock");
    }

    public User deposit(int id, double amount) throws IOException, InterruptedException {
        return postFinanceAction(id, "deposit", amount);
    }

    public User withdraw(int id, double amount) throws IOException, InterruptedException {
        return postFinanceAction(id, "withdraw", amount);
    }

    public User freeze(int id, double amount) throws IOException, InterruptedException {
        return postFinanceAction(id, "freeze", amount);
    }

    public User unfreeze(int id, double amount) throws IOException, InterruptedException {
        return postFinanceAction(id, "unfreeze", amount);
    }

    public User spendFrozen(int id, double amount) throws IOException, InterruptedException {
        return postFinanceAction(id, "spend-frozen", amount);
    }

    public String getAvatarUrl(String avatarPath) {
        if (avatarPath == null || avatarPath.isBlank() || "null".equalsIgnoreCase(avatarPath)) {
            return null;
        }
        return BASE_URL + "/avatars/" + avatarPath;
    }

    // ĐỒNG BỘ: Sửa kiểu trả về sang User và gọi UserMapper.toUser()
    private User postFinanceAction(int id, String action, double amount)
            throws IOException, InterruptedException {
        String body = mapper.writeValueAsString(Map.of("amount", amount));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id + "/" + action))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return UserMapper.toUser(mapper.readValue(send(request).body(), UserResponse.class));
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
