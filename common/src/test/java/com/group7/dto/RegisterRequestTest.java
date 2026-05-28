package com.group7.dto;

import com.group7.dto.auth.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RegisterRequest DTO Test Suite")
public class RegisterRequestTest {

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-ParameterizedConstructor")
        void testEP_ParameterizedConstructor() {
            RegisterRequest request = new RegisterRequest(
                    "Nguyen",
                    "Van A",
                    "vana@gmail.com",
                    "0123456789",
                    "password123",
                    "avatar.png"
            );

            assertEquals("Nguyen", request.getFirstName());
            assertEquals("Van A", request.getLastName());
            assertEquals("vana@gmail.com", request.getEmail());
            assertEquals("0123456789", request.getPhoneNumber());
            assertEquals("password123", request.getPassword());
            assertEquals("avatar.png", request.getAvatarPath());
        }

        @Test
        @DisplayName("EP-Valid-SettersAndGetters")
        void testEP_SettersAndGetters() {
            RegisterRequest request = new RegisterRequest();

            request.setFirstName("Nguyen");
            request.setLastName("Thi B");
            request.setEmail("thib@gmail.com");
            request.setPhoneNumber("0999999999");
            request.setPassword("securePassword");
            request.setAvatarPath("thib.png");

            assertEquals("Nguyen", request.getFirstName());
            assertEquals("Thi B", request.getLastName());
            assertEquals("thib@gmail.com", request.getEmail());
            assertEquals("0999999999", request.getPhoneNumber());
            assertEquals("securePassword", request.getPassword());
            assertEquals("thib.png", request.getAvatarPath());
        }

        @Test
        @DisplayName("EP-Invalid-NullFields")
        void testEP_NullFields() {
            RegisterRequest request = new RegisterRequest();

            request.setFirstName(null);
            request.setEmail(null);
            request.setPassword(null);

            assertNull(request.getFirstName());
            assertNull(request.getEmail());
            assertNull(request.getPassword());
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-EmptyStrings")
        void testBVA_EmptyStrings() {
            RegisterRequest request = new RegisterRequest("", "", "", "", "", "");

            assertEquals("", request.getFirstName());
            assertEquals("", request.getLastName());
            assertEquals("", request.getEmail());
            assertEquals("", request.getPhoneNumber());
            assertEquals("", request.getPassword());
            assertEquals("", request.getAvatarPath());
        }

        @Test
        @DisplayName("BVA-LongEmailValue")
        void testBVA_LongEmailValue() {
            String longEmail = "a".repeat(255) + "@gmail.com";
            RegisterRequest request = new RegisterRequest();
            request.setEmail(longEmail);

            assertEquals(longEmail, request.getEmail());
        }
    }
}