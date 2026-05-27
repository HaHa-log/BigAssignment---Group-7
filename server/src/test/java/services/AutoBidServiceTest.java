package services;

import com.group7.dto.bid.AutoBidRequest;
import com.group7.dto.bid.AutoBidResponse;
import models.Auction;
import models.AuctionManager;
import models.AutoBid;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repositories.AuctionsDAO;
import repositories.AutoBidDAO;
import repositories.UsersDAO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutoBidServiceTest {

    @Mock private AutoBidDAO autoBidDAO;
    @Mock private AuctionsDAO auctionsDAO;
    @Mock private UsersDAO usersDAO;
    @Mock private AuctionManager auctionManager;

    private AutoBidService autoBidService;

    private AutoBidRequest validRequest;
    private Auction mockAuction;
    private User mockUser;

    @BeforeEach
    void setUp() {
        autoBidService = new AutoBidService(autoBidDAO, auctionsDAO, usersDAO, auctionManager);

        validRequest = new AutoBidRequest();
        validRequest.setBidderId(10);
        validRequest.setMaxBid(2000.0);
        validRequest.setIncrement(100.0);

        mockAuction = mock(Auction.class);
        mockUser = mock(User.class);
    }

    @Test
    @DisplayName("Create AutoBid successfully when Auction and User are valid")
    void createOrUpdate_Success() {
        when(mockAuction.getId()).thenReturn(1);
        when(mockUser.getId()).thenReturn(10);
        when(mockUser.getFullName()).thenReturn("Nguyen A");

        when(auctionsDAO.getById(1)).thenReturn(mockAuction);
        when(usersDAO.getById(10)).thenReturn(mockUser);

        AutoBidResponse response = autoBidService.createOrUpdate(1, validRequest);

        assertNotNull(response);
        assertEquals(1, response.getAuctionId());
        assertEquals(10, response.getBidderId());
        assertEquals("Nguyen A", response.getBidderName());

        verify(autoBidDAO, times(1)).save(any(AutoBid.class));
        verify(auctionManager, times(1)).processAutoBids(eq(mockAuction), any(AutoBid.class));
    }

    @Test
    @DisplayName("Fail when creating AutoBid for a non-existent auction")
    void createOrUpdate_ThrowsException_WhenAuctionNotFound() {
        when(auctionsDAO.getById(999)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            autoBidService.createOrUpdate(999, validRequest);
        });

        assertEquals("[Error]: Auction not found.", exception.getMessage());
        verify(autoBidDAO, never()).save(any());
    }
}