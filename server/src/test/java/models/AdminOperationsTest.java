package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Admin Role Authorization and Governance Test Suite")
public class AdminOperationsTest {

    private Admin admin;
    private User regularUser;

    @BeforeEach
    void setUp() {
        admin = new Admin("Admin", "123", "admin@gmail.com", "0123456789", "111111", 0.0, "admin.png");
        admin.setId(10);

        User realUser = new User("Hamburger", "Toyota", "burger@gmail.com", "0832195613", "hamburger", 0.0, "burger.png");
        realUser.setId(3);

        regularUser = spy(realUser);
    }

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-IdentityAndRoleVerification")
        void testEP_RoleProperties() {
            assertTrue(admin.isAdmin());
            assertEquals("Admin", admin.getRole());
        }

        @Test
        @DisplayName("EP-Valid-BlockTargetedUserAccount")
        void testEP_BlockUserSuccessfully() {
            LocalDateTime restrictionTime = LocalDateTime.now().plusDays(7);

            admin.blockUser(regularUser, restrictionTime);
            verify(regularUser).setBlocked(restrictionTime);
        }

        @Test
        @DisplayName("EP-Valid-UnblockTargetedUserAccount")
        void testEP_UnblockUserSuccessfully() {
            admin.unblockUser(regularUser);
            verify(regularUser).isUnblocked();
        }

        @Test
        @DisplayName("EP-Invalid-NullTargetExecutionSafety")
        void testEP_NullPointersHandling() {
            assertDoesNotThrow(() -> admin.blockUser(null, LocalDateTime.now()));
            assertDoesNotThrow(() -> admin.unblockUser(null));
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-CancelAuction-TargetSessionNotFound")
        void testBVA_CancelNonExistentAuctionReturnsFalse() {
            boolean status = admin.cancelAuction(-9999);
            assertFalse(status);
        }

        @Test
        @DisplayName("BVA-CancelAuction-ValidTargetSessionId")
        void testBVA_CancelExistingAuctionReturnsTrue() {
            boolean status = admin.cancelAuction(133);
            assertTrue(status);
        }
    }
}