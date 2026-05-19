package Branch;

import Branch.Exceptions.CustomisedException;
import model.AuctionsDAO;
import model.AutoBidDAO;
import model.TransactionDAO;
import model.UsersDAO;
import model.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionManager {
    private static AuctionManager instance;
    private List<Auction> activeSessions;
    private List<Auction> completedSessions;

    private  AuctionsDAO auctionDb = DaoFactory.createAuctionsDAO();
    private TransactionDAO transactionDb = DaoFactory.createTransactionDAO();
    private AutoBidDAO autoBidDb = DaoFactory.createAutoBidDAO();

    private AuctionManager() {
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

        if (sample != null) {
            AutoBid newConfig = sample.clone();

            if (newConfig != null) {
                Auction newAuction = auctionDb.getById(toAuctionId);
                newConfig.setAuction(newAuction);
                autoBidDb.save(newConfig);
                System.out.println("[System]: AutoBid config duplicated successfully!");
            }

        } else {
            System.out.println("[System]: No prototype configuration found in auction of ID " + fromAuctionId);
        }
    }

    public void processAutoBids(Auction auction, AutoBid userConfig) {
        if (auction.getWinner() != null && auction.getWinner().isEqual(userConfig.getUser())) {
            return;
        }

        double nextPrice = auction.getCurrentPrice() + userConfig.getIncrement();

        if (nextPrice <= userConfig.getMaxBid()) {
            System.out.println("[System]: Auto-bidding for " + userConfig.getUser().getFullName());

            userConfig.getUser().placeBid(auction, nextPrice);

        } else {
            System.out.println("[System]: Automatic bidding stopped due to maximum bid limit reached");
        }
    }

    public void createAuction(Member owner, Item item, LocalDateTime startingTime, LocalDateTime endingTime) {
        /*
        if (!item.isAvailable()) {
            System.out.println("[Error]: Item '" + item.getName() + "' is not available!");
            return false;
        }
         */

        item.setStatus(Item.Status.IN_AUCTION);

        Auction session = new Auction(owner, item, startingTime, endingTime);
        activeSessions.add(session);

        auctionDb.save(session);

        session.start();
    }

    public void closeAuction(Auction session) {
        session.transitionTo(Auction.AuctionStatus.FINISHED);
        activeSessions.remove(session);
        completedSessions.add(session);

        if (session.getWinner() != null) {
//            Item soldItem = session.getItem();
//            soldItem.setStatus(Item.Status.SOLD);
//
//            session.getOwner().getInventory().remove(soldItem);
            //-> Nên chỉ remove item và để thành sold khi đã markcompleted

            User winner = (User) session.getWinner();
            double finalPrice = session.getCurrentPrice();

            if (winner.getBalance() >= finalPrice) {
                winner.freezeMoney(finalPrice);
                System.out.println("[System]: Money frozen, moving onto transaction page...");
            } else {
                System.out.println("[Warning]: Winner no longer has enough balance to freeze!");
            }

            Transaction transaction = new Transaction(
                    session,
                    (Member) session.getWinner(),
                    session.getOwner(),
                    session.getCurrentPrice()
            );
            transactionDb.save(transaction);
            session.getItem().setStatus(Item.Status.SOLD); //only be removed if sold successfully
            System.out.println("[System]: Transaction created for winner: " + session.getWinner().getFullName());

        } else {
            session.getItem().setStatus(Item.Status.AVAILABLE);
        }

        System.out.println("Auction closed!");
        auctionDb.update(session);
    }

    public boolean cancelAuction(int auctionId) {
        Auction canceledAuction = null;
        for (Auction auction : activeSessions) {
            if (auction.getId() == auctionId) {
                canceledAuction = auction;
                break;
            }
        }

        if (canceledAuction != null) {
            canceledAuction.getItem().setStatus(Item.Status.AVAILABLE);

            canceledAuction.transitionTo(Auction.AuctionStatus.CANCELED);
            activeSessions.remove(canceledAuction);
            completedSessions.add(canceledAuction);

            auctionDb.update(canceledAuction);

            System.out.println("Auction canceled!");
            return true;
        }
        System.out.println("Unable to find auction id " + auctionId);
        return false;
    }

    public void checkAndCancelExpiredTransactions() {
        List<Transaction> pendingTransactions = transactionDb.getAll().stream()
                .filter(t -> t.getStatus() == Transaction.TransactionStatus.PENDING)
                .toList();
        UsersDAO usersDb = DaoFactory.createUsersDAO();

        for (Transaction t : pendingTransactions) {
            if (t.isExpired()) {
                t.getAuction().transitionTo(Auction.AuctionStatus.CANCELED);

                Member buyer = t.getBuyer();
                buyer.unfreezeMoney(t.getFinalAmount());
                usersDb.update(buyer);

                System.out.println("[System]: Transaction " + t.getTransactionId() + " has expired. Money refunded to buyer.");
            }
        }
    }

    public List<Auction> getActiveSessions () {
        if (activeSessions == null || activeSessions.isEmpty()) {
            activeSessions = auctionDb.getAll();
        }

        return activeSessions;
    }

    public List<Auction> getAllSessions () {

        if (activeSessions == null || activeSessions.isEmpty()) {
            activeSessions = auctionDb.getAll();
        }

        return activeSessions;
    }
}
