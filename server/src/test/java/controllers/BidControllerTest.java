package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import services.BidService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BidController.class)
public class BidControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BidService bidService;

    @Test
    void testGetAll_ShouldReturn200() throws Exception {
        Mockito.when(bidService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bids"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById_ValidId_ShouldReturn200() throws Exception {
        int validId = 1;
        Mockito.when(bidService.getById(validId)).thenReturn(null);

        mockMvc.perform(get("/api/bids/" + validId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetByAuctionId_ValidAuctionId_ShouldReturn200() throws Exception {
        int validAuctionId = 1;
        Mockito.when(bidService.getByAuctionId(validAuctionId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/bids/auction/" + validAuctionId))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateBid_ValidRequest_ShouldReturn201() throws Exception {
        int auctionId = 1;
        Map<String, Object> bidRequest = new HashMap<>();
        bidRequest.put("bidderId", 2);
        bidRequest.put("amount", 250.0);

        Mockito.when(bidService.create(Mockito.eq(auctionId), Mockito.any())).thenReturn(null);

        mockMvc.perform(post("/api/bids/" + auctionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bidRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateBid_InvalidAmount_ShouldReturn400() throws Exception {
        int auctionId = 1;
        Map<String, Object> bidRequest = new HashMap<>();
        bidRequest.put("bidderId", 2);
        bidRequest.put("amount", -10.0);

        Mockito.when(bidService.create(Mockito.eq(auctionId), Mockito.any()))
                .thenThrow(new IllegalArgumentException("Bid amount must be greater than zero"));

        mockMvc.perform(post("/api/bids/" + auctionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bidRequest)))
                .andExpect(status().isBadRequest());
    }
}