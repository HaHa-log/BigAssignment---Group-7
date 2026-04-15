package Branch;

public class Member extends User implements Bidder, Seller {
    public Member(int id, String firstName, String lastName, String email, String phoneNumber, String password) {
        super(id, firstName, lastName, email, phoneNumber,password);
    }

    @Override
    public void placeBid(int auctionId, double amount) {
        System.out.println("Placing bid...");
    }

    @Override
    public void createAuction(Item item, double startingPrice) {
        System.out.println("Creating auction...");
    }
}
