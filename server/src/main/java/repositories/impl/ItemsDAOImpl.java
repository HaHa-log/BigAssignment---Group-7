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
                + "(name, starting_price, description, status, image_path, owner_id) "
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
        String sql = "DELETE FROM items WHERE item_id = ?";
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
                + "SET name = ?, starting_price = ?, description = ?, status = ?, image_path = ?, owner_id = ? "
                + "WHERE item_id = ? ";
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
        String sql = getAuctionBaseSQL() + " WHERE i.item_id = ?";
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

        String sql = getAuctionBaseSQL() + " WHERE i.owner_id = ? ORDER BY i.item_id DESC LIMIT ? OFFSET ?";

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
                + "i.item_id, i.name, i.starting_price, i.description, "
                + "i.status, i.image_path, i.owner_id, "
                + "u_owner.first_name AS owner_first_name, "
                + "u_owner.last_name AS owner_last_name, "
                + "u_owner.email AS owner_email, "
                + "u_owner.phone_number AS owner_phone_number, "
                + "u_owner.password AS owner_password, "
                + "u_owner.balance AS owner_balance, "
                + "u_owner.is_admin AS owner_is_admin, "
                + "u_owner.is_blocked AS owner_is_blocked, "
                + "u_owner.blocked_until AS owner_blocked_until, "
                + "u_owner.avatar_path AS owner_avatar_path, "
                + "u_owner.frozen_balance AS owner_frozen_balance, "
                + "a.auction_id AS active_auction_id, "
                + "a.current_price AS auction_current_price "
                + "FROM items i "
                + "LEFT JOIN users u_owner ON u_owner.users_id = i.owner_id "
                + "LEFT JOIN auctions a ON a.item_id = i.item_id "
                + "AND a.status IN ('OPEN', 'RUNNING')";
    }

    private User instantiateMember(ResultSet rs) throws SQLException {
        User obj = new User(
                rs.getString("owner_first_name"),
                rs.getString("owner_last_name"),
                rs.getString("owner_email"),
                rs.getString("owner_phone_number"),
                rs.getString("owner_password"),
                rs.getDouble("owner_balance"),
                rs.getBoolean("owner_is_admin"),
                rs.getBoolean("owner_is_blocked"),
                rs.getObject("owner_blocked_until", LocalDateTime.class),
                rs.getString("owner_avatar_path"),
                rs.getDouble("owner_frozen_balance")
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
                System.err.println("[Warning]: Sai lệch Enum Status của Item ID " + rs.getInt("item_id"));
            }
        }

        Item obj = new Item(
                rs.getString("name"),
                rs.getDouble("starting_price"),
                rs.getString("description"),
                status,
                rs.getString("image_path")
        );

        int activeAuctionId = rs.getInt("active_auction_id");
        if (!rs.wasNull()) {
            obj.setActiveAuctionId(activeAuctionId);
        }

        double auctionPrice = rs.getDouble("auction_current_price");
        if (!rs.wasNull()) {
            obj.setCurrentAuctionPrice(auctionPrice);
        }
        obj.setId(rs.getInt("item_id"));
        obj.setOwner(owner);
        return obj;
    }
}
