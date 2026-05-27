package controllers;

import controllers.GlobalExceptionHandler;
import models.Exceptions.AuctionClosedException;
import models.Exceptions.InvalidBidException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalExceptionHandlerTest.DummyController.class)
@ContextConfiguration(classes = {
        GlobalExceptionHandlerTest.TestConfig.class,
        GlobalExceptionHandlerTest.DummyController.class
})
@Import(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerTest {

    @SpringBootApplication
    static class TestConfig {}

    @Autowired private MockMvc mockMvc;

    @RestController
    static class DummyController {
        @GetMapping("/test/invalid-bid")
        public void throwInvalidBid() {
            throw new InvalidBidException(100.0, 50.0);
        }

        @GetMapping("/test/auction-closed")
        public void throwAuctionClosed() {
            throw new AuctionClosedException("SOLD");
        }
    }

    @Test
    void handleInvalidBid_ShouldReturn400() throws Exception {
        mockMvc.perform(get("/test/invalid-bid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void handleAuctionClosed_ShouldReturn409() throws Exception {
        mockMvc.perform(get("/test/auction-closed"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Cannot perform the operation because the auction is currently in status: SOLD"));
    }
}