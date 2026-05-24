package repositories.impl;

import models.AutoBid;
import models.BidStepConfiguration;
import models.User;
import config.DB;
import config.DbException;
import repositories.AuctionsDAO;
import repositories.AutoBidDAO;
import repositories.UsersDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AutoBidDAOImpl implements AutoBidDAO {
    private final UsersDAO userDb = DaoFactory.createUsersDAO();
    private final AuctionsDAO auctionsDAO = DaoFactory.createAuctionsDAO();

    protected AutoBidDAOImpl() {}

    @Override
    public void save(AutoBid obj) {
        double currentPrice = obj.getAuction().getCurrentPrice();
        if (!BidStepConfiguration.isValidStep(currentPrice, obj.getIncrement())) {
            throw new DbException("[Error]: Cannot save AutoBid configuration. Increment amount "
                    + obj.getIncrement() + " violates system pricing tier rules!");
        }

        String sql = "INSERT INTO auto_bids (auction_id, user_id, max_bid, increment_amount) "
                + "VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE max_bid = ?, increment_amount = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, obj.getAuction().getId());
            st.setInt(2, obj.getUser().getId());
            st.setDouble(3, obj.getMaxBid());
            st.setDouble(4, obj.getIncrement());
            st.setDouble(5, obj.getMaxBid());
            st.setDouble(6, obj.getIncrement());

            st.executeUpdate();
            System.out.println("[System]: AutoBid configuration saved/updated.");
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void delete(AutoBid obj) {
        String sql = "DELETE FROM auto_bids WHERE auction_id = ? AND user_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, obj.getAuction().getId());
            st.setInt(2, obj.getUser().getId());
            int rows = st.executeUpdate();

            if (rows == 0) {
                throw new DbException("AutoBid configuration is invalid!");
            }
            System.out.println("[System]: AutoBid deleted from database.");
        } catch (SQLException ex) {
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void update(AutoBid obj) {
        String sql = "UPDATE auto_bids "
                + "SET max_bid = ?, increment_amount = ? "
                + "WHERE auction_id = ? AND user_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setDouble(1, obj.getMaxBid());
            st.setDouble(2, obj.getIncrement());
            st.setInt(3, obj.getAuction().getId());
            st.setInt(4, obj.getUser().getId());

            st.executeUpdate();
            System.out.println("[System]: AutoBid updated in database.");
        } catch (SQLException ex) {
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public List<AutoBid> getByAuctionId(int auctionId) {
        String sql = "SELECT * FROM auto_bids WHERE auction_id = ?";
        List<AutoBid> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, auctionId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(instantiateAutoBid(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public AutoBid getByAuctionAndUser(int auctionId, int userId) {
        String sql = "SELECT * FROM auto_bids WHERE auction_id = ? AND user_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, auctionId);
            st.setInt(2, userId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return instantiateAutoBid(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public AutoBid getById(int id) {
        return null;
    }

    @Override
    public List<AutoBid> getAll() {
        String sql = "SELECT * FROM auto_bids";
        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            List<AutoBid> list = new ArrayList<>();
            while (rs.next()) {
                list.add(instantiateAutoBid(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    private AutoBid instantiateAutoBid(ResultSet rs) throws SQLException {
        User user = userDb.getById(rs.getInt("user_id"));
        return new AutoBid(
                auctionsDAO.getById(rs.getInt("auction_id")),
                user,
                rs.getDouble("max_bid"),
                rs.getDouble("increment_amount")
        );
    }
}