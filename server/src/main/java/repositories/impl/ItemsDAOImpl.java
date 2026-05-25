package repositories.impl;

import models.Item;
import config.DB;
import config.DbException;
import models.User;
import repositories.ItemsDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemsDAOImpl implements ItemsDAO {

    protected ItemsDAOImpl() {}

    @Override
    public void save(Item item) {
        String sql = "INSERT INTO items "
                + "(name, startingPrice, description, status, imagePath, owner_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setString(1, item.getName());
            st.setDouble(2, item.getStartingPrice());
            st.setString(3, item.getDescription());

            // Đảm bảo có trạng thái mặc định nếu trạng thái của item bị null
            st.setString(4, item.getStatus() != null ? item.getStatus().name() : "AVAILABLE");

            st.setString(5, item.getImagePath());
            st.setInt(6, item.getOwnerId());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        item.setId(id);
                    }
                }
            } else {
                throw new DbException("Unexpected Error ! No rows affected !");
            }
            System.out.println("[System]: Item " + item.getName() + " saved to database.");
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void delete(Item item) {
        String sql = "DELETE FROM items WHERE items_id = ?";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, item.getId());
            int rows = st.executeUpdate();

            if (rows == 0) {
                throw new DbException("Item is invalid!");
            }
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void update(Item item) {
        String sql = "UPDATE items "
                + "SET name = ?, startingPrice = ?, description = ?, status = ?, imagePath = ?, owner_id = ? "
                + "WHERE items_id = ? ";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, item.getName());
            st.setDouble(2, item.getStartingPrice());
            st.setString(3, item.getDescription());
            st.setString(4, item.getStatus().name());
            st.setString(5, item.getImagePath());
            st.setInt(6, item.getOwnerId());
            st.setInt(7, item.getId());
            st.executeUpdate();

        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }
    @Override
    public Item getById(int id) {
        // FIX: Use getAuctionBaseSQL() instead of SELECT *
        String sql = getAuctionBaseSQL() + " WHERE i.items_id = ?";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return instantiateItem(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Item getByName(String name) {
        // FIX: Use getAuctionBaseSQL() instead of SELECT *
        String sql = getAuctionBaseSQL() + " WHERE i.name = ?";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, name);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return instantiateItem(rs);
                }
            }
            return null;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Item> getAll() {
        // FIX: Use getAuctionBaseSQL() instead of SELECT *
        String sql = getAuctionBaseSQL();
        List<Item> list = new ArrayList<>();
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                list.add(instantiateItem(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Item> getByOwnerId(int ownerId) {
        String sql = getAuctionBaseSQL() + " WHERE i.owner_id = ?";
        List<Item> list = new ArrayList<>();
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, ownerId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(instantiateItem(rs));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Item> getByOwnerId(int ownerId, int page, int size) {
        int offset = page * size;

        String sql = getAuctionBaseSQL() + " WHERE i.owner_id = ? ORDER BY i.items_id DESC LIMIT ? OFFSET ?";

        List<Item> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, ownerId);
            st.setInt(2, size);
            st.setInt(3, offset);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(instantiateItem(rs));
                }
            }
            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    private String getAuctionBaseSQL() {
        return "SELECT "
                + "i.items_id, "
                + "i.name,"
                + "i.startingPrice, "
                + "i.description, "
                + "i.status, "
                + "i.imagePath, "
                + "i.owner_id, "
                + "u_owner.firstName AS owner_firstName, u_owner.lastName AS owner_lastName, "
                + "u_owner.email AS owner_email, u_owner.phoneNumber AS owner_phoneNumber, "
                + "u_owner.password AS owner_password, u_owner.balance AS owner_balance, "
                + "u_owner.isAdmin AS owner_isAdmin, u_owner.isBlocked AS owner_isBlocked, "
                + "u_owner.blockedUntil AS owner_blockedUntil, "
                + "u_owner.avatar_path AS owner_avatar_path "
                + "FROM items i "
                + "LEFT JOIN users u_owner ON u_owner.users_id = i.owner_id";
    }

    private User instantiateMember(ResultSet rs) throws SQLException {
        User obj = new User(
                rs.getString("owner_firstName"),
                rs.getString("owner_lastName"),
                rs.getString("owner_email"),
                rs.getString("owner_phoneNumber"),
                rs.getString("owner_password"),
                rs.getDouble("owner_balance"),
                rs.getBoolean("owner_isAdmin"),
                rs.getBoolean("owner_isBlocked"),
                rs.getObject("owner_blockedUntil", LocalDateTime.class),
                rs.getString("owner_avatar_path")
        );
        obj.setId(rs.getInt("owner_id"));
        return obj;
    }

    private Item instantiateItem(ResultSet rs) throws SQLException {
        User owner = instantiateMember(rs);

        String statusStr = rs.getString("status");
        Item.Status status = Item.Status.AVAILABLE;

        if (statusStr != null) {
            try {
                status = Item.Status.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                System.err.println("[Warning]: Sai lệch Enum Status của Item ID " + rs.getInt("items_id"));
            }
        }

        Item obj = new Item(
                rs.getString("name"),
                rs.getDouble("startingPrice"),
                rs.getString("description"),
                status,
                rs.getString("imagePath")
        );

        obj.setId(rs.getInt("items_id"));
        obj.setOwner(owner);
        return obj;
    }
}