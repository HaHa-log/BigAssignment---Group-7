package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Financial Operations Test Suite")
public class UserFinanceTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Admin", "123", "admin@gmail.com", "0123456789", "111111", 1000.0, "avatar.png");
        user.setId(2);
    }

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-FreezeMoneyWithinBalance")
        void testEP_ValidFreeze() {
            boolean success = user.freezeMoney(400.0);
            assertTrue(success);
            assertEquals(600.0, user.getBalance());
            assertEquals(400.0, user.getFrozenBalance());
        }

        @Test
        @DisplayName("EP-Invalid-FreezeMoneyExceedingBalance")
        void testEP_InvalidFreezeExceeding() {
            boolean success = user.freezeMoney(1200.0);
            assertFalse(success);
            assertEquals(1000.0, user.getBalance());
            assertEquals(0.0, user.getFrozenBalance());
        }

        @Test
        @DisplayName("EP-Valid-SpendFrozenMoney")
        void testEP_ValidSpendFrozen() {
            user.freezeMoney(500.0);
            boolean success = user.spendFrozenMoney(300.0);
            assertTrue(success);
            assertEquals(200.0, user.getFrozenBalance());
            assertEquals(500.0, user.getBalance());
        }

        @Test
        @DisplayName("EP-Invalid-SpendFrozenMoneyExceedingAvailable")
        void testEP_InvalidSpendFrozenExceeding() {
            user.freezeMoney(200.0);
            boolean success = user.spendFrozenMoney(300.0);
            assertFalse(success);
            assertEquals(200.0, user.getFrozenBalance());
        }

        @Test
        @DisplayName("EP-Valid-UnfreezeMoney")
        void testEP_ValidUnfreeze() {
            user.freezeMoney(500.0);
            boolean success = user.unfreezeMoney(300.0);
            assertTrue(success);
            assertEquals(200.0, user.getFrozenBalance());
            assertEquals(800.0, user.getBalance());
        }

        @Test
        @DisplayName("EP-Invalid-UnfreezeMoneyExceedingAvailable")
        void testEP_InvalidUnfreezeExceeding() {
            user.freezeMoney(200.0);
            boolean success = user.unfreezeMoney(300.0);
            assertFalse(success);
            assertEquals(200.0, user.getFrozenBalance());
            assertEquals(800.0, user.getBalance());
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-Freeze-ZeroAmount")
        void testBVA_FreezeZero() {
            assertFalse(user.freezeMoney(0.0));
        }

        @Test
        @DisplayName("BVA-Freeze-NegativeAmount")
        void testBVA_FreezeNegative() {
            assertFalse(user.freezeMoney(-10.0));
        }

        @Test
        @DisplayName("BVA-Freeze-ExactBalanceAmount")
        void testBVA_FreezeExactBalance() {
            assertTrue(user.freezeMoney(1000.0));
            assertEquals(0.0, user.getBalance());
            assertEquals(1000.0, user.getFrozenBalance());
        }

        @Test
        @DisplayName("BVA-SpendFrozen-ExactAmount")
        void testBVA_SpendFrozenExact() {
            user.freezeMoney(500.0);
            assertTrue(user.spendFrozenMoney(500.0));
            assertEquals(0.0, user.getFrozenBalance());
        }

        @Test
        @DisplayName("BVA-Unfreeze-ExactAmount")
        void testBVA_UnfreezeExact() {
            user.freezeMoney(500.0);
            assertTrue(user.unfreezeMoney(500.0));
            assertEquals(0.0, user.getFrozenBalance());
            assertEquals(1000.0, user.getBalance());
        }
    }
}