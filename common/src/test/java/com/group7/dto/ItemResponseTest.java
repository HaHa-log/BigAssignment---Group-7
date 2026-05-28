package com.group7.dto;

import com.group7.dto.item.ItemResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemResponse DTO Test Suite")
public class ItemResponseTest {

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-ParameterizedConstructor")
        void testEP_ParameterizedConstructor() {
            ItemResponse response = new ItemResponse(
                    1,
                    "Laptop",
                    1200.0,
                    "Gaming Laptop",
                    "AVAILABLE",
                    "laptop.png",
                    10,
                    "Nguyen Van A"
            );

            assertEquals(1, response.getId());
            assertEquals("Laptop", response.getName());
            assertEquals(1200.0, response.getStartingPrice());
            assertEquals("Gaming Laptop", response.getDescription());
            assertEquals("AVAILABLE", response.getStatus());
            assertEquals("laptop.png", response.getImagePath());
            assertEquals(10, response.getOwnerId());
            assertEquals("Nguyen Van A", response.getOwnerName());
        }

        @Test
        @DisplayName("EP-Valid-SettersAndGetters")
        void testEP_SettersAndGetters() {
            ItemResponse response = new ItemResponse();

            response.setId(5);
            response.setName("Phone");
            response.setStartingPrice(900.0);
            response.setDescription("Smartphone");
            response.setStatus("SOLD");
            response.setImagePath("phone.png");
            response.setOwnerId(99);
            response.setOwnerName("Nguyen Thi B");
            response.setActiveAuctionId(1000);
            response.setCurrentAuctionPrice(1500.0);

            assertEquals(5, response.getId());
            assertEquals("Phone", response.getName());
            assertEquals(900.0, response.getStartingPrice());
            assertEquals("Smartphone", response.getDescription());
            assertEquals("SOLD", response.getStatus());
            assertEquals("phone.png", response.getImagePath());
            assertEquals(99, response.getOwnerId());
            assertEquals("Nguyen Thi B", response.getOwnerName());
            assertEquals(1000, response.getActiveAuctionId());
            assertEquals(1500.0, response.getCurrentAuctionPrice());
        }

        @Test
        @DisplayName("EP-Valid-AllStatusTransitions")
        void testEP_AllStatusTransitions() {
            ItemResponse response = new ItemResponse();

            response.setStatus("AVAILABLE");
            assertEquals("AVAILABLE", response.getStatus());

            response.setStatus("IN_AUCTION");
            assertEquals("IN_AUCTION", response.getStatus());

            response.setStatus("SOLD");
            assertEquals("SOLD", response.getStatus());
        }

        @Test
        @DisplayName("EP-Invalid-NullOptionalFields")
        void testEP_NullOptionalFields() {
            ItemResponse response = new ItemResponse();

            response.setImagePath(null);
            response.setCurrentAuctionPrice(null);
            response.setActiveAuctionId(null);

            assertNull(response.getImagePath());
            assertNull(response.getCurrentAuctionPrice());
            assertNull(response.getActiveAuctionId());
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-StartingPrice-ZeroValue")
        void testBVA_ZeroStartingPrice() {
            ItemResponse response = new ItemResponse();
            response.setStartingPrice(0.0);
            assertEquals(0.0, response.getStartingPrice());
        }

        @Test
        @DisplayName("BVA-ItemId-MinimumValue")
        void testBVA_MinItemId() {
            ItemResponse response = new ItemResponse();
            response.setId(Integer.MIN_VALUE);
            assertEquals(Integer.MIN_VALUE, response.getId());
        }

        @Test
        @DisplayName("BVA-ItemId-MaximumValue")
        void testBVA_MaxItemId() {
            ItemResponse response = new ItemResponse();
            response.setId(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, response.getId());
        }
    }
}