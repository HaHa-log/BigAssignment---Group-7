package models;

import models.Auction.AuctionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Auction State Transitions Test Suite")
public class AuctionStateTest {

    private User owner;
    private Item item;
    private Auction auction;

    @BeforeEach
    void setUp() {
        owner = mock(User.class);
        item = mock(Item.class);
        when(item.getStartingPrice()).thenReturn(10.0);

        auction = new Auction(owner, item, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3));
    }

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-OpenToRunning: Successfully transition from OPEN to RUNNING")
        void testEP_OpenToRunning() {
            assertEquals(AuctionStatus.OPEN, auction.getStatus());

            boolean transitioned = auction.transitionTo(AuctionStatus.RUNNING);
            assertTrue(transitioned);
            assertEquals(AuctionStatus.RUNNING, auction.getStatus());
        }

        @Test
        @DisplayName("EP-Valid-RunningToFinished: Successfully transition from RUNNING to FINISHED")
        void testEP_RunningToFinished() {
            auction.transitionTo(AuctionStatus.RUNNING);

            boolean transitioned = auction.transitionTo(AuctionStatus.FINISHED);
            assertTrue(transitioned);
            assertEquals(AuctionStatus.FINISHED, auction.getStatus());
        }

        @Test
        @DisplayName("EP-Invalid-OpenToPaid: Direct transition from OPEN to PAID is blocked")
        void testEP_OpenToPaid() {
            boolean transitioned = auction.transitionTo(AuctionStatus.PAID);

            assertFalse(transitioned);

            assertEquals(AuctionStatus.OPEN, auction.getStatus());
        }

        @Test
        @DisplayName("EP-Invalid-FinishedToRunning: Cannot roll back to RUNNING once FINISHED")
        void testEP_FinishedToRunning() {
            auction.transitionTo(AuctionStatus.RUNNING);
            auction.transitionTo(AuctionStatus.FINISHED);

            boolean transitioned = auction.transitionTo(AuctionStatus.RUNNING);
            assertFalse(transitioned);
            assertEquals(AuctionStatus.FINISHED, auction.getStatus());
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-TerminalState-Paid: PAID is terminal, cannot transition to CANCELLED")
        void testBVA_TerminalStatePaid() {
            auction.transitionTo(AuctionStatus.RUNNING);
            auction.transitionTo(AuctionStatus.FINISHED);
            auction.transitionTo(AuctionStatus.PAID);

            boolean transitioned = auction.transitionTo(AuctionStatus.CANCELLED);
            assertFalse(transitioned);
        }

        @Test
        @DisplayName("BVA-TerminalState-Canceled: CANCELLED is terminal, cannot reactivate")
        void testBVA_TerminalStateCanceled() {
            auction.transitionTo(AuctionStatus.CANCELLED);

            boolean transitioned = auction.transitionTo(AuctionStatus.RUNNING);
            assertFalse(transitioned);
        }

        @Test
        @DisplayName("BVA-TimedStatus-TriggerStart: System automatically triggers RUNNING if starting time has passed")
        void testBVA_TimedStatusTriggerStart() {
            auction.setStartingTime(LocalDateTime.now().minusMinutes(5));
            assertEquals(AuctionStatus.RUNNING, auction.getStatus());
        }

        @Test
        @DisplayName("BVA-TimedStatus-BeforeStart: System remains OPEN if starting time is still in the future")
        void testBVA_TimedStatusBeforeStart() {
            auction.setStartingTime(LocalDateTime.now().plusMinutes(5));
            auction.start();
            assertEquals(AuctionStatus.OPEN, auction.getStatus());
        }
    }
}