package model.impl;

import Branch.Admin;
import Branch.Member;
import Branch.User;
import DB.DB;
import DB.DbException;
import model.UsersDAO;

import java.sql.*;
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
            PreparedStatement st = conn.prepareStatement(sql + Statement.RETURN_GENERATED_KEYS)) {

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
    }

    @Override
    public void delete(User user) {
        String sql = "DELETE FROM users WHERE user_id = ?";
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

            System.out.println("[System]: Successful update.");
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT * From users WHERE users_id = ?";
        ResultSet rs = null;

        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, id);
            rs = st.executeQuery();
            if (rs.next()){
                Member member = instantiateMember(rs);
                return member;
            }
            return null;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally{
            DB.closeResultSet(rs);
        }
    }

    @Override
    public User getByEmail(String email) {
        String sql = "SELECT * From users WHERE email = ?";
        ResultSet rs = null;

        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, email);
            rs = st.executeQuery();
            if (rs.next()){
                Member member = instantiateMember(rs);
                return member;
            }
            return null;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        } finally{
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM users";
        ResultSet rs = null;

        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            rs = st.executeQuery();
            List<User> list = new ArrayList<>();

            while (rs.next()) {
                if (rs.getBoolean("isAdmin") == false) {
                    list.add(instantiateMember(rs));
                } else {
                    list.add(instantiateAdmin(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
        }
    }

    @Override
    public List<Member> getAllMember() {
        String sql = "SELECT * FROM users";
        ResultSet rs = null;

        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            rs = st.executeQuery();
            List<Member> list = new ArrayList<>();

            while (rs.next()) {
                if (rs.getBoolean("isAdmin") == false) {
                    list.add(instantiateMember(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
        }
    }

    public List<Admin> getAllAdmin() {
        String sql = "SELECT * FROM users";
        ResultSet rs = null;

        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            rs = st.executeQuery();
            List<Admin> list = new ArrayList<>();

            while (rs.next()) {
                if (rs.getBoolean("isAdmin") == true) {
                    list.add(instantiateAdmin(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeResultSet(rs);
        }
    }


    private Member instantiateMember(ResultSet rs) throws SQLException {
        Member obj = new Member(
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("email"),
                rs.getString("phoneNumber"),
                rs.getString("password"),
                rs.getDouble("balance")
        );
        return obj;
    }

    private Admin instantiateAdmin(ResultSet rs) throws SQLException {
        Admin obj = new Admin(
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("email"),
                rs.getString("phoneNumber"),
                rs.getString("password")
        );
        return obj;
    }
}
