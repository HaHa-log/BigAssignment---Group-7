package models;

import models.Exceptions.AuctionClosedException;
import models.Exceptions.InvalidBidException;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Concurrency Safety Test Suite")
public class AuctionConcurrencyTest {

    private static final int THREAD_COUNT = 10;
    private static final int TIMEOUT_SECONDS = 15;

    private User owner;
    private Item item;
    private Auction auction;

    @BeforeEach
    void setUp() {
        owner = mock(User.class);
        when(owner.getId()).thenReturn(1);
        when(owner.getFullName()).thenReturn("mili Ampee");

        item = mock(Item.class);
        when(item.getId()).thenReturn(0);
        when(item.getStartingPrice()).thenReturn(100.0);

        auction = new Auction(
                owner,
                item,
                Auction.AuctionStatus.RUNNING,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(2)
        );
        auction.setAuctionId(0);
    }

    private User createMockBidder(int id, double balance) {
        User bidder = mock(User.class);
        when(bidder.getId()).thenReturn(id);
        when(bidder.getFullName()).thenReturn("Bidder#" + id);
        when(bidder.getBalance()).thenReturn(balance);
        when(bidder.isEqual(owner)).thenReturn(false);
        when(bidder.freezeMoney(anyDouble())).thenReturn(true);
        when(bidder.unfreezeMoney(anyDouble())).thenReturn(true);
        when(bidder.getHighestBid(any(Auction.class))).thenReturn(0.0);
        return bidder;
    }

    private void shutdownPool(ExecutorService pool) throws InterruptedException {
        pool.shutdown();
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
    }

    private double getHighestBidAmount(Auction a) {
        return a.getBids().stream()
                .mapToDouble(bid -> bid.getBidPrice().getPrice())
                .max()
                .orElse(100.0);
    }

    @Nested
    @DisplayName("Race Condition - Multiple Different Bidders")
    class RaceConditionDifferentBidders {

        @RepeatedTest(20)
        @DisplayName("CONC-01: Only one winner emerges; current price remains consistent")
        void testCONC01_OnlyOneWinnerAndPriceIsConsistent() throws InterruptedException {
            int n = THREAD_COUNT;
            ExecutorService pool = Executors.newFixedThreadPool(n);
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(n);

            List<User> bidders = new ArrayList<>();
            List<Double> attemptedAmounts = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                bidders.add(createMockBidder(100 + i, 99999.0));
                attemptedAmounts.add(100.0 + (i + 1) * 10.0);
            }

            List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());
            AtomicInteger successCount = new AtomicInteger(0);

