package APItest;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.group7.dto.auth.LoginRequest;
import com.group7.dto.auth.RegisterRequest;
import com.group7.dto.auth.AuthResponse;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.AuthApiService;
import services.AuthSessionMapper;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class AuthApiServiceTest {

    private AuthApiService authApiService;

    private final String typicalAuthResponseJson = """
        {
            "userId": 7,
            "firstName": "Alex",
            "lastName": "Jones",
            "email": "alex.jones@example.com",
            "phoneNumber": "0999999999",
            "balance": 500.0,
            "role": "Admin",
            "avatarPath": "/images/avatars/alex.png"
        }
        """;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        System.setProperty("server.url", wmRuntimeInfo.getHttpBaseUrl());

        authApiService = new AuthApiService();
    }

    @Test
    @DisplayName("register()")
    void register_Success() throws Exception {
        stubFor(post(urlEqualTo("/api/auth/register"))
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(containing("\"email\":\"alex.jones@example.com\""))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(typicalAuthResponseJson)));

        RegisterRequest req = new RegisterRequest();
        req.setEmail("alex.jones@example.com");
        req.setPassword("securePassword123");

        AuthResponse response = authApiService.register(req);

        assertNotNull(response);
        assertEquals(7, response.getUserId());
        assertEquals("Admin", response.getRole());
    }

    @Test
    @DisplayName("login()")
    void login_Success() throws Exception {
        stubFor(post(urlEqualTo("/api/auth/login"))
                .withHeader("Content-Type", containing("application/json"))
                .withRequestBody(containing("\"password\":\"securePassword123\""))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(typicalAuthResponseJson)));

        LoginRequest req = new LoginRequest();
        req.setEmail("alex.jones@example.com");
        req.setPassword("securePassword123");

        AuthResponse response = authApiService.login(req);

        assertNotNull(response);
        assertEquals("Alex", response.getFirstName());
    }

    @Test
    @DisplayName("IllegalArgumentException")
    void auth_Field_Validation_Failure() {
        stubFor(post(urlEqualTo("/api/auth/login"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": \"Invalid email or password credentials.\"}")));

        LoginRequest req = new LoginRequest();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                authApiService.login(req)
        );
        assertEquals("Invalid email or password credentials.", exception.getMessage());
    }

    @Test
    @DisplayName("RuntimeException")
    void auth_General_Server_Failure() {
        stubFor(post(urlEqualTo("/api/auth/register"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Database Connection Drop")));

        RegisterRequest req = new RegisterRequest();

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                authApiService.register(req)
        );
        assertTrue(exception.getMessage().contains("Server error (500)"));
    }

    @Test
    @DisplayName("AuthSessionMapper")
    void testAuthSessionMapper_DomainMapping() throws Exception {
        com.fasterxml.jackson.databind.ObjectMapper testMapper = utils.ApiJson.mapper();
        AuthResponse dto = testMapper.readValue(typicalAuthResponseJson, AuthResponse.class);

        User domainUser = AuthSessionMapper.toUser(dto);

        assertNotNull(domainUser);
        assertEquals(7, domainUser.getId());
        assertEquals("Alex Jones", domainUser.getFullName());
        assertEquals("alex.jones@example.com", domainUser.getEmail());
        assertEquals("0999999999", domainUser.getPhoneNumber());
        assertEquals(500.0, domainUser.getBalance());
        assertEquals("/images/avatars/alex.png", domainUser.getAvatarPath());

        assertTrue(domainUser.isAdmin(), "User role parsing should accurately flag Admin privilege status.");

        assertFalse(domainUser.isBlocked(), "Default initialized user state should be unblocked.");
        assertEquals("server-authenticated", domainUser.getPassword(), "Placeholder session secret must match.");
    }
}