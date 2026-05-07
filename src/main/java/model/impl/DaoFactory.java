package model.impl;

import model.*;

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

    public static BidsDAO createBidDAO() {
        return new BidsDAOImpl();
    }

    public static TransactionDAO createTransactionDAO() {
        return new TransactionDAOImpl();
    }
}
