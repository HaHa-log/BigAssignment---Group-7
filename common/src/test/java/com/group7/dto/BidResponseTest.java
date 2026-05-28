package com.group7.dto;

import com.group7.dto.bid.BidResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BidResponse DTO Test Suite")
public class BidResponseTest {

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-ParameterizedConstructor")
        void testEP_ParameterizedConstructor() {
            LocalDateTime bidTime = LocalDateTime.now();
            BidResponse response = new BidResponse(
                    1,
                    100,
                    5,
                    "Nguyen Van A",
                    2500.0,
                    bidTime
            );

            assertEquals(1, response.getId());
            assertEquals(100, response.getAuctionId());
            assertEquals(5, response.getBidderId());
            assertEquals("Nguyen Van A", response.getBidderName());
            assertEquals(2500.0, response.getBidPrice());
            assertEquals(bidTime, response.getBidTime());
        }

        @Test
        @DisplayName("EP-Valid-SettersAndGetters")
        void testEP_SettersAndGetters() {
            BidResponse response = new BidResponse();
            LocalDateTime bidTime = LocalDateTime.now();

            response.setId(2);
            response.setAuctionId(200);
            response.setBidderId(10);
            response.setBidderName("Nguyen Thi B");
            response.setBidPrice(3500.0);
            response.setBidTime(bidTime);

            assertEquals(2, response.getId());
            assertEquals(200, response.getAuctionId());
            assertEquals(10, response.getBidderId());
            assertEquals("Nguyen Thi B", response.getBidderName());
            assertEquals(3500.0, response.getBidPrice());
            assertEquals(bidTime, response.getBidTime());
        }

        @Test
        @DisplayName("EP-Invalid-NullBidderName")
        void testEP_NullBidderName() {
            BidResponse response = new BidResponse();
            response.setBidderName(null);
            assertNull(response.getBidderName());
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-BidPrice-ZeroValue")
        void testBVA_ZeroBidPrice() {
            BidResponse response = new BidResponse();
            response.setBidPrice(0.0);
            assertEquals(0.0, response.getBidPrice());
        }

        @Test
        @DisplayName("BVA-AuctionId-MinimumValue")
        void testBVA_MinAuctionId() {
            BidResponse response = new BidResponse();
            response.setAuctionId(Integer.MIN_VALUE);
            assertEquals(Integer.MIN_VALUE, response.getAuctionId());
        }

        @Test
        @DisplayName("BVA-AuctionId-MaximumValue")
        void testBVA_MaxAuctionId() {
            BidResponse response = new BidResponse();
            response.setAuctionId(Integer.MAX_VALUE);
            assertEquals(Integer.MAX_VALUE, response.getAuctionId());
        }
    }
}