package APItest;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import models.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import services.ItemApiService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class ItemApiServiceTest {

    private ItemApiService itemApiService;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        System.setProperty("server.url", wmRuntimeInfo.getHttpBaseUrl());
        itemApiService = new ItemApiService();
    }

    @Test
    @DisplayName("uploadItemImage()")
    void uploadItemImage_Success(@TempDir Path tempDir) throws Exception {
        // Create a temporary mock image file
        Path tempFile = tempDir.resolve("test-image.png");
        Files.writeString(tempFile, "FakePNGImageBinaryContentData");
        File imageFile = tempFile.toFile();

        stubFor(post(urlEqualTo("/api/items/201/image"))
                .withHeader("Content-Type", containing("multipart/form-data; boundary="))
                .willReturn(aResponse().withStatus(200)));

        assertDoesNotThrow(() -> itemApiService.uploadItemImage(201, imageFile));
    }

    @Test
    @DisplayName("uploadItemImage()")
    void uploadItemImage_ServerFailure(@TempDir Path tempDir) throws Exception {
        Path tempFile = tempDir.resolve("fail-image.jpg");
        Files.writeString(tempFile, "FakeContent");
        File imageFile = tempFile.toFile();

        stubFor(post(urlEqualTo("/api/items/201/image"))
                .willReturn(aResponse().withStatus(500).withBody("Disk storage full")));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                itemApiService.uploadItemImage(201, imageFile)
        );
        assertTrue(ex.getMessage().contains("Failed to upload image: Disk storage full"));
    }

    @Test
    @DisplayName("fetchInventory()")
    void fetchInventory_WithPaging_Success() throws Exception {
        String inventoryJson = """
        [
            {
                "id": 301,
                "name": "Item A",
                "startingPrice": 10.0,
                "description": "Desc A",
                "status": "IN_AUCTION",
                "imagePath": "a.png"
            }
        ]
        """;

        stubFor(get(urlEqualTo("/api/items/owner/5?page=1&size=5"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(inventoryJson)));

        List<Item> inventory = itemApiService.fetchInventory(5, 1, 5);

        assertNotNull(inventory);
        assertEquals(1, inventory.size());
        assertEquals("Item A", inventory.get(0).getName());
    }

    @Test
    @DisplayName("getItemImageUrl() ")
    void testGetItemImageUrl() {
        String resultUrl = itemApiService.getItemImageUrl("sample.png");

        assertNotNull(resultUrl);
        assertTrue(resultUrl.endsWith("/api/items/images/sample.png"));

        assertNull(itemApiService.getItemImageUrl(null));
        assertNull(itemApiService.getItemImageUrl("   "));
        assertNull(itemApiService.getItemImageUrl("null"));
    }
}