package models;

import models.Exceptions.AuthenticationException;
import models.Exceptions.CustomisedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Auction Manager and Seller Factory Test Suite")
public class AuctionManagementTest {

    private User owner;
    private Item item;
    private Seller sellerInterface;

    @BeforeEach
    void setUp() {
        User realUser = new User("Admin", "123", "admin@gmail.com", "0123456789", "111111", 1000.0, "avatar.png");
        realUser.setId(10);

        owner = spy(realUser);
        doReturn("Admin 123").when(owner).getFullName();
        doReturn("Admin").when(owner).getFirstName();

        item = mock(Item.class);
        when(item.getId()).thenReturn(217);
        when(item.getName()).thenReturn("Sample Item Name");
        when(item.getDescription()).thenReturn("Sample Item Description");

        when(item.getOwnerId()).thenReturn(10);

        when(item.getOwner()).thenReturn(owner);

        sellerInterface = owner;
    }

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-SellerCreatesAuctionSession")
        void testEP_ValidAuctionCreation() {
            LocalDateTime start = LocalDateTime.now().plusHours(1);
            LocalDateTime end = LocalDateTime.now().plusHours(3);

            assertDoesNotThrow(() -> {
                sellerInterface.createAuction(item, start, end);
            });
        }

        @Test
        @DisplayName("EP-Invalid-SellerIsCurrentlyBlocked")
        void testEP_SellerBlockedThrowsException() {
            doReturn(true).when(owner).isBlocked();
            LocalDateTime start = LocalDateTime.now().plusHours(1);
            LocalDateTime end = LocalDateTime.now().plusHours(3);

            assertThrows(AuthenticationException.class, () -> {
                sellerInterface.createAuction(item, start, end);
            });
        }

        @Test
        @DisplayName("EP-Invalid-MissingTimeParameters")
        void testEP_MissingTimesThrowException() {
            assertThrows(CustomisedException.class, () -> {
                sellerInterface.createAuction(item, null, null);
            });
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-Times-TerminationInThePast")
        void testBVA_EndTimeInPastThrows() {
            LocalDateTime start = LocalDateTime.now().minusHours(5);
            LocalDateTime end = LocalDateTime.now().minusHours(2);

            assertThrows(CustomisedException.class, () -> {
                sellerInterface.createAuction(item, start, end);
            });
        }

        @Test
        @DisplayName("BVA-Times-TerminationBeforeCreationTime")
        void testBVA_EndTimeBeforeStartTimeThrows() {
            LocalDateTime start = LocalDateTime.now().plusHours(5);
            LocalDateTime end = LocalDateTime.now().plusHours(2);

            assertThrows(CustomisedException.class, () -> {
                sellerInterface.createAuction(item, start, end);
            });
        }

        @Test
        @DisplayName("BVA-Times-TerminationEqualToCreationTime")
        void testBVA_EndTimeEqualToStartTimeThrows() {
            LocalDateTime targetTime = LocalDateTime.now().plusHours(2);

            assertThrows(CustomisedException.class, () -> {
                sellerInterface.createAuction(item, targetTime, targetTime);
            });
        }
    }
}