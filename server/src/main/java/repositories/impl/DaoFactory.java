package repositories.impl;

import repositories.*;

public class DaoFactory {
    public static UsersDAO createUsersDAO(){
        return new UsersDAOImpl();
    }

    public static ItemsDAO createItemDAO() {
        return new ItemsDAOImpl();
    }

    public static AuctionsDAO createAuctionsDAO() {
        return new AuctionsDAOImpl();
    }

    public static BidsDAO createBidsDAO() {
        return new BidsDAOImpl();
    }

    public static TransactionsDAO createTransactionDAO() {
        return new TransactionsDAOImpl();
    }

    public static AutoBidDAO createAutoBidDAO() {
        return new AutoBidDAOImpl();
    }
}
