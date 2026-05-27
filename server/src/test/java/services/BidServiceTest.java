package services;

import com.group7.dto.bid.*;
import models.Auction;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BidServiceTest {

    @Mock private BidsDAO bidsDAO;
    @Mock private AuctionsDAO auctionsDAO;
    @Mock private UsersDAO usersDAO;

    private BidService bidService;

    @BeforeEach
    void setUp() {
        bidService = new BidService(bidsDAO, auctionsDAO, usersDAO);
    }

    @Test
    @DisplayName("Create bid successfully")
    void createBid_Success() {
        int auctionId = 1;
        BidRequest req = new BidRequest();
        req.setBidderId(2);
        req.setAmount(500.0);

        Auction mockAuction = mock(Auction.class);
        User mockUser = mock(User.class);

        when(mockAuction.getId()).thenReturn(auctionId);
        when(mockUser.getId()).thenReturn(2);
        when(mockUser.getFullName()).thenReturn("Test User");

        when(auctionsDAO.getById(auctionId)).thenReturn(mockAuction);
        when(usersDAO.getById(2)).thenReturn(mockUser);

        BidResponse resp = bidService.create(auctionId, req);

        assertNotNull(resp);
        verify(bidsDAO, times(1)).save(any(Bid.class));
    }
}