            for (int i = 0; i < n; i++) {
                final User bidder = bidders.get(i);
                final double amount = attemptedAmounts.get(i);

                pool.submit(() -> {
                    try {
                        startGate.await();
                        auction.placeBid(bidder, amount);
                        successCount.incrementAndGet();
                    } catch (InvalidBidException | AuctionClosedException e) {
                        exceptions.add(e);
                    } catch (Exception e) {
                        exceptions.add(e);
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startGate.countDown();
            boolean finished = doneLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            shutdownPool(pool);

            assertTrue(finished);

            long unexpectedErrors = exceptions.stream()
                    .filter(e -> !(e instanceof InvalidBidException) &&
                            !(e instanceof AuctionClosedException))
                    .count();
            assertEquals(0, unexpectedErrors);

            if (successCount.get() > 0) {
                assertNotNull(auction.getWinner());
                assertTrue(auction.getCurrentPrice() > 100.0);

                double highestBid = getHighestBidAmount(auction);
                assertEquals(highestBid, auction.getCurrentPrice());
            }
        }

        @RepeatedTest(20)
        @DisplayName("CONC-02: Identical bid amounts — exactly one accepted")
        void testCONC02_IdenticalBidAmountsOnlyOneAccepted() throws InterruptedException {
            int n = THREAD_COUNT;
            ExecutorService pool = Executors.newFixedThreadPool(n);
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(n);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger invalidBidCount = new AtomicInteger(0);
            List<Exception> unexpectedExceptions =
                    Collections.synchronizedList(new ArrayList<>());

            final double SAME_BID_AMOUNT = 150.0;

            for (int i = 0; i < n; i++) {
                final User bidder = createMockBidder(200 + i, 99999.0);

                pool.submit(() -> {
                    try {
                        startGate.await();
                        auction.placeBid(bidder, SAME_BID_AMOUNT);
                        successCount.incrementAndGet();
                    } catch (InvalidBidException e) {
                        invalidBidCount.incrementAndGet();
                    } catch (Exception e) {
                        unexpectedExceptions.add(e);
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startGate.countDown();
            boolean finished = doneLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            shutdownPool(pool);

            assertTrue(finished);
            assertEquals(0, unexpectedExceptions.size());
            assertEquals(1, successCount.get());
            assertEquals(n - 1, invalidBidCount.get());
            assertEquals(SAME_BID_AMOUNT, auction.getCurrentPrice());
            assertNotNull(auction.getWinner());
        }

        @RepeatedTest(20)
        @DisplayName("CONC-03: Bid list size matches successful bid count")
        void testCONC03_BidsListIsConsistentAfterRace() throws InterruptedException {
            int n = THREAD_COUNT;
            ExecutorService pool = Executors.newFixedThreadPool(n);
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(n);

            AtomicInteger successCount = new AtomicInteger(0);

            for (int i = 0; i < n; i++) {
                final User bidder = createMockBidder(300 + i, 99999.0);
                final double amount = 110.0 + i * 5.0;

                pool.submit(() -> {
                    try {
                        startGate.await();
                        auction.placeBid(bidder, amount);
                        successCount.incrementAndGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (InvalidBidException | AuctionClosedException ignored) {
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startGate.countDown();
            assertTrue(doneLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
            shutdownPool(pool);

            assertEquals(successCount.get(), auction.getBids().size());

            if (!auction.getBids().isEmpty()) {
                double highestBid = getHighestBidAmount(auction);
                assertEquals(highestBid, auction.getCurrentPrice());
            }
        }
    }

    @Nested
    @DisplayName("Race Condition - Same Bidder Spam")
    class RaceConditionSameBidder {

        @RepeatedTest(20)
        @DisplayName("CONC-04: Same bidder spamming — state remains consistent")
        void testCONC04_SameBidderSpam() throws InterruptedException {
            int n = THREAD_COUNT;
            ExecutorService pool = Executors.newFixedThreadPool(n);
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(n);

            User spammer = createMockBidder(999, 99999.0);

            AtomicInteger successCount = new AtomicInteger(0);
            List<Exception> unexpectedExceptions =
                    Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < n; i++) {
                final double amount = 110.0 + i * 10.0;

                pool.submit(() -> {
                    try {
                        startGate.await();
                        auction.placeBid(spammer, amount);
                        successCount.incrementAndGet();
                    } catch (InvalidBidException | AuctionClosedException ignored) {
                    } catch (Exception e) {
                        unexpectedExceptions.add(e);
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startGate.countDown();
            assertTrue(doneLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
            shutdownPool(pool);

            assertEquals(0, unexpectedExceptions.size());

            if (successCount.get() > 0) {
                assertNotNull(auction.getWinner());
                assertEquals(spammer.getId(), auction.getWinner().getId());
            }

            assertTrue(auction.getCurrentPrice() >= 100.0);
        }
    }

    @Nested
    @DisplayName("Race Condition - Bid At Closing Boundary")
    class RaceConditionAtClosingBoundary {

        @RepeatedTest(20)
        @DisplayName("CONC-05: Bids racing against auction closing")
        void testCONC05_BidsRacingAuctionClose() throws InterruptedException {
            Auction shortAuction = new Auction(
                    owner,
                    item,
                    Auction.AuctionStatus.RUNNING,
                    LocalDateTime.now().minusHours(1),
                    LocalDateTime.now().plusNanos(500_000_000)            );
            shortAuction.setAuctionId(0);

            double startingPrice = 100.0;
            int n = THREAD_COUNT;

            ExecutorService pool = Executors.newFixedThreadPool(n);
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneLatch = new CountDownLatch(n);

            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger closedCount = new AtomicInteger(0);
            List<Exception> unexpectedExceptions =
                    Collections.synchronizedList(new ArrayList<>());

            for (int i = 0; i < n; i++) {
                final User bidder = createMockBidder(400 + i, 99999.0);
                final double amount = startingPrice + (i + 1) * 10.0;
                final int delayMs = i * 50;

                pool.submit(() -> {
                    try {
                        startGate.await();
                        Thread.sleep(delayMs);
                        shortAuction.placeBid(bidder, amount);
                        successCount.incrementAndGet();
                    } catch (AuctionClosedException e) {
                        closedCount.incrementAndGet();
                    } catch (InvalidBidException ignored) {
                    } catch (Exception e) {
                        unexpectedExceptions.add(e);
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startGate.countDown();
            assertTrue(doneLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
            shutdownPool(pool);

            assertEquals(0, unexpectedExceptions.size());
            assertTrue(shortAuction.getCurrentPrice() >= startingPrice);
        }
    }
}