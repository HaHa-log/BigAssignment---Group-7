package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Bidder Operational Bounds Test Suite")
public class BidderInteractionTest {

    private User userBidder;
    private Auction auction;
    private Bidder bidderInterface;

    @BeforeEach
    void setUp() {
        User linhUser = new User("Linh", "Ha", "buihalinh@gmail.com", "0835361207", "Ha060108", 9999999999.0, "linh.png");
        linhUser.setId(6);

        userBidder = linhUser;
        bidderInterface = userBidder;

        auction = mock(Auction.class);
        when(auction.getId()).thenReturn(133);
    }

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-BidAmountBelowBalanceLimit")
        void testEP_PlaceBidWithinBalance() {
            boolean result = bidderInterface.placeBid(auction, 300.0);
            assertTrue(result);
            verify(auction).placeBid(userBidder, 300.0);
        }

        @Test
        @DisplayName("EP-Invalid-BidAmountExceedsBalanceLimit")
        void testEP_BidExceedingBalanceThrows() {
            double overBalanceAmount = 99999999999.0;

            assertThrows(IllegalArgumentException.class, () -> {
                bidderInterface.placeBid(auction, overBalanceAmount);
            });
        }

        @Test
        @DisplayName("EP-Invalid-ExecutionInterceptedByAccountBlock")
        void testEP_BlockedBidderThrows() {
            User blockedBurger = new User("Hamburger", "Toyota", "burger@gmail.com", "0832195613", "hamburger", 500.0, "burger.png");
            blockedBurger.setId(3);

            blockedBurger.setBlocked(java.time.LocalDateTime.now().plusDays(1));

            Bidder blockedBidderInterface = blockedBurger;

            assertThrows(IllegalArgumentException.class, () -> {
                blockedBidderInterface.placeBid(auction, 100.0);
            });
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-Bid-ZeroAmountLimit")
        void testBVA_BidValueZeroThrows() {
            assertThrows(IllegalArgumentException.class, () -> {
                bidderInterface.placeBid(auction, 0.0);
            });
        }

        @Test
        @DisplayName("BVA-Bid-NegativeAmountLimit")
        void testBVA_BidValueNegativeThrows() {
            assertThrows(IllegalArgumentException.class, () -> {
                bidderInterface.placeBid(auction, -50.0);
            });
        }

        @Test
        @DisplayName("BVA-Bid-ExactBalanceLimitValue")
        void testBVA_BidValueExactBalanceSucceeds() {
            boolean result = bidderInterface.placeBid(auction, 9999999999.0);
            assertTrue(result);
            verify(auction).placeBid(userBidder, 9999999999.0);
        }
    }
}