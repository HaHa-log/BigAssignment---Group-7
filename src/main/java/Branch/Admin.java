package Branch;

import Branch.User;

public class Admin extends User {
    public Admin(int id, String firstName, String lastName, String email, String phoneNumber, String password, double balance) {
        super(id, firstName, lastName, email, phoneNumber,password, balance);
    }

    public void blockUser(int userId) {}
    public void cancelAuction(int auctionId) {}
}