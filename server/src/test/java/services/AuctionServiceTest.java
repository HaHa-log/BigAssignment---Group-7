package services;

import com.group7.dto.auction.AuctionResponse;
import config.BidWebSocketHandler;
import models.Auction;
import models.AuctionManager;
import models.Bid;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repositories.AuctionsDAO;
import repositories.BidsDAO;
import repositories.UsersDAO;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuctionServiceTest {

    @Mock private TransactionService transactionService;
    @Mock private BidWebSocketHandler webSocketHandler;
    @Mock private AuctionsDAO auctionsDAO;
    @Mock private UsersDAO usersDAO;
    @Mock private BidsDAO bidsDAO;
    @Mock private ItemService itemService;
    @Mock private AuctionManager auctionManager;

    private AuctionService auctionService;

    @BeforeEach
    void setUp() {
        auctionService = new AuctionService(
                auctionsDAO,
                usersDAO,
                bidsDAO,
                itemService,
                transactionService,
                webSocketHandler,
                auctionManager
        );
    }

    @Test
    @DisplayName("Place bid successfully and broadcast via WebSocket")
    void placeBid_Success() {
        int auctionId = 100;
        int bidderId = 5;
        double bidAmount = 1500.0;

        Auction mockAuction = mock(Auction.class);
        User mockBidder = mock(User.class);

        when(mockAuction.getId()).thenReturn(auctionId);
        when(mockAuction.getCurrentPrice()).thenReturn(bidAmount);

        when(auctionsDAO.getById(auctionId)).thenReturn(mockAuction);
        when(usersDAO.getById(bidderId)).thenReturn(mockBidder);
        when(bidsDAO.getByAuctionId(auctionId)).thenReturn(Collections.emptyList());

        AuctionResponse response = auctionService.placeBid(auctionId, bidderId, bidAmount);

        assertNotNull(response);

        verify(mockBidder, times(1)).placeBid(mockAuction, bidAmount);
        verify(bidsDAO, times(1)).save(any(Bid.class));
        verify(auctionManager, times(1)).processAutoBids(eq(mockAuction), isNull());
        verify(webSocketHandler, times(1)).broadcastBid(auctionId, bidAmount);
    }

    @Test
    @DisplayName("Confirm receipt successfully from the buyer")
    void confirmReceipt_Success() {
        int auctionId = 1;
        int buyerId = 2;
        User mockBuyer = mock(User.class);
        Auction mockAuction = mock(Auction.class);

        when(usersDAO.getById(buyerId)).thenReturn(mockBuyer);
        when(auctionsDAO.getById(auctionId)).thenReturn(mockAuction);

        AuctionResponse response = auctionService.confirmReceipt(auctionId, buyerId);

        assertNotNull(response);
        verify(transactionService, times(1)).confirmReceipt(auctionId, buyerId);
    }
}