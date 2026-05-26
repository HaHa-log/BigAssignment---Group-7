package models;

public class Admin extends User {

    public Admin(String firstName, String lastName, String email, String phoneNumber, String password, double balance, String avatarPath) {
        super(firstName, lastName, email, phoneNumber, password, balance, avatarPath);
        setAdmin(true);
    }

    public boolean isAdmin() {
        return true;
    }

    public boolean cancelAuction(int auctionId) {
        AuctionManager manager = AuctionManager.getInstance();
        boolean success = manager.cancelAuction(auctionId);
        if (success) {
            System.out.println("[Admin]: Auction " + auctionId + " has been cancelled.");
            return true;
        } else {
            System.out.println("[Admin]: Could not find auction with ID: " + auctionId);
            return false;
        }
    }
}
