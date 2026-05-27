package controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import services.AuctionService;
import java.util.Collections;

import server.ServerApplication;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuctionController.class)
@ContextConfiguration(classes = {ServerApplication.class, AuctionController.class})
public class AuctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuctionService auctionService;

    @Test
    void testGetAll_WithDefaultParams_ShouldReturn200() throws Exception {
        Mockito.when(auctionService.getAll(0, 20, "ALL")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/auctions"))
                .andExpect(status().isOk());

        Mockito.verify(auctionService).getAll(0, 20, "ALL");
    }

    @Test
    void testGetAll_WithCustomParams_ShouldReturn200() throws Exception {
        Mockito.when(auctionService.getAll(1, 50, "ACTIVE")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/auctions")
                        .param("page", "1")
                        .param("size", "50")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk());

        Mockito.verify(auctionService).getAll(1, 50, "ACTIVE");
    }

    @Test
    void testGetById_ShouldReturn200() throws Exception {
        int auctionId = 1;
        Mockito.when(auctionService.getById(auctionId)).thenReturn(null);

        mockMvc.perform(get("/api/auctions/{id}", auctionId))
                .andExpect(status().isOk());

        Mockito.verify(auctionService).getById(auctionId);
    }

    @Test
    void testCreate_ShouldReturn201() throws Exception {
        String jsonRequest = "{\"itemId\":1,\"startingPrice\":100.0,\"description\":\"Sample item\"}";
        Mockito.when(auctionService.create(Mockito.any())).thenReturn(null);

        mockMvc.perform(post("/api/auctions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());

        Mockito.verify(auctionService).create(Mockito.any());
    }

    @Test
    void testPlaceBid_ShouldReturn200() throws Exception {
        int auctionId = 1;
        String jsonRequest = "{\"bidderId\":10,\"amount\":150.0}";
        Mockito.when(auctionService.placeBid(auctionId, 10, 150.0)).thenReturn(null);

        mockMvc.perform(post("/api/auctions/{id}/bids", auctionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        Mockito.verify(auctionService).placeBid(auctionId, 10, 150.0);
    }

    @Test
    void testCancel_ShouldReturn200() throws Exception {
        int auctionId = 1;
        Mockito.when(auctionService.cancel(auctionId)).thenReturn(null);

        mockMvc.perform(post("/api/auctions/{id}/cancel", auctionId))
                .andExpect(status().isOk());

        Mockito.verify(auctionService).cancel(auctionId);
    }

    @Test
    void testConfirmReceipt_ShouldReturn200() throws Exception {
        int auctionId = 1;
        String jsonRequest = "{\"buyerId\":5}";
        Mockito.when(auctionService.confirmReceipt(auctionId, 5)).thenReturn(null);

        mockMvc.perform(post("/api/auctions/{id}/confirm-receipt", auctionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        Mockito.verify(auctionService).confirmReceipt(auctionId, 5);
    }
}