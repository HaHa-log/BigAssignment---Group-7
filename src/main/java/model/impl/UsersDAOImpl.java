package model.impl;

import Branch.User;
import DB.DB;
import DB.DbException;
import model.UsersDAO;

import java.sql.*;
import java.util.List;

public class UsersDAOImpl implements UsersDAO {
    private Connection conn;

    public UsersDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(User user) throws SQLException {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("INSERT INTO users " +
                            "(email, phoneNumber, firstName, lastName, password, isAdmin, isBlocked, balance) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, user.getEmail());
            st.setString(2, user.getPhoneNumber());
            st.setString(3, user.getFirstName());
            st.setString(4, user.getLastName());
            st.setString(5, user.getPassword());
            st.setBoolean(6, user.isAdmin());
            st.setBoolean(7, user.isBlocked());
            st.setDouble(8, user.getBalance());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    user.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Unexpected Error ! No rows affected !");
            }

            System.out.println("[System]: User " + user.getFirstName() + " saved to database.");
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
        finally{
            DB.closeStatement(st);
        }
    }

    @Override
    public void delete(User user) throws SQLException {

    }

    @Override
    public User getById(int id) throws SQLException {
        return null;
    }

    @Override
    public User getByEmail(int email) throws SQLException {
        return null;
    }


    @Override
    public int getId(User user) throws SQLException {
        return 0;
    }

    @Override
    public List<User> getAll() throws SQLException {
        return List.of();
    }
}
