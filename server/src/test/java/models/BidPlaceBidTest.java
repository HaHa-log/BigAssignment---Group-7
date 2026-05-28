package models;

import models.Exceptions.AuctionClosedException;
import models.Exceptions.AuthenticationException;
import models.Exceptions.InvalidBidException;
import repositories.impl.BidsDAOImpl;
import repositories.impl.UsersDAOImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

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
        when(owner.getEmail()).thenReturn("owner@gmail.com");
        when(owner.getPhoneNumber()).thenReturn("0123456789");

        bidder = mock(User.class);
        when(bidder.getId()).thenReturn(3);
        when(bidder.getEmail()).thenReturn("bidder@gmail.com");
        when(bidder.getPhoneNumber()).thenReturn("0987654321");

        when(bidder.isEqual(owner)).thenReturn(false);
        when(bidder.freezeMoney(anyDouble())).thenReturn(true);
        when(bidder.unfreezeMoney(anyDouble())).thenReturn(true);
        when(bidder.getHighestBid(any())).thenReturn(0.0);

        item = mock(Item.class);
        when(item.getId()).thenReturn(217);
        when(item.getStartingPrice()).thenReturn(100.0);

        auction = new Auction(owner, item, Auction.AuctionStatus.RUNNING,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1));
        auction.setAuctionId(89);
        auction.setCurrentPrice(100.0);
    }

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-StandardBid")
        void testEP_ValidStandardBid() throws Exception {
            // Mock both DAOs to prevent any database interaction
            try (MockedConstruction<BidsDAOImpl> mockedBids = mockConstruction(BidsDAOImpl.class);
                 MockedConstruction<UsersDAOImpl> mockedUsers = mockConstruction(UsersDAOImpl.class)) {

                double bidAmount = 150.0;
                boolean result = auction.placeBid(bidder, bidAmount);

                assertTrue(result);
                assertEquals(bidAmount, auction.getCurrentPrice());
                assertEquals(bidder, auction.getWinner());
            }
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
            try (MockedConstruction<BidsDAOImpl> mockedBids = mockConstruction(BidsDAOImpl.class);
                 MockedConstruction<UsersDAOImpl> mockedUsers = mockConstruction(UsersDAOImpl.class)) {

                double bidAmount = 100.01;
                boolean result = auction.placeBid(bidder, bidAmount);

                assertTrue(result);
                assertEquals(bidAmount, auction.getCurrentPrice());
            }
        }

        @Test
        @DisplayName("BVA-NegativeBidValue")
        void testBVA_NegativeBidValue() {
            assertThrows(InvalidBidException.class, () -> {
                auction.placeBid(bidder, -1.0);
            });
        }
    }
}