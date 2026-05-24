package models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BidStepConfiguration Test Suite")
public class BidStepConfigurationTest {

    @Nested
    @DisplayName("Equivalence Partitioning Tests")
    class EquivalencePartitioningTests {

        @Test
        @DisplayName("EP-Valid-Tier1: Price inside interval [0; 99.99]")
        void testEP_ValidTier1() {
            double currentPrice = 50.0;
            List<Double> allowedSteps = BidStepConfiguration.getAllowedSteps(currentPrice);

            assertNotNull(allowedSteps);
            assertTrue(allowedSteps.contains(1.0));
            assertTrue(allowedSteps.contains(2.0));
            assertTrue(allowedSteps.contains(5.0));
            assertFalse(allowedSteps.contains(10.0));
        }

        @Test
        @DisplayName("EP-Valid-Tier2: Price inside interval [100; 999.99]")
        void testEP_ValidTier2() {
            double currentPrice = 500.0;
            List<Double> allowedSteps = BidStepConfiguration.getAllowedSteps(currentPrice);

            assertNotNull(allowedSteps);
            assertTrue(allowedSteps.contains(10.0));
            assertTrue(allowedSteps.contains(20.0));
            assertTrue(allowedSteps.contains(50.0));
            assertFalse(allowedSteps.contains(5.0));
        }

        @Test
        @DisplayName("EP-Valid-Tier3: Price inside interval [1000; 999999999]")
        void testEP_ValidTier3() {
            double currentPrice = 5000.0;
            List<Double> allowedSteps = BidStepConfiguration.getAllowedSteps(currentPrice);

            assertNotNull(allowedSteps);
            assertTrue(allowedSteps.contains(100.0));
            assertTrue(allowedSteps.contains(500.0));
            assertTrue(allowedSteps.contains(1000.0));
            assertFalse(allowedSteps.contains(50.0));
        }

        @Test
        @DisplayName("EP-Invalid: Negative price returns empty list")
        void testEP_InvalidPrice() {
            double currentPrice = -50.0;

            List<Double> allowedSteps =
                    BidStepConfiguration.getAllowedSteps(currentPrice);

            assertNotNull(allowedSteps);
            assertTrue(allowedSteps.isEmpty());
        }
    }

    @Nested
    @DisplayName("Boundary Value Analysis Tests")
    class BoundaryValueAnalysisTests {

        @Test
        @DisplayName("BVA-Tier1-Min: Boundary value at 0.0")
        void testBVA_Tier1Min() {
            double currentPrice = 0.0;
            List<Double> allowedSteps = BidStepConfiguration.getAllowedSteps(currentPrice);
            assertTrue(allowedSteps.contains(1.0));
        }

        @Test
        @DisplayName("BVA-Tier1-Max: Boundary value at 99.99")
        void testBVA_Tier1Max() {
            double currentPrice = 99.99;
            List<Double> allowedSteps = BidStepConfiguration.getAllowedSteps(currentPrice);
            assertTrue(allowedSteps.contains(5.0));
            assertFalse(allowedSteps.contains(10.0));
        }

        @Test
        @DisplayName("BVA-Tier2-Min: Boundary value at 100.0")
        void testBVA_Tier2Min() {
            double currentPrice = 100.0;
            List<Double> allowedSteps = BidStepConfiguration.getAllowedSteps(currentPrice);
            assertTrue(allowedSteps.contains(10.0));
            assertFalse(allowedSteps.contains(5.0));
        }

        @Test
        @DisplayName("BVA-Tier2-Max: Boundary value at 999.99")
        void testBVA_Tier2Max() {
            double currentPrice = 999.99;
            List<Double> allowedSteps = BidStepConfiguration.getAllowedSteps(currentPrice);
            assertTrue(allowedSteps.contains(50.0));
            assertFalse(allowedSteps.contains(100.0));
        }

        @Test
        @DisplayName("BVA-Tier3-Min: Boundary value at 1000.0")
        void testBVA_Tier3Min() {
            double currentPrice = 1000.0;
            List<Double> allowedSteps = BidStepConfiguration.getAllowedSteps(currentPrice);
            assertTrue(allowedSteps.contains(100.0));
            assertFalse(allowedSteps.contains(50.0));
        }

        @Test
        @DisplayName("BVA-Tier3-Max: Boundary value at 999999999.0")
        void testBVA_Tier3Max() {
            double currentPrice = 999999999.0;
            List<Double> allowedSteps = BidStepConfiguration.getAllowedSteps(currentPrice);
            assertTrue(allowedSteps.contains(1000.0));
        }
    }

    @Nested
    @DisplayName("Validation Logic Tests")
    class ValidationTests {

        @Test
        @DisplayName("isValidStep: Expected true and false outcomes across tiers")
        void testIsValidStep() {
            assertTrue(BidStepConfiguration.isValidStep(50.0, 2.0));
            assertFalse(BidStepConfiguration.isValidStep(50.0, 10.0));

            assertTrue(BidStepConfiguration.isValidStep(500.0, 20.0));
            assertFalse(BidStepConfiguration.isValidStep(500.0, 5.0));

            assertTrue(BidStepConfiguration.isValidStep(5000.0, 500.0));
            assertFalse(BidStepConfiguration.isValidStep(5000.0, 20.0));
        }
    }
}