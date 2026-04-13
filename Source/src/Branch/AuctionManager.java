package Branch;

import java.util.ArrayList;
import java.util.List;

public class AuctionManager {
    private static AuctionManager instance;
    private List<Auction> activeSessions;

    private AuctionManager() {
        activeSessions = new ArrayList<>();
    }

    public static synchronized AuctionManager getInstance() {
        if (instance == null) {
            instance = new AuctionManager();
        }
        return instance;
    }

    public void createAuction(Item item, double startingPrice) {
        Auction session = new Auction();
        session.setStartingPrice(startingPrice);
        activeSessions.add(session);

        System.out.println("New auction session for " + item.getName());
        session.start();
    }

}
