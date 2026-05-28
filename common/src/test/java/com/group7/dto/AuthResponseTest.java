package com.group7.dto;

import com.group7.dto.auth.AuthResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthResponse DTO Test Suite")
public class AuthResponseTest {

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-ParameterizedConstructor")
        void testEP_ParameterizedConstructor() {
            AuthResponse response = new AuthResponse(
                    1,
                    "Nguyen",
                    "Van A",
                    "Nguyen Van A",
                    "vana@gmail.com",
                    "0123456789",
                    "USER",
                    2500.0,
                    "vana_avatar.png"
            );

            assertEquals(1, response.getUserId());
            assertEquals("Nguyen", response.getFirstName());
            assertEquals("Van A", response.getLastName());
            assertEquals("Nguyen Van A", response.getFullName());
            assertEquals("vana@gmail.com", response.getEmail());
            assertEquals("0123456789", response.getPhoneNumber());
            assertEquals("USER", response.getRole());
            assertEquals(2500.0, response.getBalance());
            assertEquals("vana_avatar.png", response.getAvatarPath());
        }

        @Test
        @DisplayName("EP-Valid-SettersAndGetters")
        void testEP_SettersAndGetters() {
            AuthResponse response = new AuthResponse();

            response.setUserId(20);
            response.setFirstName("Nguyen");
            response.setLastName("Thi B");
            response.setFullName("Nguyen Thi B");
            response.setEmail("thib@gmail.com");
            response.setPhoneNumber("0987654321");
            response.setRole("ADMIN");
            response.setBalance(7000.0);
            response.setAvatarPath("thib_avatar.png");

            assertEquals(20, response.getUserId());
            assertEquals("Nguyen", response.getFirstName());
            assertEquals("Thi B", response.getLastName());
            assertEquals("Nguyen Thi B", response.getFullName());
            assertEquals("thib@gmail.com", response.getEmail());
            assertEquals("0987654321", response.getPhoneNumber());
            assertEquals("ADMIN", response.getRole());
            assertEquals(7000.0, response.getBalance());
            assertEquals("thib_avatar.png", response.getAvatarPath());
        }

        @Test
        @DisplayName("EP-Invalid-NullFields")
        void testEP_NullFields() {
            AuthResponse response = new AuthResponse();

            response.setFirstName(null);
            response.setLastName(null);
            response.setFullName(null);

            assertNull(response.getFirstName());
            assertNull(response.getLastName());
            assertNull(response.getFullName());
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-Balance-ZeroValue")
        void testBVA_ZeroBalance() {
            AuthResponse response = new AuthResponse();
            response.setBalance(0.0);

            assertEquals(0.0, response.getBalance());
        }

        @Test
        @DisplayName("BVA-UserId-MinimumValue")
        void testBVA_MinUserId() {
            AuthResponse response = new AuthResponse();
            response.setUserId(Integer.MIN_VALUE);

            assertEquals(Integer.MIN_VALUE, response.getUserId());
        }

        @Test
        @DisplayName("BVA-UserId-MaximumValue")
        void testBVA_MaxUserId() {
            AuthResponse response = new AuthResponse();
            response.setUserId(Integer.MAX_VALUE);

            assertEquals(Integer.MAX_VALUE, response.getUserId());
        }
    }
}