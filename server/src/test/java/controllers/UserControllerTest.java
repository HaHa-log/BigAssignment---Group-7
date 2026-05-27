package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import services.FileStorageService;
import services.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private FileStorageService fileStorageService;

    @Test
    void testGetAllUsers_ShouldReturn200() throws Exception {
        Mockito.when(userService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById_ShouldReturn200() throws Exception {
        int userId = 1;
        Mockito.when(userService.getById(userId)).thenReturn(null);

        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByEmail_ValidFormat_ShouldReturn200() throws Exception {
        String email = "valid.user@example.com";
        Mockito.when(userService.getByEmail(email)).thenReturn(null);

        mockMvc.perform(get("/api/users/email/" + email))
                .andExpect(status().isOk());
    }

    @Test
    void testFreeze_ValidAmount_ShouldReturn200() throws Exception {
        int userId = 1;
        Map<String, Double> body = new HashMap<>();
        body.put("amount", 200.0);

        Mockito.when(userService.freeze(userId, 200.0)).thenReturn(null);

        mockMvc.perform(post("/api/users/" + userId + "/freeze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void testFreeze_InvalidAmount_ShouldReturn400() throws Exception {
        int userId = 1;
        Map<String, Double> body = new HashMap<>();
        body.put("amount", -50.0);

        Mockito.when(userService.freeze(userId, -50.0))
                .thenThrow(new IllegalArgumentException("[Error]: Deposited amount must be greater than 0"));

        mockMvc.perform(post("/api/users/" + userId + "/freeze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUnfreeze_ShouldReturn200() throws Exception {
        int userId = 1;
        Map<String, Double> body = new HashMap<>();
        body.put("amount", 100.0);

        Mockito.when(userService.unfreeze(userId, 100.0)).thenReturn(null);

        mockMvc.perform(post("/api/users/" + userId + "/unfreeze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void testSpendFrozen_ShouldReturn200() throws Exception {
        int userId = 1;
        Map<String, Double> body = new HashMap<>();
        body.put("amount", 50.0);

        Mockito.when(userService.spendFrozen(userId, 50.0)).thenReturn(null);

        mockMvc.perform(post("/api/users/" + userId + "/spend-frozen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetNotifications_ShouldReturn200() throws Exception {
        int userId = 1;
        Mockito.when(userService.getNotifications(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/" + userId + "/notifications"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetAuctionHistory_ShouldReturn200() throws Exception {
        int userId = 1;
        Mockito.when(userService.getAuctionHistory(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/" + userId + "/history"))
                .andExpect(status().isOk());
    }
}