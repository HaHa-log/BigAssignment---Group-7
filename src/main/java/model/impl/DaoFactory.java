package model.impl;

import model.AuctionsDAO;
import model.ItemsDAO;
import model.UsersDAO;

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
}
