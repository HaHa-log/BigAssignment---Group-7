package services;

import com.group7.dto.user.UserResponse;
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
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {

    @Mock private UsersDAO usersDAO;
    @Mock private AuctionsDAO auctionsDAO;

    private UserService userService;

    private MockedStatic<DaoFactory> mockedDaoFactory;

    @BeforeEach
    void setUp() {
        mockedDaoFactory = mockStatic(DaoFactory.class);
        mockedDaoFactory.when(DaoFactory::createUsersDAO).thenReturn(usersDAO);
        mockedDaoFactory.when(DaoFactory::createAuctionsDAO).thenReturn(auctionsDAO);

        userService = new UserService();
    }

    @AfterEach
    void tearDown() {
        if (mockedDaoFactory != null) {
            mockedDaoFactory.close();
        }
    }

    @Test
    @DisplayName("Deposit money into account successfully")
    void deposit_Success() {
        int userId = 1;
        double amount = 500.0;
        User mockUser = mock(User.class);

        when(usersDAO.getById(userId)).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getFullName()).thenReturn("John Doe");
        when(mockUser.getBalance()).thenReturn(500.0);

        UserResponse response = userService.deposit(userId, amount);

        assertNotNull(response);
        verify(mockUser).depositMoney(amount);
        verify(usersDAO).update(mockUser);
    }

    @Test
    @DisplayName("Fail to deposit when amount is negative or zero")
    void deposit_InvalidAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.deposit(1, -50));
        assertThrows(IllegalArgumentException.class, () -> userService.deposit(1, 0));
        verify(usersDAO, never()).update(any());
    }

    @Test
    @DisplayName("Withdraw money fails due to insufficient balance")
    void withdraw_InsufficientBalance_ThrowsException() {
        int userId = 2;
        double amount = 1000.0;
        User mockUser = mock(User.class);

        when(usersDAO.getById(userId)).thenReturn(mockUser);
        when(mockUser.withdrawMoney(amount)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.withdraw(userId, amount));
        verify(usersDAO, never()).update(mockUser);
    }

    @Test
    @DisplayName("Block user account successfully")
    void blockUser_Success() {
        int userId = 3;
        User mockUser = mock(User.class);

        when(usersDAO.getById(userId)).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(userId);
        when(mockUser.getFullName()).thenReturn("Blocked User");

        UserResponse response = userService.block(userId);

        assertNotNull(response);
        verify(mockUser).setBlocked(any());
        verify(usersDAO).update(mockUser);
    }
}
