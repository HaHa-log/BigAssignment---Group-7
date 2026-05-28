package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.dto.item.ItemRequest;
import com.group7.dto.item.ItemResponse;
import config.ApiConfig;
import models.Item;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ItemApiService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = ApiConfig.baseUrl() + "/api/items";

    public void uploadItemImage(int itemId, File imageFile) throws Exception {
        String boundary = "----Boundary" + System.currentTimeMillis();

        byte[] fileBytes = java.nio.file.Files.readAllBytes(imageFile.toPath()); //đọc toàn bộ bytes của ảnh
        String header = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; name=\"file\"; filename=\"" + imageFile.getName() + "\"\r\n"
                + "Content-Type: application/octet-stream\r\n\r\n";
        String footer = "\r\n--" + boundary + "--\r\n";

        //Combine header + body + footer into a single byte array[]
        byte[] h = header.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] f = footer.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] body = new byte[h.length + fileBytes.length + f.length];
        System.arraycopy(h,0, body, 0,                                h.length);
        System.arraycopy(fileBytes, 0, body, h.length,                         fileBytes.length);
        System.arraycopy(f, 0, body, h.length + fileBytes.length,      f.length);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/" + itemId + "/image"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Failed to upload image: " + response.body());
        }
    }

    public List<Item> fetchInventory(int ownerId, int page, int size) throws Exception {
        String urlWithPaging = baseUrl + "/owner/" + ownerId + "?page=" + page + "&size=" + size;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithPaging))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            List<ItemResponse> dtoList = objectMapper.readValue(
                    response.body(),
                    new TypeReference<List<ItemResponse>>() {}
            );

            return ItemMapper.toItemList(dtoList);
        } else {
            throw new RuntimeException("Failed to load inventory. Server responded with code: " + response.statusCode());
        }
    }

    public Item create(ItemRequest request) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return objectMapper.readValue(response.body(), Item.class);
        } else {
            throw new RuntimeException("Failed to create item: " + response.body());
        }
    }

    public List<Item> fetchInventory(int ownerId) throws Exception {
        return fetchInventory(ownerId, 0, 12);
    }

    public String getItemImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isBlank() || "null".equalsIgnoreCase(imagePath)) {
            return null;
        }
        return baseUrl + "/images/" + imagePath;
    }
}