package APItest;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.group7.dto.auction.CreateAuctionRequest;
import com.group7.dto.bid.AutoBidRequest;
import exceptions.ApiException;
import models.Auction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.AuctionApiService;

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class AuctionApiServiceTest {

    private AuctionApiService apiService;

    private final String singleAuctionJson = """
        {
            "id": 42,
            "ownerId": 1,
            "ownerName": "John Doe",
            "itemId": 101,
            "itemName": "Vintage Watch",
            "itemDescription": "A rare 1970s automatic watch.",
            "itemImagePath": "/images/watch.jpg",
            "startingPrice": 150.0,
            "currentPrice": 200.0,
            "status": "OPEN",
            "startingTime": "2026-05-26T12:00:00",
            "endingTime": "2026-06-02T12:00:00",
            "bids": []
        }
        """;

    private final String multipleAuctionsJson = """
        [
            {
                "id": 42,
                "ownerId": 1,
                "ownerName": "John Doe",
                "itemId": 101,
                "itemName": "Vintage Watch",
                "startingPrice": 150.0,
                "currentPrice": 200.0,
                "status": "OPEN",
                "bids": []
            },
            {
                "id": 43,
                "ownerId": 1,
                "ownerName": "John Doe",
                "itemId": 102,
                "itemName": "Retro Camera",
                "startingPrice": 300.0,
                "currentPrice": 350.0,
                "status": "OPEN",
                "bids": [
                    {
                        "bidderId": 2,
                        "bidderName": "Jane Smith",
                        "bidPrice": 350.0,
                        "bidTime": "2026-05-26T14:00:00"
                    }
                ]
            }
        ]
        """;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        System.setProperty("server.url", wmRuntimeInfo.getHttpBaseUrl());
        apiService = new AuctionApiService();
    }

    @Test
    @DisplayName("getAll(paging)")
    void getAll_WithPaging_Success() throws Exception {
        stubFor(get(urlPathEqualTo("/api/auctions"))
                .withQueryParam("page", equalTo("0"))
                .withQueryParam("size", equalTo("10"))
                .withQueryParam("status", equalTo("OPEN"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(multipleAuctionsJson)));

        List<Auction> results = apiService.getAll(0, 10, "OPEN");

        assertNotNull(results);
        assertNotNull(results.get(1).getBids());
        assertEquals(2, results.size());
        assertEquals(42, results.get(0).getAuctionId());
        assertEquals(43, results.get(1).getAuctionId());
    }

    @Test
    @DisplayName("getAll()")
    void getAll_NoArgs_Success() throws Exception {
        stubFor(get(urlEqualTo("/api/auctions"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(multipleAuctionsJson)));

        List<Auction> results = apiService.getAll();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertFalse(results.isEmpty());

        // Verifies the AuctionMapper identity map reuse optimizations across separate list entries
        assertEquals(results.get(0).getOwner().getId(), results.get(1).getOwner().getId());
    }

    @Test
    @DisplayName("getById()")
    void getById_Success() throws Exception {
        stubFor(get(urlEqualTo("/api/auctions/42"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(singleAuctionJson)));

        Auction result = apiService.getById(42);

        assertNotNull(result);
        assertEquals(42, result.getAuctionId());
        assertEquals("Vintage Watch", result.getItem().getName());
        assertEquals("John Doe", result.getOwner().getFullName()); // Tests splitNameOptimized logic
    }

    @Test
    @DisplayName("create()")
    void create_Success() throws Exception {
        stubFor(post(urlEqualTo("/api/auctions"))
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(containing("\"itemName\":\"Vintage Watch\""))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(singleAuctionJson)));

        CreateAuctionRequest requestDto = new CreateAuctionRequest();
        requestDto.setItemName("Vintage Watch");

        Auction result = apiService.create(requestDto);

        assertNotNull(result);
        assertEquals("Vintage Watch", result.getItem().getName());
    }

    @Test
    @DisplayName("placeBid()")
    void placeBid_Success() throws Exception {
        stubFor(post(urlEqualTo("/api/auctions/42/bids"))
                .withRequestBody(matchingJsonPath("$.bidderId", equalTo("2")))
                .withRequestBody(matchingJsonPath("$.amount", equalTo("250.0")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(singleAuctionJson)));

        Auction result = apiService.placeBid(42, 2, 250.0);
        assertNotNull(result);
    }

    @Test
    @DisplayName("enableAutoBid()")
    void enableAutoBid_VoidSuccess() throws Exception {
        stubFor(post(urlEqualTo("/api/autobids/42"))
                .willReturn(aResponse().withStatus(200)));

        AutoBidRequest payload = new AutoBidRequest();

        assertDoesNotThrow(() -> apiService.enableAutoBid(42, payload));
    }

    @Test
    @DisplayName("cancel()")
    void cancel_Success() throws Exception {
        stubFor(post(urlEqualTo("/api/auctions/42/cancel"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(singleAuctionJson)));

        Auction result = apiService.cancel(42);
        assertNotNull(result);
    }

    @Test
    @DisplayName("confirmReceipt()")
    void confirmReceipt_Success() throws Exception {
        stubFor(post(urlEqualTo("/api/auctions/42/confirm-receipt"))
                .withRequestBody(matchingJsonPath("$.buyerId", equalTo("7")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(singleAuctionJson)));

        Auction result = apiService.confirmReceipt(42, 7);
        assertNotNull(result);
    }

    @Test
    @DisplayName("9. send() Errors - Should process and extract backend error payloads when status is non-2xx")
    void handle_ApiException_Parsing() {
        stubFor(get(urlEqualTo("/api/auctions/999"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Resource not found on backend system.\"}")));

        ApiException exception = assertThrows(ApiException.class, () -> apiService.getById(999));
        assertEquals("Resource not found on backend system.", exception.getMessage());
    }

    @Test
    @DisplayName("10. placeBid() Errors - Should handle validation exceptions with accurate server reasons")
    void placeBid_ServerRejection_ThrowsApiException() throws Exception {
        stubFor(post(urlEqualTo("/api/auctions/89/bids"))
                .withRequestBody(containing("\"amount\":-400000.0"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Bid amount must be greater than current price.\"}")));

        ApiException exception = assertThrows(ApiException.class, () -> {
            apiService.placeBid(89, 2, -400000.0);
        });

        assertEquals("Bid amount must be greater than current price.", exception.getMessage());
    }
}