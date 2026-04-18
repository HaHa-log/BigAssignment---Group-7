package Branch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionManager {
    private static AuctionManager instance;
    private List<Auction> activeSessions;
    private List<Auction> completedSessions;

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

    public void createAuction(Member owner, Item item, int ownerId, double startingPrice, LocalDateTime createdAt, LocalDateTime terminatedAt) {
        Auction session = new Auction(owner, item);
        session.setStartingPrice(startingPrice);
        activeSessions.add(session);

        TempDatabase.saveAuction(session);

        System.out.println("New auction session for " + item.getName());
        session.start();
    }

    public void closeAuction(Auction session) {
        session.transitionTo(Auction.AuctionStatus.FINISHED);
        activeSessions.remove(session);
        completedSessions.add(session);

        TempDatabase.updateAuction(session);

        if (session.getWinner() != null) {
            Transaction transaction = new Transaction(
                    session,
                    (Member) session.getWinner(),
                    session.getOwner(),
                    session.getCurrentPrice()
            );
            TempDatabase.saveTransaction(transaction);
        }

        System.out.println("Auction closed!");
    }

    public boolean cancelAuction(int auctionId) {
        Auction canceledAuction = null;
        for (Auction auction : activeSessions) {
            if (auction.getAuctionId() == auctionId) {
                canceledAuction = auction;
                break;
            }
        }

        if (canceledAuction != null) {
            canceledAuction.transitionTo(Auction.AuctionStatus.CANCELED);
            activeSessions.remove(canceledAuction);
            completedSessions.add(canceledAuction);

            TempDatabase.updateAuction(canceledAuction);

            System.out.println("Auction canceled!");
            return true;
        }
        System.out.println("Unable to find auction id " + auctionId);
        return false;
    }

    public List<Auction> getActiveSessions () {
        return this.activeSessions;
    }
}
