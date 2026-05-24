package models;

import models.Exceptions.AuctionClosedException;
import models.Exceptions.AuthenticationException;
import models.Exceptions.InvalidBidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Auction PlaceBid Logic Test Suite")
public class BidPlaceBidTest {

    private User owner;
    private User bidder;
    private Item item;
    private Auction auction;

    @BeforeEach
    void setUp() {
        owner = mock(User.class);
        when(owner.getId()).thenReturn(2);
        when(owner.getEmail()).thenReturn("admin@gmail.com");
        when(owner.getPhoneNumber()).thenReturn("0123456789");
        when(owner.getFirstName()).thenReturn("Admin");
        when(owner.getLastName()).thenReturn("123");
        when(owner.getFullName()).thenReturn("Admin 123");
        when(owner.getPassword()).thenReturn("111111");

        bidder = mock(User.class);
        when(bidder.getId()).thenReturn(2);
        when(bidder.getEmail()).thenReturn("admin@gmail.com");
        when(bidder.getPhoneNumber()).thenReturn("0123456789");
        when(bidder.getFirstName()).thenReturn("Admin");
        when(bidder.getLastName()).thenReturn("123");
        when(bidder.getFullName()).thenReturn("Admin 123");
        when(bidder.getPassword()).thenReturn("111111");

        when(bidder.isEqual(owner)).thenReturn(false);
        when(bidder.freezeMoney(anyDouble())).thenReturn(true);
        when(bidder.unfreezeMoney(anyDouble())).thenReturn(true);
        when(bidder.getHighestBid(any())).thenReturn(0.0);

        item = mock(Item.class);
        when(item.getId()).thenReturn(217);
        when(item.getStartingPrice()).thenReturn(100.0);

        auction = new Auction(owner, item, Auction.AuctionStatus.RUNNING, LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));
        auction.setAuctionId(89);
    }

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-StandardBid")
        void testEP_ValidStandardBid() throws Exception {
            double bidAmount = 150.0;
            boolean result = auction.placeBid(bidder, bidAmount);

            assertTrue(result);
            assertEquals(bidAmount, auction.getCurrentPrice());
            assertEquals(bidder, auction.getWinner());
        }

        @Test
        @DisplayName("EP-Invalid-BidderIsOwner")
        void testEP_BidderIsOwner() {
            when(bidder.isEqual(owner)).thenReturn(true);

            assertThrows(AuthenticationException.class, () -> {
                auction.placeBid(bidder, 150.0);
            });
        }

        @Test
        @DisplayName("EP-Invalid-AuctionNotRunning")
        void testEP_AuctionNotRunning() {
            auction.transitionTo(Auction.AuctionStatus.FINISHED);

            assertThrows(AuctionClosedException.class, () -> {
                auction.placeBid(bidder, 150.0);
            });
        }

        @Test
        @DisplayName("EP-Invalid-InsufficientBalance")
        void testEP_InsufficientBalance() {
            when(bidder.freezeMoney(anyDouble())).thenReturn(false);

            assertThrows(IllegalArgumentException.class, () -> {
                auction.placeBid(bidder, 150.0);
            });
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-BelowCurrentPrice")
        void testBVA_BelowCurrentPrice() {
            double bidAmount = 99.99;

            assertThrows(InvalidBidException.class, () -> {
                auction.placeBid(bidder, bidAmount);
            });
        }

        @Test
        @DisplayName("BVA-EqualToCurrentPrice")
        void testBVA_EqualToCurrentPrice() {
            double bidAmount = 100.0;

            assertThrows(InvalidBidException.class, () -> {
                auction.placeBid(bidder, bidAmount);
            });
        }

        @Test
        @DisplayName("BVA-JustAboveCurrentPrice")
        void testBVA_JustAboveCurrentPrice() throws Exception {
            double bidAmount = 110.0;
            boolean result = auction.placeBid(bidder, bidAmount);

            assertTrue(result);
            assertEquals(bidAmount, auction.getCurrentPrice());
        }

        @Test
        @DisplayName("BVA-NegativeBidValue")
        void testBVA_NegativeBidValue() {
            double bidAmount = -1.0;

            assertThrows(InvalidBidException.class, () -> {
                auction.placeBid(bidder, bidAmount);
            });
        }
    }
}