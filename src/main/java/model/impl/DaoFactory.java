package model.impl;

import model.UsersDAO;

public class DaoFactory {
    public static UsersDAO createUsersDAO(){
        return new UsersDAOImpl();
    }
}
