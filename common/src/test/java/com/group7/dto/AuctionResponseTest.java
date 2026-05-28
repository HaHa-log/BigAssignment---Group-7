package com.group7.dto;

import com.group7.dto.auction.AuctionResponse;
import com.group7.dto.bid.BidResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuctionResponse DTO Test Suite")
public class AuctionResponseTest {

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-ParameterizedConstructor")
        void testEP_ParameterizedConstructor() {
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = startTime.plusDays(1);
            List<BidResponse> bids = new ArrayList<>();

            AuctionResponse response = new AuctionResponse(
                    1,
                    10,
                    "Nguyen Van A",
                    100,
                    "Laptop",
                    "Gaming Laptop",
                    "laptop.png",
                    "ACTIVE",
                    1000.0,
                    1500.0,
                    startTime,
                    endTime,
                    20,
                    "Nguyen Thi B",
                    bids
            );

            assertEquals(1, response.getId());
            assertEquals(10, response.getOwnerId());
            assertEquals("Nguyen Van A", response.getOwnerName());
            assertEquals(100, response.getItemId());
            assertEquals("Laptop", response.getItemName());
            assertEquals("ACTIVE", response.getStatus());
            assertEquals(startTime, response.getStartingTime());
            assertEquals(endTime, response.getEndingTime());
            assertEquals(20, response.getWinnerId());
            assertEquals("Nguyen Thi B", response.getWinnerName());
            assertEquals(bids, response.getBids());
        }

        @Test
        @DisplayName("EP-Valid-BidsListModification")
        void testEP_BidsListModification() {
            AuctionResponse response = new AuctionResponse();
            List<BidResponse> bids = new ArrayList<>();
            bids.add(new BidResponse());

            response.setBids(bids);

            assertEquals(1, response.getBids().size());
        }

        @Test
        @DisplayName("EP-Invalid-NullWinnerAssignment")
        void testEP_NullWinnerAssignment() {
            AuctionResponse response = new AuctionResponse(
                    1, 1, "Owner", 2, "Item", "Description", "image.png",
                    "ENDED", 100, 200, LocalDateTime.now(),
                    LocalDateTime.now().plusHours(1), null, null, null
            );

            assertNull(response.getWinnerId());
            assertNull(response.getWinnerName());
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-StartingPrice-ZeroValue")
        void testBVA_ZeroStartingPrice() {
            AuctionResponse response = new AuctionResponse(
                    1, 1, "Owner", 1, "Item", "Description", "image.png",
                    "ACTIVE", 0.0, 0.0, LocalDateTime.now(),
                    LocalDateTime.now().plusDays(1), null, null, null
            );

            assertEquals(0.0, response.getStartingPrice());
        }

        @Test
        @DisplayName("BVA-AuctionId-MinimumValue")
        void testBVA_MinAuctionId() {
            AuctionResponse response = new AuctionResponse(
                    Integer.MIN_VALUE, 1, "Owner", 1, "Item", "Description", "image.png",
                    "ACTIVE", 100, 200, LocalDateTime.now(),
                    LocalDateTime.now().plusHours(1), null, null, null
            );

            assertEquals(Integer.MIN_VALUE, response.getId());
        }

        @Test
        @DisplayName("BVA-AuctionId-MaximumValue")
        void testBVA_MaxAuctionId() {
            AuctionResponse response = new AuctionResponse(
                    Integer.MAX_VALUE, 1, "Owner", 1, "Item", "Description", "image.png",
                    "ACTIVE", 100, 200, LocalDateTime.now(),
                    LocalDateTime.now().plusHours(1), null, null, null
            );

            assertEquals(Integer.MAX_VALUE, response.getId());
        }
    }
}