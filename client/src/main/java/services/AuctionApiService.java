package services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.dto.bid.BidRequest;
import config.ApiConfig;
import models.Auction;
import com.group7.dto.auction.*;
import exceptions.ApiException;
import utils.ApiJson;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class AuctionApiService {
    private static final String BASE_URL = ApiConfig.baseUrl() + "/api/auctions";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = ApiJson.mapper();

    public List<Auction> getAll(int page, int size, String status) throws IOException, InterruptedException {
        String urlWithPaging = BASE_URL + "?page=" + page + "&size=" + size
                + "&status=" + java.net.URLEncoder.encode(
                status == null ? "ALL" : status,
                java.nio.charset.StandardCharsets.UTF_8
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlWithPaging))
                .GET()
                .build();

        HttpResponse<String> response = send(request);
        List<AuctionResponse> auctions = mapper.readValue(
                response.body(),
                new TypeReference<List<AuctionResponse>>() {
                }
        );
        return AuctionMapper.toAuctionList(auctions);
    }

    public List<Auction> getAll() throws IOException, InterruptedException {
        return getAll(0, 20, "ALL");
    }

    public List<Auction> getAllForManagement() throws IOException, InterruptedException {
        int page = 0;
        int pageSize = 100;
        List<Auction> allAuctions = new ArrayList<>();
        List<Auction> batch;

        do {
            batch = getAll(page, pageSize, "ALL");
            allAuctions.addAll(batch);
            page++;
        } while (batch.size() == pageSize);

        return allAuctions;
    }

    public Auction getById(int auctionId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + auctionId))
                .GET()
                .build();
        return AuctionMapper.toAuction(mapper.readValue(send(request).body(), AuctionResponse.class));
    }

    public Auction create(CreateAuctionRequest payload) throws IOException, InterruptedException {
        HttpRequest request = jsonRequest(BASE_URL, payload).POST(
                HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload))
        ).build();
        return AuctionMapper.toAuction(mapper.readValue(send(request).body(), AuctionResponse.class));
    }

    public Auction placeBid(int auctionId, int bidderId, double amount) throws IOException, InterruptedException, IllegalArgumentException {
        BidRequest payload = new BidRequest(bidderId, amount);
        HttpRequest request = jsonRequest(BASE_URL + "/" + auctionId + "/bids", payload).POST(
                HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload))
        ).build();
        return AuctionMapper.toAuction(mapper.readValue(send(request).body(), AuctionResponse.class));
    }

    public void enableAutoBid(int auctionId, com.group7.dto.bid.AutoBidRequest payload) throws IOException, InterruptedException {
        String url = ApiConfig.baseUrl() + "/api/autobids/" + auctionId;
        HttpRequest request = jsonRequest(url, payload).POST(
                HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload))
        ).build();
        send(request);
    }

    public Auction cancel(int auctionId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + auctionId + "/cancel"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return AuctionMapper.toAuction(mapper.readValue(send(request).body(), AuctionResponse.class));
    }

    public Auction confirmReceipt(int auctionId, int buyerId) throws IOException, InterruptedException {
        ConfirmReceiptRequest payload = new ConfirmReceiptRequest(buyerId);
        HttpRequest request = jsonRequest(BASE_URL + "/" + auctionId + "/confirm-receipt", payload).POST(
                HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(payload))
        ).build();
        return AuctionMapper.toAuction(mapper.readValue(send(request).body(), AuctionResponse.class));
    }

    public URI bidWebSocketUri(int auctionId) {
        return URI.create(ApiConfig.baseUrl().replaceFirst("^http", "ws") + "/ws/auctions/" + auctionId + "/bids");
    }

    public double readCurrentPriceUpdate(CharSequence data) throws IOException {
        return mapper.readTree(data.toString()).get("currentPrice").asDouble();
    }

    private HttpRequest.Builder jsonRequest(String url, Object ignoredPayload) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json");
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
