package Branch;

import Branch.Common.Balance;
import Branch.Common.Price;

public interface Bidder {

    default boolean placeBid(Auction auction, Price amount) {
        if (this instanceof Member member) {
           if (member.isBlocked()) {
               System.out.println("[Error]: Your account is blocked");
               return false;
           }

           double currentBalance = member.getBalance();

           if (amount.getPrice() > currentBalance) {
               System.out.println("[Error]: Bid cannot be greater than your balance");
               return false;
           }

           if (amount.getPrice() == 0) {
               System.out.println("[Error]: Bid must be greater than zero");
               return false;
           }

           return auction.placeBid(this, amount);
        } else {
            System.out.println("[Error]: Only members can place bids. Admins are restricted.");
            return false;
        }
    }
}