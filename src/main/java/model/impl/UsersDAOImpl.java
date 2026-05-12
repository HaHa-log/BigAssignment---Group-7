package model.impl;

import Branch.Admin;
import Branch.Member;
import Branch.User;
import DB.DB;
import DB.DbException;
import model.UsersDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsersDAOImpl implements UsersDAO {

    protected UsersDAOImpl() {};

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users "
                + "(email, phoneNumber, firstName, lastName, password, isAdmin, isBlocked, balance) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        user.setId(id);
                    }
                }
            } else {
                throw new DbException("Unexpected Error ! No rows affected !");
            }

            System.out.println("[System]: User " + user.getFirstName() + " saved to database.");
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void delete(User user) {
        String sql = "DELETE FROM users WHERE users_id = ?";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, user.getId());
            int rows = st.executeUpdate();

            if (rows == 0) {
                throw new DbException("User is invalid!");
            }
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users "
                +"SET email = ?, phoneNumber = ?, firstName = ?, lastName = ?, password = ?, isAdmin = ?, isBlocked = ?, balance = ? "
                + " WHERE users_id = ? ";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, user.getEmail());
            st.setString(2, user.getPhoneNumber());
            st.setString(3, user.getFirstName());
            st.setString(4, user.getLastName());
            st.setString(5, user.getPassword());
            st.setBoolean(6, user.isAdmin());
            st.setBoolean(7, user.isBlocked());
            st.setDouble(8, user.getBalance());
            st.setInt(9, user.getId());

            st.executeUpdate();

        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT * From users WHERE users_id = ?";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()){
                    Member member = instantiateMember(rs);
                    return member;
                }
            }
            return null;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public User getByEmail(String email) {
        String sql = "SELECT * From users WHERE email = ?";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    if (rs.getBoolean("isAdmin") == false) {
                        Member member = instantiateMember(rs);
                        return member;
                    } else if (rs.getBoolean("isAdmin") == true) {
                        Admin admin = instantiateAdmin(rs);
                        return admin;
                    }
                }
            }
            return null;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users";
        List<User> list = new ArrayList<>();
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                if (rs.getBoolean("isAdmin") == false) {
                    list.add(instantiateMember(rs));
                } else if (rs.getBoolean("isAdmin") == true) {
                    list.add(instantiateAdmin(rs));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Member> getAllMember() {
        String sql = "SELECT * FROM users WHERE isAdmin = ?";
        List<Member> list = new ArrayList<>();
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setBoolean(1, false);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(instantiateMember(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Admin> getAllAdmin() {
        String sql = "SELECT * FROM users WHERE isAdmin = ?";
        List<Admin> list = new ArrayList<>();
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setBoolean(1, true);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(instantiateAdmin(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }


    private Member instantiateMember(ResultSet rs) throws SQLException {
        Member obj = new Member(
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("email"),
                rs.getString("phoneNumber"),
                rs.getString("password"),
                rs.getDouble("balance"),
                rs.getBoolean("isAdmin"),
                rs.getBoolean("isBlocked"),
                rs.getObject("blockedUntil", LocalDateTime.class)
        );

        obj.setId(rs.getInt("users_id"));
        return obj;
    }

    private Admin instantiateAdmin(ResultSet rs) throws SQLException {
        Admin obj = new Admin(
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("email"),
                rs.getString("phoneNumber"),
                rs.getString("password"),
                rs.getDouble("balance"),
                rs.getBoolean("isAdmin"),
                rs.getBoolean("isBlocked"),
                rs.getObject("blockedUntil", LocalDateTime.class)
        );

        obj.setId(rs.getInt("users_id"));
        return obj;
    }
}
