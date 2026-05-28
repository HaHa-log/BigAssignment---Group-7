package com.group7.dto;

import com.group7.dto.user.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserResponse DTO Test Suite")
public class UserResponseTest {

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-ParameterizedConstructor")
        void testEP_ParameterizedConstructor() {
            UserResponse response = new UserResponse(
                    1,
                    "Nguyen Van A",
                    "vana@gmail.com",
                    "0123456789",
                    "USER",
                    false,
                    1000.0,
                    "avatar.png",
                    250.0
            );

            assertEquals(1, response.getId());
            assertEquals("Nguyen Van A", response.getFullName());
            assertEquals("vana@gmail.com", response.getEmail());
            assertEquals("0123456789", response.getPhoneNumber());
            assertEquals("USER", response.getRole());
            assertFalse(response.isBlocked());
            assertEquals(1000.0, response.getBalance());
            assertEquals("avatar.png", response.getAvatarPath());
            assertEquals(250.0, response.getFrozenBalance());
        }

        @Test
        @DisplayName("EP-Valid-FrozenBalanceModification")
        void testEP_FrozenBalanceModification() {
            UserResponse response = new UserResponse();
            response.setFrozenBalance(999.0);

            assertEquals(999.0, response.getFrozenBalance());
        }

        @Test
        @DisplayName("EP-Valid-BlockedStateVerification")
        void testEP_BlockedStateVerification() {
            UserResponse response = new UserResponse(
                    2,
                    "Nguyen Thi B",
                    "thib@gmail.com",
                    "0999999999",
                    "ADMIN",
                    true,
                    5000.0,
                    "admin.png",
                    0.0
            );

            assertTrue(response.isBlocked());
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-FrozenBalance-ZeroValue")
        void testBVA_ZeroFrozenBalance() {
            UserResponse response = new UserResponse();
            response.setFrozenBalance(0.0);

            assertEquals(0.0, response.getFrozenBalance());
        }

        @Test
        @DisplayName("BVA-UserId-MinimumValue")
        void testBVA_MinUserId() {
            UserResponse response = new UserResponse(
                    Integer.MIN_VALUE,
                    "A",
                    "a@gmail.com",
                    "0",
                    "USER",
                    false,
                    0.0,
                    null,
                    0.0
            );

            assertEquals(Integer.MIN_VALUE, response.getId());
        }

        @Test
        @DisplayName("BVA-UserId-MaximumValue")
        void testBVA_MaxUserId() {
            UserResponse response = new UserResponse(
                    Integer.MAX_VALUE,
                    "A",
                    "a@gmail.com",
                    "0",
                    "USER",
                    false,
                    0.0,
                    null,
                    0.0
            );

            assertEquals(Integer.MAX_VALUE, response.getId());
        }
    }
}