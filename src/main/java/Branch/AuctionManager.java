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

        System.out.println("New auction session for " + item.getName());
        session.start();
    }

    public void closeAuction(Auction session) {
        session.transitionTo(Auction.AuctionStatus.FINISHED);
        activeSessions.remove(session);
        completedSessions.add(session);

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
        for (Auction auction: activeSessions) {
            if (auction.getAuctionId() == auctionId) {
                auction.transitionTo(Auction.AuctionStatus.CANCELED);
                activeSessions.remove(auction);
                completedSessions.add(auction);
                System.out.println("Auction canceled!");
                return true;
            }
        }
        System.out.println("Unable to find auction id " + auctionId);
        return false;
    }

    public List<Auction> getActiveSessions () {
        return this.activeSessions;
    }
}
