package models;
/*
import model.AuctionsDAO;
import model.AutoBidDAO;
import model.TransactionDAO;
import model.UsersDAO;
import model.impl.DaoFactory;
*/

import java.util.List;

public class AuctionManager {
    private static AuctionManager instance;

    /*private final AuctionsDAO auctionDb;
    private final TransactionDAO transactionDb;
    private final AutoBidDAO autoBidDb;*/
    private List<Auction> activeSessions;
    private List<Auction> completedSessions;

/*
    private AuctionManager() {
        /*auctionDb = DaoFactory.createAuctionsDAO();
        transactionDb = DaoFactory.createTransactionDAO();
        autoBidDb = DaoFactory.createAutoBidDAO();
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

    public void duplicateAutoBidConfig(int fromAuctionId, int toAuctionId, int userId) {
        AutoBid sample = autoBidDb.getByAuctionAndUser(fromAuctionId, userId);
        if (sample == null) {
            System.out.println("[System]: No prototype configuration found in auction of ID " + fromAuctionId);
            return;
        }

        AutoBid newConfig = sample.clone();
        if (newConfig == null) {
            return;
        }

        Auction newAuction = auctionDb.getById(toAuctionId);
        newConfig.setAuction(newAuction);
        autoBidDb.save(newConfig);
        System.out.println("[System]: AutoBid config duplicated successfully!");
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
            System.out.println("[System]: Automatic bidding stopped due to maximum bid limit reached");
            return;
        }

        System.out.println("[System]: Auto-bidding for " + userConfig.getUser().getFullName());
        userConfig.getUser().placeBid(auction, nextPrice);
    }

    public void createAuction(Member owner, Item item, LocalDateTime startingTime, LocalDateTime endingTime) {
        item.setStatus(Item.Status.IN_AUCTION);

        Auction session = new Auction(owner, item, startingTime, endingTime);
        auctionDb.save(session);
        activeSessions.add(session);
        session.start();
    }

    public void closeAuction(Auction session) {
        if (session == null || session.getRawStatus() == Auction.AuctionStatus.FINISHED
                || session.getRawStatus() == Auction.AuctionStatus.PAID
                || session.getRawStatus() == Auction.AuctionStatus.CANCELED) {
            return;
        }

        if (!session.transitionTo(Auction.AuctionStatus.FINISHED)) {
            return;
        }

        moveToCompleted(session);

        User winner = session.getWinner();
        if (winner == null) {
            session.getItem().setStatus(Item.Status.AVAILABLE);
            auctionDb.update(session);
            System.out.println("Auction closed!");
            return;
        }

        createPendingTransaction(session, winner);
        auctionDb.update(session);
        System.out.println("Auction closed!");
    }

    public boolean cancelAuction(int auctionId) {
        Auction canceledAuction = findActiveAuction(auctionId);

        if (canceledAuction == null) {
            System.out.println("Unable to find auction id " + auctionId);
            return false;
        }

        canceledAuction.getItem().setStatus(Item.Status.AVAILABLE);
        canceledAuction.transitionTo(Auction.AuctionStatus.CANCELED);
        moveToCompleted(canceledAuction);
        auctionDb.update(canceledAuction);

        System.out.println("Auction canceled!");
        return true;
    }

    public Transaction confirmReceipt(Auction auction, Member buyer) {
        if (auction == null) {
            throw new IllegalArgumentException("[Error]: Auction is required.");
        }
        return confirmReceipt(auction.getId(), buyer);
    }

    public Transaction confirmReceipt(int auctionId, Member buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException("[Error]: Buyer is required.");
        }

        Transaction transaction = transactionDb.getPendingByAuctionAndBuyer(auctionId, buyer.getId());
        if (transaction == null) {
            throw new IllegalArgumentException("[Error]: No pending transaction found for this auction.");
        }

        if (!buyer.isEqual(transaction.getBuyer())) {
            throw new IllegalArgumentException("[Error]: Only the buyer can confirm receipt.");
        }

        double amount = transaction.getFinalAmount();

        boolean success = buyer.spendFrozenMoney(amount);

        if (!success) {throw new IllegalArgumentException("[Error]: Payment failed.");
        }
        buyer.addTransaction("🛒 PAYMENT | -" + amount + " | Item: " + transaction.getAuction().getItem().getName()
        );

        Member seller = transaction.getSeller();
        seller.depositMoney(amount);
        
        seller.addTransaction("💰 SALE | +" + amount + " | Item: " + transaction.getAuction().getItem().getName()
        );

        transaction.markCompleted();
        transaction.getAuction().transitionTo(Auction.AuctionStatus.PAID);

        transactionDb.update(transaction);
        auctionDb.update(transaction.getAuction());

        return transaction;
    }

    public void checkAndCancelExpiredTransactions() {
        List<Transaction> pendingTransactions = transactionDb.getAll().stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.PENDING)
                .toList();
        UsersDAO usersDb = DaoFactory.createUsersDAO();

        for (Transaction transaction : pendingTransactions) {
            if (!transaction.isExpired()) {
                continue;
            }

            transaction.getAuction().transitionTo(Auction.AuctionStatus.CANCELED);

            Member buyer = transaction.getBuyer();
            buyer.unfreezeMoney(transaction.getFinalAmount());
            usersDb.update(buyer);

            System.out.println("[System]: Transaction " + transaction.getTransactionId()
                    + " has expired. Money refunded to buyer.");
        }
    }

    public List<Auction> getActiveSessions() {
        activeSessions = new ArrayList<>(auctionDb.getActiveAuctions());
        return activeSessions;
    }

    public List<Auction> getAllSessions() {
        return auctionDb.getAll();
    }

    private Auction findActiveAuction(int auctionId) {
        return getActiveSessions().stream()
                .filter(auction -> auction.getId() == auctionId)
                .findFirst()
                .orElse(null);
    }

    private void moveToCompleted(Auction session) {
        activeSessions.removeIf(auction -> auction.getId() == session.getId());
        if (completedSessions.stream().noneMatch(auction -> auction.getId() == session.getId())) {
            completedSessions.add(session);
        }
    }

    private void createPendingTransaction(Auction session, User winner) {
        double finalPrice = session.getCurrentPrice();
        if (winner.getFrozenBalance() < finalPrice && winner.freezeMoney(finalPrice - winner.getFrozenBalance())) {
            System.out.println("[System]: Money frozen, moving onto transaction page...");
        } else if (winner.getFrozenBalance() < finalPrice) {
            System.out.println("[Warning]: Winner no longer has enough balance to freeze!");
        }

        Transaction transaction = new Transaction(
                session,
                (Member) winner,
                session.getOwner(),
                finalPrice
        );
        transactionDb.save(transaction);
        session.getItem().setStatus(Item.Status.SOLD);
        System.out.println("[System]: Transaction created for winner: " + winner.getFullName());
    }*/
}
