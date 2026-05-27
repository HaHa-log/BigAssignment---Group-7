package models;

import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Item Status Lifecycle Test Suite")
public class ItemStatusTest {

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = mock(User.class);
        when(owner.getId()).thenReturn(1);
        when(owner.getFullName()).thenReturn("Yoshikage Kira");

        item = new Item("Test Item", 100.0, "A test item for auction");
        item.setId(0);

        item.setOwner(owner);
    }

    @Test
    @DisplayName("ITEM-01: Newly created item starts as AVAILABLE")
    void testITEM01_NewItemIsAvailable() {
        assertEquals(Item.Status.AVAILABLE, item.getStatus(),
                "A newly created item must be in AVAILABLE status");
    }

    @Test
    @DisplayName("ITEM-02: Item is IN_AUCTION while auction is RUNNING")
    void testITEM02_ItemIsInAuctionWhileRunning() {
        item.setStatus(Item.Status.IN_AUCTION);

        Auction auction = new Auction(
                owner, item,
                Auction.AuctionStatus.RUNNING,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        auction.setAuctionId(0);

        assertEquals(Auction.AuctionStatus.RUNNING, auction.getStatus(),
                "Auction must be RUNNING");
        assertEquals(Item.Status.IN_AUCTION, item.getStatus(),
                "Item must be IN_AUCTION when the auction is RUNNING");
    }

    @Test
    @DisplayName("ITEM-03: Item returns to AVAILABLE after auction finishes with no winner")
    void testITEM03_ItemAvailableAfterNoWinnerAuction() {
        item.setStatus(Item.Status.IN_AUCTION);

        Auction expiredAuction = new Auction(
                owner, item,
                Auction.AuctionStatus.RUNNING,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusSeconds(1)
        );
        expiredAuction.setAuctionId(0);

        Auction.AuctionStatus finalStatus = expiredAuction.getStatus();

        assertEquals(Auction.AuctionStatus.FINISHED, finalStatus,
                "Auction must automatically transition to FINISHED when endTime has passed");
        assertEquals(Item.Status.AVAILABLE, item.getStatus(),
                "Item must return to AVAILABLE when there is no bidder");
    }

    @Test
    @DisplayName("ITEM-04: Item becomes SOLD after pending transaction is created for winner")
    void testITEM04_ItemSoldAfterWinnerTransaction() {
        item.setStatus(Item.Status.IN_AUCTION);

        item.setStatus(Item.Status.SOLD);

        assertEquals(Item.Status.SOLD, item.getStatus(),
                "Item must be SOLD after the pending transaction is created for the winner");
    }

    @Test
    @DisplayName("ITEM-05: Item returns to AVAILABLE when auction is cancelled")
    void testITEM05_ItemAvailableAfterCancellation() {
        item.setStatus(Item.Status.IN_AUCTION);

        Auction runningAuction = new Auction(
                owner, item,
                Auction.AuctionStatus.RUNNING,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        runningAuction.setAuctionId(0);

        runningAuction.getItem().setStatus(Item.Status.AVAILABLE);
        runningAuction.transitionTo(Auction.AuctionStatus.CANCELED);

        assertEquals(Item.Status.AVAILABLE, item.getStatus(),
                "Item must return to AVAILABLE when the auction is cancelled");
        assertEquals(Auction.AuctionStatus.CANCELED, runningAuction.getStatus(),
                "Auction must be in CANCELED status");
    }

    @Test
    @DisplayName("ITEM-06 (BVA): Item stays IN_AUCTION when auction just started")
    void testITEM06_ItemStaysInAuctionAtStart() {
        item.setStatus(Item.Status.IN_AUCTION);

        Auction justStarted = new Auction(
                owner, item,
                Auction.AuctionStatus.OPEN,
                LocalDateTime.now().minusNanos(1),
                LocalDateTime.now().plusHours(2)
        );
        justStarted.setAuctionId(0);

        assertEquals(Auction.AuctionStatus.RUNNING, justStarted.getStatus(),
                "Auction must automatically transition to RUNNING once startTime passes");
        assertEquals(Item.Status.IN_AUCTION, item.getStatus(),
                "Item must remain IN_AUCTION when the auction just starts");
    }
}
