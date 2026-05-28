package services;

import models.*;
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
import repositories.*;
import repositories.impl.DaoFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransactionServiceTest {

    @Mock private TransactionDAO transactionDAO;
    @Mock private AuctionsDAO auctionsDAO;
    @Mock private UsersDAO usersDAO;

    private TransactionService transactionService;

    private MockedStatic<DaoFactory> mockedDaoFactory;

    @BeforeEach
    void setUp() {
        mockedDaoFactory = mockStatic(DaoFactory.class);
        mockedDaoFactory.when(DaoFactory::createTransactionDAO).thenReturn(transactionDAO);
        mockedDaoFactory.when(DaoFactory::createAuctionsDAO).thenReturn(auctionsDAO);
        mockedDaoFactory.when(DaoFactory::createUsersDAO).thenReturn(usersDAO);

        transactionService = new TransactionService();
    }

    @AfterEach
    void tearDown() {
        if (mockedDaoFactory != null) {
            mockedDaoFactory.close();
        }
    }

    @Test
    @DisplayName("Create pending transaction successfully when auction has a valid winner")
    void createPendingTransaction_Success() {
        int auctionId = 1;
        Auction mockAuction = mock(Auction.class);
        User mockWinner = mock(User.class);
        User mockSeller = mock(User.class);

        when(auctionsDAO.getById(auctionId)).thenReturn(mockAuction);
        when(mockAuction.getWinner()).thenReturn(mockWinner);
        when(mockWinner.getId()).thenReturn(5);
        when(mockWinner.getFullName()).thenReturn("Winner Buyer");

        when(mockAuction.getOwner()).thenReturn(mockSeller);
        when(mockSeller.getId()).thenReturn(10);
        when(mockSeller.getFullName()).thenReturn("Owner Seller");

        when(mockAuction.getCurrentPrice()).thenReturn(1000.0);
        when(transactionDAO.getPendingByAuctionAndBuyer(anyInt(), anyInt())).thenReturn(null);

        assertDoesNotThrow(() -> transactionService.createPendingTransaction(auctionId));

        verify(transactionDAO, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Throw exception when auction does not have a winner")
    void createPendingTransaction_NoWinner_ThrowsException() {
        int auctionId = 1;
        Auction mockAuction = mock(Auction.class);
        when(auctionsDAO.getById(auctionId)).thenReturn(mockAuction);
        when(mockAuction.getWinner()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> transactionService.createPendingTransaction(auctionId));
    }
}
