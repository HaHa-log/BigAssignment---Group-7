package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import services.FileStorageService;
import services.ItemService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    void testGetAllItems_ShouldReturn200() throws Exception {
        Mockito.when(itemService.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetItemById_ShouldReturn200() throws Exception {
        int itemId = 1;
        Mockito.when(itemService.getById(itemId)).thenReturn(null);
        mockMvc.perform(get("/api/items/" + itemId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetItemsByOwnerId_ShouldReturn200() throws Exception {
        int ownerId = 1;
        Mockito.when(itemService.getByOwnerId(ownerId)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/items/owner/" + ownerId))
                .andExpect(status().isOk());
    }

    @Test
    void testUploadItemImage_ShouldReturn200() throws Exception {
        int itemId = 1;
        MockMultipartFile file = new MockMultipartFile(
                "file", "image.png", MediaType.IMAGE_PNG_VALUE, "fake image data".getBytes()
        );

        Mockito.when(fileStorageService.saveItemImage(Mockito.any(), Mockito.eq(itemId))).thenReturn("image.png");
        Mockito.when(itemService.updateImage(Mockito.eq(itemId), Mockito.anyString())).thenReturn(null);

        mockMvc.perform(multipart("/api/items/" + itemId + "/image").file(file))
                .andExpect(status().isOk());
    }

    @Test
    void testGetItemImage_FileNotExists_ShouldReturn404() throws Exception {
        String filename = "nonexistent.jpg";
        Path mockPath = Path.of("nonexistent_directory/" + filename);
        Mockito.when(fileStorageService.resolveItem(filename)).thenReturn(mockPath);

        mockMvc.perform(get("/api/items/images/" + filename))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetItemImage_Success_ShouldReturn200() throws Exception {
        String filename = "test.png";
        Path tempFile = Files.createTempFile("test_image", ".png");
        Files.writeString(tempFile, "dummy content");

        try {
            Mockito.when(fileStorageService.resolveItem(filename)).thenReturn(tempFile);

            mockMvc.perform(get("/api/items/images/" + filename))
                    .andExpect(status().isOk());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testUpdateItem_ShouldReturn200() throws Exception {
        int itemId = 1;
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("name", "Updated Item Name");
        updateRequest.put("description", "Updated Description");

        Mockito.when(itemService.update(Mockito.eq(itemId), Mockito.any())).thenReturn(null);

        mockMvc.perform(put("/api/items/" + itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteItem_ShouldReturn204() throws Exception {
        int itemId = 1;
        Mockito.doNothing().when(itemService).delete(itemId);

        mockMvc.perform(delete("/api/items/" + itemId))
                .andExpect(status().isNoContent());
    }
}