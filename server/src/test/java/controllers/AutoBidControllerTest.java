package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import services.AutoBidService;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AutoBidController.class)
class AutoBidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AutoBidService autoBidService;

    @Test
    void testEnableAutoBid_ValidRequest_ShouldReturn201() throws Exception {
        int auctionId = 1;
        Map<String, Object> autoBidRequest = new HashMap<>();
        autoBidRequest.put("userId", 5);
        autoBidRequest.put("maxBid", 1000.0);
        autoBidRequest.put("increment", 50.0);

        Mockito.when(autoBidService.createOrUpdate(Mockito.eq(auctionId), Mockito.any())).thenReturn(null);

        mockMvc.perform(post("/api/autobids/" + auctionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(autoBidRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void testEnableAutoBid_InvalidArgument_ShouldReturn400() throws Exception {
        int auctionId = 1;
        Map<String, Object> autoBidRequest = new HashMap<>();
        autoBidRequest.put("userId", 5);
        autoBidRequest.put("maxBid", 1000.0);
        autoBidRequest.put("increment", -5.0);

        Mockito.when(autoBidService.createOrUpdate(Mockito.eq(auctionId), Mockito.any()))
                .thenThrow(new IllegalArgumentException("Step must be greater than 0."));

        mockMvc.perform(post("/api/autobids/" + auctionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(autoBidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Step must be greater than 0."));
    }

    @Test
    void testEnableAutoBid_SystemError_ShouldReturn500() throws Exception {
        int auctionId = 1;
        Map<String, Object> autoBidRequest = new HashMap<>();
        autoBidRequest.put("userId", 5);
        autoBidRequest.put("maxBid", 1000.0);
        autoBidRequest.put("increment", 50.0);

        Mockito.when(autoBidService.createOrUpdate(Mockito.eq(auctionId), Mockito.any()))
                .thenThrow(new RuntimeException("Database connection failure"));

        mockMvc.perform(post("/api/autobids/" + auctionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(autoBidRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Unexpected error: Database connection failure"));
    }

    @Test
    void testGetByAuctionId_ShouldReturn200() throws Exception {
        int auctionId = 1;
        Mockito.when(autoBidService.getByAuctionId(auctionId)).thenReturn(null);

        mockMvc.perform(get("/api/autobids/auction/" + auctionId))
                .andExpect(status().isOk());
    }
}