package APItest;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import exceptions.ApiException;
import models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import services.UserApiService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@WireMockTest
class UserApiServiceTest {

    private UserApiService userApiService;

    private final String typicalUserResponseJson = """
        {
            "id": 12,
            "fullName": "John Doe",
            "email": "john.doe@example.com",
            "phoneNumber": "0912345678",
            "balance": 1000.0,
            "frozenBalance": 100.0,
            "role": "User",
            "blocked": false,
            "avatarPath": "profile.png"
        }
        """;

    @BeforeEach
    void setUp(WireMockRuntimeInfo wmRuntimeInfo) {
        System.setProperty("server.url", wmRuntimeInfo.getHttpBaseUrl());
        userApiService = new UserApiService();
    }

    @Test
    @DisplayName("1. getAll() - Should deserialize a list of valid user profiles")
    void getAll_Success() throws Exception {
        stubFor(get(urlEqualTo("/api/users"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[" + typicalUserResponseJson + "]")));

        List<User> results = userApiService.getAll();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("John Doe", results.get(0).getFullName());
    }

    @Test
    @DisplayName("2. getByEmail() - Should request dynamic string parameters path accurately")
    void getByEmail_Success() throws Exception {
        stubFor(get(urlEqualTo("/api/users/email/john.doe@example.com"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(typicalUserResponseJson)));

        User result = userApiService.getByEmail("john.doe@example.com");
        assertNotNull(result);
        assertEquals(12, result.getId());
    }

    @Test
    @DisplayName("3. getById() - Should target structural resource numbers matching routing patterns")
    void getById_Success() throws Exception {
        stubFor(get(urlEqualTo("/api/users/12"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(typicalUserResponseJson)));

        User result = userApiService.getById(12);
        assertNotNull(result);
    }

    @Test
    @DisplayName("4. uploadAvatar() - Should combine custom header payload segments over multipart boundary paths")
    void uploadAvatar_MultipartSuccess(@TempDir Path tempDir) throws Exception {
        Path tempFile = tempDir.resolve("avatar.jpg");
        Files.writeString(tempFile, "FakeImageDataStreamContent");
        File filePayload = tempFile.toFile();

        stubFor(post(urlEqualTo("/api/users/12/avatar"))
                .withHeader("Content-Type", containing("multipart/form-data; boundary="))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(typicalUserResponseJson)));

        User result = userApiService.uploadAvatar(12, filePayload);
        assertNotNull(result);
    }

    @Test
    @DisplayName("5. State Management - Verify block() and unblock() execution flows")
    void testStateActions_Success() throws Exception {
        stubFor(post(urlEqualTo("/api/users/12/block"))
                .willReturn(aResponse().withStatus(200).withBody(typicalUserResponseJson)));
        stubFor(post(urlEqualTo("/api/users/12/unblock"))
                .willReturn(aResponse().withStatus(200).withBody(typicalUserResponseJson)));

        assertNotNull(userApiService.block(12));
        assertNotNull(userApiService.unblock(12));
    }

    @Test
    @DisplayName("6. Finance Subroutines - Verify deposit, withdraw, freeze, unfreeze and spendFrozen methods")
    void testFinanceActions_Success() throws Exception {
        // Targets your private shared 'postFinanceAction' block comprehensively
        String targetActions[] = {"deposit", "withdraw", "freeze", "unfreeze", "spend-frozen"};

        for (String action : targetActions) {
            stubFor(post(urlEqualTo("/api/users/12/" + action))
                    .withHeader("Content-Type", equalTo("application/json"))
                    .withRequestBody(matchingJsonPath("$.amount", equalTo("150.0")))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(typicalUserResponseJson)));
        }

        assertNotNull(userApiService.deposit(12, 150.0));
        assertNotNull(userApiService.withdraw(12, 150.0));
        assertNotNull(userApiService.freeze(12, 150.0));
        assertNotNull(userApiService.unfreeze(12, 150.0));
        assertNotNull(userApiService.spendFrozen(12, 150.0));
    }

    @Test
    @DisplayName("7. getAvatarUrl() - Scope local string formatting check loops")
    void testGetAvatarUrl_UtilityBranches() {
        String completeUrl = userApiService.getAvatarUrl("pic.png");
        assertNotNull(completeUrl);
        assertTrue(completeUrl.endsWith("/api/users/avatars/pic.png"));

        assertNull(userApiService.getAvatarUrl(null));
        assertNull(userApiService.getAvatarUrl(""));
        assertNull(userApiService.getAvatarUrl("null"));
    }

    @Test
    @DisplayName("8. Exception Handling - Extract structural JSON errors elegantly")
    void testSend_ErrorExtraction() {
        stubFor(get(urlEqualTo("/api/users/99"))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withBody("{\"error\": \"User balance cannot drop below zero bounds.\"}")));

        ApiException ex = assertThrows(ApiException.class, () -> userApiService.getById(99));
        assertEquals("User balance cannot drop below zero bounds.", ex.getMessage());
    }
}