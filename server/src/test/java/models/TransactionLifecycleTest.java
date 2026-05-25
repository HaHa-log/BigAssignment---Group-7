package models;

import models.Exceptions.IllegalTransactionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Transaction Lifecycle State & Execution Test Suite")
public class TransactionLifecycleTest {

    private Auction auction;
    private User buyer;
    private User seller;
    private Item item;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        buyer = mock(User.class);
        when(buyer.getId()).thenReturn(9);
        when(buyer.getEmail()).thenReturn("maiuyen@gmail.com");
        when(buyer.getPhoneNumber()).thenReturn("0842377626");
        when(buyer.getFirstName()).thenReturn("Uyen");
        when(buyer.getLastName()).thenReturn("Tran");
        when(buyer.getFullName()).thenReturn("Tran Uyen");
        when(buyer.getPassword()).thenReturn("123456789");
        when(buyer.getBalance()).thenReturn(200390.5);

        seller = mock(User.class);
        when(seller.getId()).thenReturn(2);
        when(seller.getEmail()).thenReturn("admin@gmail.com");
        when(seller.getPhoneNumber()).thenReturn("0123456789");
        when(seller.getFirstName()).thenReturn("Admin");
        when(seller.getLastName()).thenReturn("123");
        when(seller.getFullName()).thenReturn("Admin 123");
        when(seller.getPassword()).thenReturn("111111");

        item = mock(Item.class);
        when(item.getId()).thenReturn(217);
        when(item.getStartingPrice()).thenReturn(100.0);

        auction = mock(Auction.class);
        when(auction.getId()).thenReturn(89);
        when(auction.getItem()).thenReturn(item);

        transaction = new Transaction(auction, buyer, seller, 200.0);
    }

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-PendingToCompleted")
        void testEP_MarkCompletedSuccessfully() throws Exception {
            when(buyer.spendFrozenMoney(200.0)).thenReturn(true);

            transaction.markCompleted();

            assertEquals(Transaction.TransactionStatus.COMPLETED, transaction.getStatus());
            assertNotNull(transaction.getCompletedAt());
            verify(seller).depositMoney(200.0);
            verify(buyer).addItem(item);
        }

        @Test
        @DisplayName("EP-Invalid-CompleteWhenBuyerBalanceInsufficient")
        void testEP_MarkCompletedInsufficientFrozenMoney() {
            when(buyer.spendFrozenMoney(200.0)).thenReturn(false);

            assertThrows(IllegalTransactionException.class, () -> {
                transaction.markCompleted();
            });
            assertEquals(Transaction.TransactionStatus.PENDING, transaction.getStatus());
        }

        @Test
        @DisplayName("EP-Valid-CompletedToRefunded")
        void testEP_MarkRefundedSuccessfully() throws Exception {
            when(buyer.spendFrozenMoney(200.0)).thenReturn(true);
            transaction.markCompleted();

            when(seller.withdrawMoney(200.0)).thenReturn(true);
            transaction.markRefunded();

            assertEquals(Transaction.TransactionStatus.REFUNDED, transaction.getStatus());
            verify(buyer).depositMoney(200.0);
            verify(seller).addItem(item);
        }

        @Test
        @DisplayName("EP-Invalid-RefundWhenSellerBalanceInsufficient")
        void testEP_MarkRefundedSellerInsufficientMoney() throws Exception {
            when(buyer.spendFrozenMoney(200.0)).thenReturn(true);
            transaction.markCompleted();

            when(seller.withdrawMoney(200.0)).thenReturn(false);

            assertThrows(IllegalTransactionException.class, () -> {
                transaction.markRefunded();
            });
            assertEquals(Transaction.TransactionStatus.COMPLETED, transaction.getStatus());
        }

        @Test
        @DisplayName("EP-Invalid-RefundWhenTransactionStillPending")
        void testEP_RefundInPendingState() {
            assertThrows(IllegalTransactionException.class, () -> {
                transaction.markRefunded();
            });
            assertEquals(Transaction.TransactionStatus.PENDING, transaction.getStatus());
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-Complete-TransactionHasExpired")
        void testBVA_CompletedAfterExpiry() {
            transaction.setExpiryTime(LocalDateTime.now().minusSeconds(1));

            assertThrows(IllegalTransactionException.class, () -> {
                transaction.markCompleted();
            });
            assertEquals(Transaction.TransactionStatus.PENDING, transaction.getStatus());
        }

        @Test
        @DisplayName("BVA-Complete-TransactionDoubleInvocation")
        void testBVA_DoubleCompletionThrows() throws Exception {
            when(buyer.spendFrozenMoney(200.0)).thenReturn(true);
            transaction.markCompleted();

            assertThrows(IllegalTransactionException.class, () -> {
                transaction.markCompleted();
            });
        }

        @Test
        @DisplayName("BVA-Complete-ExecutionRollbackOnInternalError")
        void testBVA_RollbackTriggeredByDepositException() throws Exception {
            when(buyer.spendFrozenMoney(200.0)).thenReturn(true);
            doThrow(new RuntimeException()).when(seller).depositMoney(200.0);

            assertThrows(IllegalTransactionException.class, () -> {
                transaction.markCompleted();
            });
            verify(buyer).unfreezeMoney(200.0);
            assertEquals(Transaction.TransactionStatus.PENDING, transaction.getStatus());
        }
    }
}