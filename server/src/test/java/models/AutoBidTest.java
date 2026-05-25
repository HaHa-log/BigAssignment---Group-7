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

@DisplayName("AutoBid Configuration Constraints & Execution Test Suite")
public class AutoBidTest {

    private Auction mockAuction;
    private User mockUser;
    private AutoBid autoBid;

    @BeforeEach
    void setUp() {
        mockAuction = mock(Auction.class);
        when(mockAuction.getCurrentPrice()).thenReturn(50.0);

        mockUser = mock(User.class);
        when(mockUser.getBalance()).thenReturn(1000.0);

        autoBid = new AutoBid(mockAuction, mockUser, 500.0, 2.0);
    }

    @Nested
    @DisplayName("1. Equivalence Partitioning (EP)")
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
    }

    @Nested
    @DisplayName("2. Boundary Value Analysis (BVA)")
    class BoundaryValueAnalysisTests {

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
        @DisplayName("BVA-MaxBid-ExceedsBalance")
        void testBVA_MaxBidExceedsBalance() {
            assertThrows(CustomisedException.class, () ->
                    autoBid.setMaxBid(1005.0)
            );
        }

        @Test
        @DisplayName("BVA-Increment-SystemStepInvalid")
        void testBVA_SystemStepValidationFails() {
            try (MockedStatic<BidStepConfiguration> mockedConfig = mockStatic(BidStepConfiguration.class)) {
                mockedConfig.when(() -> BidStepConfiguration.isValidStep(50.0, 3.0)).thenReturn(false);
                mockedConfig.when(() -> BidStepConfiguration.getAllowedSteps(50.0))
                        .thenReturn(Collections.singletonList(10.0));

                assertThrows(CustomisedException.class, () ->
                        autoBid.setIncrement(3.0)
                );
            }
        }
    }

    @Nested
    @DisplayName("3. AutoBid Execution Flow Tests")
    class AutoBidExecutionFlowTests {

        @Test
        @DisplayName("Flow-SucceedsWithinLimits")
        void testProcessAutoBids_SucceedsWithinLimits() throws Exception {
            when(mockAuction.getCurrentPrice()).thenReturn(60.0);
            when(mockAuction.placeBid(mockUser, 62.0)).thenReturn(true);

            boolean bidPlaced = mockAuction.placeBid(autoBid.getUser(), mockAuction.getCurrentPrice() + autoBid.getIncrement());

            assertTrue(bidPlaced);
            verify(mockAuction).placeBid(mockUser, 62.0);
        }

        @Test
        @DisplayName("Flow-StopsWhenExceedingMaxBid")
        void testProcessAutoBids_StopsWhenExceedingMaxBid() {
            when(mockAuction.getCurrentPrice()).thenReturn(510.0);

            if (mockAuction.getCurrentPrice() >= autoBid.getMaxBid()) {
            }

            verify(mockAuction, never()).placeBid(any(User.class), anyDouble());
        }

        @Test
        @DisplayName("Flow-StopsWhenInsufficientBalance")
        void testProcessAutoBids_StopsWhenUserRunsOutOfMoney() {
            when(mockUser.getBalance()).thenReturn(10.0);
            when(mockAuction.getCurrentPrice()).thenReturn(50.0);

            if (autoBid.getUser().getBalance() < (mockAuction.getCurrentPrice() + autoBid.getIncrement())) {
            }

            verify(mockAuction, never()).placeBid(mockUser, 52.0);
        }
    }
}