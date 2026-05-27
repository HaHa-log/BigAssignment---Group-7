package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import services.TransactionService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @Test
    void testGetByUser_ShouldReturn200() throws Exception {
        int userId = 1;
        Mockito.when(transactionService.getByUserId(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/transactions/user/" + userId))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById_ShouldReturn200() throws Exception {
        int transactionId = 1;
        Mockito.when(transactionService.getById(transactionId)).thenReturn(null);

        mockMvc.perform(get("/api/transactions/" + transactionId))
                .andExpect(status().isOk());
    }

    @Test
    void testCreatePending_ShouldReturn201() throws Exception {
        int auctionId = 1;
        Mockito.when(transactionService.createPendingTransaction(auctionId)).thenReturn(null);

        mockMvc.perform(post("/api/transactions/auction/" + auctionId + "/create-pending"))
                .andExpect(status().isCreated());
    }

    @Test
    void testConfirmReceipt_ShouldReturn200() throws Exception {
        int auctionId = 1;
        Map<String, Integer> body = new HashMap<>();
        body.put("buyerId", 10);

        Mockito.when(transactionService.confirmReceipt(auctionId, 10)).thenReturn(null);

        mockMvc.perform(post("/api/transactions/auction/" + auctionId + "/confirm-receipt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());
    }

    @Test
    void testRefund_ShouldReturn200() throws Exception {
        int transactionId = 1;
        Mockito.when(transactionService.refundTransaction(transactionId)).thenReturn(null);

        mockMvc.perform(post("/api/transactions/" + transactionId + "/refund"))
                .andExpect(status().isOk());
    }
}