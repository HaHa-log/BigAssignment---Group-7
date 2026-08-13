package repositories.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import config.DB;
import config.DbException;
import models.Admin;
import models.Auction;
import models.Bidder;
import models.Item;
import models.User;
import repositories.AuctionsDAO;
import repositories.ItemsDAO;

/**
 * Implementation of AuctionsDAO interface for managing auction operations.
 */
public class AuctionsDAOImpl implements AuctionsDAO {

    protected AuctionsDAOImpl() {
    }

    @Override
    public void save(Auction auction) {

        String sql = "INSERT INTO auctions "
                + "(owner_id, item_id, startingPrice, currentPrice, startingTime, endingTime) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setInt(1, auction.getOwner().getId());
            st.setInt(2, auction.getItem().getId());
            st.setDouble(3, auction.getStartingPrice());
            st.setDouble(4, auction.getCurrentPrice());
            setLocalDateTime(st, 5, auction.getStartingTime());
            setLocalDateTime(st, 6, auction.getEndingTime());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        auction.setAuctionId(id);
                    }
                }
            } else {
                throw new DbException("Unexpected Error ! No rows affected !");
            }
            System.out.println("[System]: Auction saved to database.");
        } catch (SQLException ex) {
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void delete(Auction auction) {
        String sql = "DELETE FROM auctions WHERE auction_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, auction.getId());
            int rows = st.executeUpdate();

            if (rows == 0) {
                throw new DbException("Auction is invalid!");
            }
        } catch (SQLException ex) {
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void update(Auction auction) {
        String sql = "UPDATE auctions "
                + "SET owner_id = ?, item_id = ?, status = ?, startingPrice = ?, "
                + "currentPrice = ?, startingTime = ?, endingTime = ?, winner_id = ? "
                + " WHERE auction_id = ? ";
        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, auction.getOwner().getId());
            st.setInt(2, auction.getItem().getId());
            st.setString(3, auction.getStatus().name());
            st.setDouble(4, auction.getStartingPrice());
            st.setDouble(5, auction.getCurrentPrice());
            setLocalDateTime(st, 6, auction.getStartingTime());
            setLocalDateTime(st, 7, auction.getEndingTime());

            st.setObject(8, auction.getWinner() != null
                    ? ((User) auction.getWinner()).getId() : null);
            st.setInt(9, auction.getId());

            st.executeUpdate();

        } catch (SQLException ex) {
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public Auction getByIdWithLock(Connection conn, int id) {
        String sql = getAuctionBaseSql() + " WHERE a.auction_id = ? FOR UPDATE";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? instantiateAuction(rs) : null;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void updateWithConn(Connection conn, Auction auction) {
        String sql = "UPDATE auctions "
                + "SET currentPrice = ?, winner_id = ?, status = ?, endingTime = ? "
                + "WHERE auction_id = ?";
        try (PreparedStatement st = conn.prepareStatement(sql)) {
            st.setDouble(1, auction.getCurrentPrice());
            st.setObject(2, auction.getWinner() != null
                    ? ((User) auction.getWinner()).getId() : null);
            st.setString(3, auction.getStatus().name());
            setLocalDateTime(st, 4, auction.getEndingTime());
            st.setInt(5, auction.getId());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Auction getById(int id) {
        String sql = getAuctionBaseSql() + " WHERE a.auction_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return instantiateAuction(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Auction> getAll() {
        String sql = getAuctionBaseSql();

        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            List<Auction> list = new ArrayList<>();
            while (rs.next()) {
                list.add(instantiateAuction(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Auction> getAll(int page, int size, String status) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int offset = safePage * safeSize;

        boolean filterStatus = status != null
                && !status.isBlank()
                && !"ALL".equalsIgnoreCase(status);

        String sql = getAuctionBaseSql()
                + (filterStatus ? " WHERE a.status = ? " : " ")
                + " ORDER BY a.auction_id DESC LIMIT ? OFFSET ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            int index = 1;
            if (filterStatus) {
                st.setString(index++, status.toUpperCase());
            }
            st.setInt(index++, safeSize);
            st.setInt(index, offset);

            try (ResultSet rs = st.executeQuery()) {
                List<Auction> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(instantiateAuction(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Auction> getAllByUserId(int userId) {
        String sql = getAuctionBaseSql()
                + " LEFT JOIN bids b ON a.auction_id = b.auction_id"
                + " WHERE a.owner_id = ?"
                + " OR a.winner_id = ?"
                + " OR b.bidder_id = ?"
                + " GROUP BY a.auction_id"
                + " ORDER BY a.auction_id DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, userId);
            st.setInt(2, userId);
            st.setInt(3, userId);

            try (ResultSet rs = st.executeQuery()) {
                List<Auction> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(instantiateAuction(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Auction> getActiveAuctions() {
        String sql = getAuctionBaseSql() + " WHERE a.status = ? OR a.status = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, "RUNNING");
            st.setString(2, "OPEN");

            try (ResultSet rs = st.executeQuery()) {
                List<Auction> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(instantiateAuction(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    private Auction instantiateAuction(ResultSet rs) throws SQLException {
        Item item = instantiateItem(rs);
        Bidder winner = instantiateWinner(rs);
        Auction obj = new Auction(
                item.getOwner(),
                item,
                Auction.AuctionStatus.valueOf(rs.getString("status")),
                rs.getObject("startingTime", LocalDateTime.class),
                rs.getObject("endingTime", LocalDateTime.class),
                rs.getDouble("startingPrice"),
                rs.getDouble("currentPrice"),
                winner
        );

        obj.setAuctionId(rs.getInt("auction_id"));
        return obj;
    }

    private void setLocalDateTime(PreparedStatement st, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.TIMESTAMP);
            return;
        }
        st.setObject(index, value);
    }

    private String getAuctionBaseSql() {
        return "SELECT a.*, "
                + "u_owner.users_id AS owner_id, "
                + "u_owner.first_name AS owner_first_name, u_owner.last_name AS owner_last_name, "
                + "u_owner.email AS owner_email, u_owner.phone_number AS owner_phone_number, "
                + "u_owner.password AS owner_password, u_owner.balance AS owner_balance, "
                + "u_owner.is_admin AS owner_is_admin, u_owner.is_blocked AS owner_is_blocked, "
                + "u_owner.blocked_until AS owner_blockedUntil, "
                + "u_owner.avatar_path AS owner_avatar_path, "
                + "u_owner.frozen_balance AS owner_frozen_balance, "
                + "i.item_id AS item_id, "
                + "i.name AS item_name, i.\"startingPrice\" AS item_startingPrice, "
                + "i.description AS item_description, i.status AS item_status, "
                + "i.\"imagePath\" AS item_imagePath, i.owner_id AS item_owner_id, "
                + "u_winner.users_id AS winner_id, "
                + "u_winner.first_name AS winner_firstName, u_winner.last_name AS winner_lastName, "
                + "u_winner.email AS winner_email, u_winner.phone_number AS winner_phone_number, "
                + "u_winner.password AS winner_password, u_winner.balance AS winner_balance, "
                + "u_winner.is_admin AS winner_isAdmin, u_winner.is_blocked AS winner_isBlocked, "
                + "u_winner.blocked_until AS winner_blockedUntil, "
                + "u_winner.avatar_path AS owner_avatar_path, "
                + "u_winner.frozen_balance AS owner_frozen_balance "
                + "FROM auctions a "
                + "INNER JOIN users u_owner ON a.owner_id = u_owner.users_id "
                + "INNER JOIN items i ON a.item_id = i.item_id "
                + "LEFT JOIN users u_winner ON a.winner_id = u_winner.users_id";
    }

    private User instantiateOwner(ResultSet rs) throws SQLException {
        boolean isAdmin = rs.getBoolean("owner_isAdmin");

        User obj = isAdmin ? new Admin(
                rs.getString("owner_firstName"),
                rs.getString("owner_lastName"),
                rs.getString("owner_email"),
                rs.getString("owner_phoneNumber"),
                rs.getString("owner_password"),
                rs.getDouble("owner_balance"),
                true,
                rs.getBoolean("owner_isBlocked"),
                rs.getObject("owner_blockedUntil", LocalDateTime.class),
                rs.getString("owner_avatar_path"),
                rs.getDouble("owner_frozen_balance")
        ) : new User(
                rs.getString("owner_firstName"),
                rs.getString("owner_lastName"),
                rs.getString("owner_email"),
                rs.getString("owner_phoneNumber"),
                rs.getString("owner_password"),
                rs.getDouble("owner_balance"),
                false,
                rs.getBoolean("owner_isBlocked"),
                rs.getObject("owner_blockedUntil", LocalDateTime.class),
                rs.getString("owner_avatar_path"),
                rs.getDouble("owner_frozen_balance")
        );

        obj.setId(rs.getInt("owner_id"));
        return obj;
    }

    private Item instantiateItem(ResultSet rs) throws SQLException {
        User owner = instantiateOwner(rs);
        Item obj = new Item(
                rs.getString("item_name"),
                rs.getDouble("item_startingPrice"),
                rs.getString("item_description"),
                Item.Status.valueOf(rs.getString("item_status")),
                rs.getString("item_imagePath")
        );
        obj.setId(rs.getInt("item_id"));
        obj.setOwner(owner);
        return obj;
    }

    private Bidder instantiateWinner(ResultSet rs) throws SQLException {
        if (rs.getObject("winner_id") == null || rs.getInt("winner_id") == 0) {
            return null;
        }

        boolean isAdmin = rs.getBoolean("winner_isAdmin");
        User obj = isAdmin ? new Admin(
                rs.getString("winner_firstName"),
                rs.getString("winner_lastName"),
                rs.getString("winner_email"),
                rs.getString("winner_phoneNumber"),
                rs.getString("winner_password"),
                rs.getDouble("winner_balance"),
                true,
                rs.getBoolean("winner_isBlocked"),
                rs.getObject("winner_blockedUntil", LocalDateTime.class),
                rs.getString("winner_avatar_path"),
                rs.getDouble("winner_frozen_balance")
        ) : new User(
                rs.getString("winner_firstName"),
                rs.getString("winner_lastName"),
                rs.getString("winner_email"),
                rs.getString("winner_phoneNumber"),
                rs.getString("winner_password"),
                rs.getDouble("winner_balance"),
                false,
                rs.getBoolean("winner_isBlocked"),
                rs.getObject("winner_blockedUntil", LocalDateTime.class),
                rs.getString("winner_avatar_path"),
                rs.getDouble("winner_frozen_balance")
        );

        obj.setId(rs.getInt("winner_id"));
        return obj;
    }
}
