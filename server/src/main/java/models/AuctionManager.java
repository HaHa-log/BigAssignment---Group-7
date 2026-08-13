package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import repositories.AuctionsDAO;
import repositories.AutoBidDAO;
import repositories.ItemsDAO;
import repositories.TransactionsDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuctionManager {
    private static final Logger log = LoggerFactory.getLogger(AuctionManager.class);
    private static AuctionManager instance;

    private final AuctionsDAO auctionDb;
    private final TransactionsDAO transactionDb;
    private final AutoBidDAO autoBidDb;
    private final ItemsDAO itemsDb;
    private final UsersDAO userDb;
    private List<Auction> activeSessions;
    private List<Auction> completedSessions;

    private AuctionManager() {
        auctionDb = DaoFactory.createAuctionsDAO();
        transactionDb = DaoFactory.createTransactionDAO();
        autoBidDb = DaoFactory.createAutoBidDAO();
        itemsDb = DaoFactory.createItemDAO();
        userDb = DaoFactory.createUsersDAO();
        activeSessions = new ArrayList<>();
        completedSessions = new ArrayList<>();
    }

    public static synchronized AuctionManager getInstance() {
        if (instance == null) {
            instance = new AuctionManager();
        }
        return instance;
    }

    public AutoBid getAutoBidConfig(int auctionId, int userId) {
        return autoBidDb.getByAuctionAndUser(auctionId, userId);
    }

    public List<Auction> getActiveSessions() {
        activeSessions = new ArrayList<>(auctionDb.getActiveAuctions());
        return activeSessions;
    }

    public List<Auction> getAllSessions() {
        return auctionDb.getAll();
    }

    public void processAutoBids(Auction auction, AutoBid userConfig) {
        if (auction == null || userConfig == null) {
            return;
        }
        if (auction.getWinner() != null && auction.getWinner().isEqual(userConfig.getUser())) {
            return;
        }
        double nextPrice = auction.getCurrentPrice() + userConfig.getIncrement();
        if (nextPrice > userConfig.getMaxBid()) {
            log.info("Automatic bidding stopped due to maximum bid limit reached");
            return;
        }
        log.info("Auto-bidding for {}", userConfig.getUser().getFullName());
        userConfig.getUser().placeBid(auction, nextPrice);
    }

    public Auction createAuction(User owner, Item item, LocalDateTime startingTime, LocalDateTime endingTime) {
        item.setStatus(Item.Status.IN_AUCTION);
        Auction session = new Auction(owner, item, startingTime, endingTime);
        itemsDb.update(item);
        auctionDb.save(session);
        activeSessions.add(session);
        session.start();

        return session;
    }

    public void closeAuction(Auction session) {
        if (session == null
                || session.getStatus() == Auction.AuctionStatus.PAID
                || session.getStatus() == Auction.AuctionStatus.CANCELED) {
            return;
        }

        if (session.getStatus() != Auction.AuctionStatus.FINISHED) {
            if (!session.transitionTo(Auction.AuctionStatus.FINISHED)) {
                return;
            }
        }

        moveToCompleted(session);

        User winner = session.getWinner();
        if (winner == null) {
            Item item = session.getItem();
            item.setStatus(Item.Status.AVAILABLE);
            itemsDb.update(item);

            auctionDb.update(session);
            log.info("Auction #{} closed with no winner.", session.getAuctionId());
            return;
        }

        createPendingTransaction(session, winner);
        auctionDb.update(session);
        log.info("Auction #{} moved to pending invoice confirmation.", session.getAuctionId());
    }

    private void createPendingTransaction(Auction session, User winner) {
        Transaction existingTx = transactionDb.getPendingByAuctionAndBuyer(session.getId(), winner.getId());
        if (existingTx != null) {
            log.warn("Pending transaction already exists in DB for auction ID {}", session.getId());
            return;
        }

        session.getItem().setStatus(Item.Status.SOLD);
        double finalPrice = session.getCurrentPrice();
        Transaction transaction = new Transaction(session, winner, session.getOwner(), finalPrice);
        itemsDb.update(session.getItem());
        transactionDb.save(transaction);

        log.info("Pending invoice generated for Auction Winner: {}", winner.getFullName());
    }

    public boolean cancelAuction(int auctionId) {
        Auction canceledAuction = findActiveAuction(auctionId);
        if (canceledAuction == null) {
            log.warn("Unable to find auction id {}", auctionId);
            return false;
        }
        canceledAuction.getItem().setStatus(Item.Status.AVAILABLE);
        canceledAuction.transitionTo(Auction.AuctionStatus.CANCELED);
        moveToCompleted(canceledAuction);
        itemsDb.update(canceledAuction.getItem());
        auctionDb.update(canceledAuction);
        log.info("Auction canceled!");
        return true;
    }

    public boolean confirmReceipt(Auction auction, User buyer) {
        if (auction == null) {
            throw new IllegalArgumentException("[Error]: Auction is required.");
        }

        if (auction != null) {
            auction.getStatus();
        }
        auction.transitionTo(Auction.AuctionStatus.PAID);
        auction.getItem().setStatus(Item.Status.AVAILABLE);

        auction.getOwner().removeItem(auction.getItem());
        auction.getWinner().addItem(auction.getItem());

        auctionDb.update(auction);
        itemsDb.update(auction.getItem());
        userDb.update(buyer);
        userDb.update(auction.getOwner());

        return true;
    }

    public void checkAndCancelExpiredTransactions() {
        List<Transaction> pendingTransactions = transactionDb.getAll();
        UsersDAO usersDb = DaoFactory.createUsersDAO();


        if (pendingTransactions == null || pendingTransactions.isEmpty()) {
            return;
        } else {
            for (Transaction transaction : pendingTransactions) {
                if (pendingTransactions == null || pendingTransactions.isEmpty()) {
                    return;
                }

                if (!transaction.isExpired()) {
                    continue;
                }
                if (transaction.markExpiredRefund()) {
                    Auction auction = transaction.getAuction();
                    auction.transitionTo(Auction.AuctionStatus.CANCELED);
                    transactionDb.update(transaction);
                    usersDb.update(transaction.getBuyer());
                    auctionDb.update(auction);
                    System.out.println("[System]: Transaction " + transaction.getTransactionId()
                            + " has expired. Money refunded to buyer.");
                }
            }
        }
    }
    private Auction findActiveAuction ( int auctionId){
        return getActiveSessions().stream()
                .filter(auction -> auction.getId() == auctionId)
                .findFirst()
                .orElse(null);
    }

    private void moveToCompleted (Auction session){
        activeSessions.removeIf(auction -> auction.getId() == session.getId());
        if (completedSessions.stream().noneMatch(auction -> auction.getId() == session.getId())) {
            completedSessions.add(session);
        }
    }
}