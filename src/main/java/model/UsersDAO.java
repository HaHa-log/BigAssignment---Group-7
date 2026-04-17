package model;

import Branch.User;

import java.sql.SQLException;

public interface UsersDAO extends DAO<User> {
    User getByEmail(int email) throws SQLException;
}
