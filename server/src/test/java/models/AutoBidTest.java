package models;

import models.Exceptions.CustomisedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("AutoBid Configuration Constraints Test Suite")
public class AutoBidTest {

    private Auction auction;
    private User user;
    private AutoBid autoBid;

    @BeforeEach
    void setUp() {
        auction = mock(Auction.class);
        when(auction.getCurrentPrice()).thenReturn(50.0);

        user = mock(User.class);
        when(user.getBalance()).thenReturn(1000.0);

        autoBid = new AutoBid(auction, user, 500.0, 2.0);
    }

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-MaxBidAndIncrement")
        void testEP_ValidConfig() {
            try (MockedStatic<BidStepConfiguration> mockedConfig = mockStatic(BidStepConfiguration.class)) {
                mockedConfig.when(() -> BidStepConfiguration.isValidStep(50.0, 5.0)).thenReturn(true);

                autoBid.setMaxBid(400.0);
                autoBid.setIncrement(5.0);

                assertEquals(400.0, autoBid.getMaxBid());
                assertEquals(5.0, autoBid.getIncrement());
            }
        }

        @Test
        @DisplayName("EP-Invalid-MaxBidExceedsBalance")
        void testEP_MaxBidExceedsBalance() {
            assertThrows(CustomisedException.class, () ->
                    autoBid.setMaxBid(1500.0)
            );
        }

        @Test
        @DisplayName("EP-Invalid-IncrementNegativeOrZero")
        void testEP_IncrementZeroOrNegative() {
            assertThrows(CustomisedException.class, () ->
                    autoBid.setIncrement(0.0)
            );
            assertThrows(CustomisedException.class, () ->
                    autoBid.setIncrement(-1.0)
            );
        }

        @Test
        @DisplayName("EP-Invalid-IncrementHigherThanMaxBid")
        void testEP_IncrementHigherThanMaxBid() {
            try (MockedStatic<BidStepConfiguration> mockedConfig = mockStatic(BidStepConfiguration.class)) {
                mockedConfig.when(() -> BidStepConfiguration.isValidStep(anyDouble(), anyDouble())).thenReturn(true);

                assertThrows(CustomisedException.class, () ->
                        autoBid.setIncrement(600.0)
                );
            }
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-MaxBid-BelowCurrentPrice")
        void testBVA_MaxBidBelowCurrentPrice() {
            assertThrows(IllegalArgumentException.class, () ->
                    autoBid.setMaxBid(49.99)
            );
        }

        @Test
        @DisplayName("BVA-MaxBid-EqualToCurrentPrice")
        void testBVA_MaxBidEqualToCurrentPrice() {
            assertThrows(IllegalArgumentException.class, () ->
                    autoBid.setMaxBid(50.0)
            );
        }

        @Test
        @DisplayName("BVA-MaxBid-JustAboveCurrentPrice")
        void testBVA_MaxBidJustAboveCurrentPrice() {
            double validMaxBid = 50.01;
            autoBid.setMaxBid(validMaxBid);
            assertEquals(validMaxBid, autoBid.getMaxBid());
        }

        @Test
        @DisplayName("BVA-MaxBid-EqualToBalance")
        void testBVA_MaxBidExactBalance() {
            double exactBalance = 1000.0;
            autoBid.setMaxBid(exactBalance);
            assertEquals(exactBalance, autoBid.getMaxBid());
        }

        @Test
        @DisplayName("BVA-Increment-SystemStepInvalid")
        void testBVA_SystemStepValidationFails() {
            try (MockedStatic<BidStepConfiguration> mockedConfig = mockStatic(BidStepConfiguration.class)) {
                mockedConfig.when(() -> BidStepConfiguration.isValidStep(50.0, 3.0)).thenReturn(false);
                mockedConfig.when(() -> BidStepConfiguration.getAllowedSteps(50.0)).thenReturn(Collections.singletonList(1.0));

                assertThrows(CustomisedException.class, () ->
                        autoBid.setIncrement(3.0)
                );
            }
        }
    }
}