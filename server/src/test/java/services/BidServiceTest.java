package services;

import com.group7.dto.bid.*;
import models.Auction;
import models.Bid;
import models.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import repositories.AuctionsDAO;
import repositories.BidsDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BidServiceTest {

    @Mock private BidsDAO bidsDAO;
    @Mock private AuctionsDAO auctionsDAO;
    @Mock private UsersDAO usersDAO;

    private BidService bidService;

    private MockedStatic<DaoFactory> mockedDaoFactory;

    @BeforeEach
    void setUp() {
        mockedDaoFactory = mockStatic(DaoFactory.class);
        mockedDaoFactory.when(DaoFactory::createBidsDAO).thenReturn(bidsDAO);
        mockedDaoFactory.when(DaoFactory::createAuctionsDAO).thenReturn(auctionsDAO);
        mockedDaoFactory.when(DaoFactory::createUsersDAO).thenReturn(usersDAO);

        bidService = new BidService();
    }

    @AfterEach
    void tearDown() {
        if (mockedDaoFactory != null) {
            mockedDaoFactory.close();
        }
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

    @Test
    @DisplayName("Fail when creating bid for a non-existent auction")
    void createBid_ThrowsException_WhenAuctionNotFound() {
        int auctionId = 999;
        BidRequest req = new BidRequest();
        req.setBidderId(2);
        req.setAmount(500.0);

        when(auctionsDAO.getById(auctionId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            bidService.create(auctionId, req);
        });

        verify(bidsDAO, never()).save(any());
    }

    @Test
    @DisplayName("Fail when bidder profile does not exist")
    void createBid_ThrowsException_WhenUserNotFound() {
        int auctionId = 1;
        BidRequest req = new BidRequest();
        req.setBidderId(999);
        req.setAmount(500.0);

        Auction mockAuction = mock(Auction.class);
        when(auctionsDAO.getById(auctionId)).thenReturn(mockAuction);
        when(usersDAO.getById(999)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            bidService.create(auctionId, req);
        });

        verify(bidsDAO, never()).save(any());
    }

}
