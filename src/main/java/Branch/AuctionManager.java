package Branch;

import model.AuctionsDAO;
import model.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionManager {
    private static AuctionManager instance;
    private List<Auction> activeSessions;
    private List<Auction> completedSessions;

    private  AuctionsDAO auctionDb = DaoFactory.createAuctionsDAO();

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

    public void createAuction(Member owner, Item item, LocalDateTime createdAt, LocalDateTime terminatedAt) {
        /*
        if (!item.isAvailable()) {
            System.out.println("[Error]: Item '" + item.getName() + "' is not available!");
            return false;
        }
         */

        item.setStatus(Item.Status.IN_AUCTION);

        Auction session = new Auction(owner, item);
        activeSessions.add(session);

        auctionDb.save(session);

        System.out.println("New auction session for " + item.getName());
        session.start();
    }

    public void closeAuction(Auction session) {
        session.transitionTo(Auction.AuctionStatus.FINISHED);
        activeSessions.remove(session);
        completedSessions.add(session);

        auctionDb.update(session);

        if (session.getWinner() != null) {
            Item soldItem = session.getItem();
            soldItem.setStatus(Item.Status.SOLD);

            session.getSeller().getInventory().remove(soldItem);

            Transaction transaction = new Transaction(
                    session,
                    (Member) session.getWinner(),
                    session.getSeller(),
                    session.getCurrentPrice()
            );
            TempDatabase.saveTransaction(transaction);

        } else {
            session.getItem().setStatus(Item.Status.AVAILABLE);
        }

        System.out.println("Auction closed!");
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

    public List<Auction> getActiveSessions () {
        return auctionDb.getActiveAuctions();
    }
}
