package model.impl;

import DB.DB;
import model.UsersDAO;

public class DaoFactory {
    public static UsersDAO createUsersDAO(){
        return new UsersDAOImpl(DB.getConnection());
    }
}
