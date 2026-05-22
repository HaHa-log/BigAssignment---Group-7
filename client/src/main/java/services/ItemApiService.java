package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.dto.item.ItemRequest;
import com.group7.dto.item.ItemResponse;
import models.Item;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ItemApiService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Parses JSON
    private final String BASE_URL = "http://localhost:8080/api/items"; // Replace with your server URL

    public List<Item> fetchInventory(int ownerId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/owner/" + ownerId))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Read JSON array into a list of DTOs
            List<ItemResponse> dtoList = objectMapper.readValue(
                    response.body(),
                    new TypeReference<List<ItemResponse>>() {}
            );

            // Map DTOs into real UI-renderable Item domain models
            List<Item> domainItems = new ArrayList<>();
            for (ItemResponse dto : dtoList) {
                domainItems.add(ItemMapper.toItem(dto));
            }
            return domainItems;
        } else {
            throw new RuntimeException("Failed to load inventory. Server responded with code: " + response.statusCode());
        }
    }

    public void createItem(ItemRequest itemDto) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(itemDto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RuntimeException("Failed to save item on server: " + response.body());
        }
    }
}