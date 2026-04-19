package model.impl;

import model.UsersDAO;

public class DaoFactory {
    public static UsersDAOImpl createUsersDAO(){
        return new UsersDAOImpl();
    }
}
