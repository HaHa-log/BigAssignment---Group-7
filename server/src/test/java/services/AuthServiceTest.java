package services;

import com.group7.dto.auth.*;
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
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthServiceTest {

    @Mock
    private UsersDAO userdbMock;

    private AuthService authService;

    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;

    private MockedStatic<DaoFactory> mockedDaoFactory;

    @BeforeEach
    void setUp() {
        mockedDaoFactory = mockStatic(DaoFactory.class);
        mockedDaoFactory.when(DaoFactory::createUsersDAO).thenReturn(userdbMock);

        authService = new AuthService();

        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setFirstName("Nguyen");
        validRegisterRequest.setLastName("An");
        validRegisterRequest.setEmail("nguyenan@example.com");
        validRegisterRequest.setPhoneNumber("0912345678");
        validRegisterRequest.setPassword("password123");
        validRegisterRequest.setAvatarPath("avatar.png");

        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("nguyenan@example.com");
        validLoginRequest.setPassword("password123");
    }

    @AfterEach
    void tearDown() {
        if (mockedDaoFactory != null) {
            mockedDaoFactory.close();
        }
    }

    @Test
    @DisplayName("Register successfully when information is valid and email does not exist")
    void register_Success() {
        when(userdbMock.getByEmail(validRegisterRequest.getEmail())).thenReturn(null);

        AuthResponse response = authService.register(validRegisterRequest);

        assertNotNull(response);
        assertEquals("Nguyen", response.getFirstName());
        assertEquals("nguyenan@example.com", response.getEmail());

        verify(userdbMock, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Register fail when email already exists in the system")
    void register_ThrowsException_WhenEmailExists() {
        User existingUser = new User("Khac", "A", "nguyenan@example.com", "0912345678", "123456", 0.0, "img.png");
        when(userdbMock.getByEmail(validRegisterRequest.getEmail())).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.register(validRegisterRequest);
        });

        assertEquals("[Failure]: An account with this email already exists.", exception.getMessage());

        verify(userdbMock, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Login successfully with correct email and password")
    void login_Success() {
        User mockUser = new User("Nguyen", "An", "nguyenan@example.com", "0912345678", "password123", 100.0, "avatar.png");
        when(userdbMock.getByEmail(validLoginRequest.getEmail())).thenReturn(mockUser);

        AuthResponse response = authService.login(validLoginRequest);

        assertNotNull(response);
        assertEquals("Nguyen An", response.getFullName());
    }

    @Test
    @DisplayName("Login fail when password is incorrect")
    void login_ThrowsException_WhenPasswordIncorrect() {
        User mockUser = new User("Nguyen", "An", "nguyenan@example.com", "0912345678", "password123", 100.0, "avatar.png");
        when(userdbMock.getByEmail(validLoginRequest.getEmail())).thenReturn(mockUser);

        validLoginRequest.setPassword("wrong_password");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authService.login(validLoginRequest);
        });

        assertEquals("[Failure]: Incorrect password.", exception.getMessage());
    }
}