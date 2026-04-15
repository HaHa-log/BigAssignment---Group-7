package Branch;

public class Member extends User implements Bidder, Seller {
    public Member(int id, String firstName, String lastName, String email, String phoneNumber, String password, double balance) {
        super(id, firstName, lastName, email, phoneNumber,password, balance);
    }

    @Override
    public void createAuction(Item item, double startingPrice) {
        System.out.println("Creating auction...");
    }
}
