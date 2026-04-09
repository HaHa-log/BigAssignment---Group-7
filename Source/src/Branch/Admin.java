public class Admin extends User {
    public Admin(int id, String firstName, String lastName, String email, String phoneNumber, String password) {
        super(id, firstName, lastName, email, phoneNumber,password);
    }

    public void blockUser(int userId) {}
    public void cancelAuction(int auctionId) {}
}