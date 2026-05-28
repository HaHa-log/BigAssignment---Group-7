package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import repositories.AuctionsDAO;
import repositories.AutoBidDAO;
import repositories.ItemsDAO;
import repositories.TransactionDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

public class AuctionManager {
    private static AuctionManager instance;

    private final AuctionsDAO auctionDb;
    private final TransactionDAO transactionDb;
    private final AutoBidDAO autoBidDb;
    private final ItemsDAO itemsDb;
    private List<Auction> activeSessions;
    private List<Auction> completedSessions;

    private AuctionManager() {
        auctionDb = DaoFactory.createAuctionsDAO();
        transactionDb = DaoFactory.createTransactionDAO();
        autoBidDb = DaoFactory.createAutoBidDAO();
        this.itemsDb = DaoFactory.createItemDAO();
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

    public Auction createAuction(User owner, Item item, LocalDateTime startingTime, LocalDateTime endingTime) {
        item.setStatus(Item.Status.IN_AUCTION);
        Auction session = new Auction(owner, item, startingTime, endingTime);
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
            System.out.println("[System]: Auction #" + session.getAuctionId() + " closed with no winner.");
            return;
        }

        // Tạo bản ghi giao dịch chờ thanh toán, không xử lý dịch chuyển số dư tài khoản
        createPendingTransaction(session, winner);
        auctionDb.update(session);
        System.out.println("[System]: Auction #" + session.getAuctionId() + " moved to pending invoice confirmation.");
    }

    private void createPendingTransaction(Auction session, User winner) {
        // KIỂM TRA CHỐNG LỖI TẠO TRÙNG BẢN GHI (Duplicate Record):
        Transaction existingTx = transactionDb.getPendingByAuctionAndBuyer(session.getId(), winner.getId());
        if (existingTx != null) {
            System.out.println("[System Warning]: Pending transaction already exists in DB for auction ID " + session.getId());
            return;
        }

        double finalPrice = session.getCurrentPrice();
        Transaction transaction = new Transaction(session, winner, session.getOwner(), finalPrice);
        transactionDb.save(transaction);

        session.getItem().setStatus(Item.Status.SOLD);
        System.out.println("[System]: Pending invoice generated for Auction Winner: " + winner.getFullName());
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

    public Transaction confirmReceipt(Auction auction, User buyer) {
        if (auction == null) {
            throw new IllegalArgumentException("[Error]: Auction is required.");
        }
        return confirmReceipt(auction.getId(), buyer);
    }

    public Transaction confirmReceipt(int auctionId, User buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException("[Error]: Buyer is required.");
        }

        // Ép cập nhật trạng thái thời gian thực tế của DB Server trước khi tìm kiếm hóa đơn
        Auction auction = auctionDb.getById(auctionId);
        if (auction != null) {
            auction.getStatus();
        }

        Transaction transaction = transactionDb.getPendingByAuctionAndBuyer(auctionId, buyer.getId());
        if (transaction == null) {
            throw new IllegalArgumentException("[Error]: No pending transaction found for this auction.");
        }

        if (!buyer.isEqual(transaction.getBuyer())) {
            throw new IllegalArgumentException("[Error]: Only the buyer can confirm receipt.");
        }

        transaction.markCompleted();
        transaction.getAuction().transitionTo(Auction.AuctionStatus.PAID);

        // DB update
        transactionDb.update(transaction);
        auctionDb.update(transaction.getAuction());
        DaoFactory.createUsersDAO().update(buyer);
        DaoFactory.createUsersDAO().update(transaction.getSeller());

        return transaction;
    }

    @Scheduled(fixedDelay = 600_000)
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
            User buyer = transaction.getBuyer();
            buyer.unfreezeMoney(transaction.getFinalAmount());
            usersDb.update(buyer);
            System.out.println("[System]: Transaction " + transaction.getTransactionId()
                    + " has expired. Money refunded to buyer.");
        }
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
}
