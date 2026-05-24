package repositories.impl;

import models.Member;
import models.Transaction;
import config.DB;
import config.DbException;
import models.Auction;
import repositories.AuctionsDAO;
import repositories.TransactionDAO;
import repositories.UsersDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO {
    UsersDAO userDAO = DaoFactory.createUsersDAO();
    AuctionsDAO auctionsDAO = DaoFactory.createAuctionsDAO();

    protected TransactionDAOImpl() {};

    @Override
    public void save(Transaction transaction) {
        String sql = "INSERT INTO transaction "
                + "(auction_id, buyer_id, seller_id, finalAmount, paidAt, completedAt, status, expiry_time) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setInt(1, transaction.getAuction().getId());
            st.setInt(2, transaction.getBuyer().getId());
            st.setInt(3, transaction.getSeller().getId());
            st.setDouble(4, transaction.getFinalAmount());
            st.setTimestamp(5, transaction.getPaidAt() != null
                    ? Timestamp.valueOf(transaction.getPaidAt()) : null);
            st.setTimestamp(6, transaction.getCompletedAt() != null
                    ? Timestamp.valueOf(transaction.getCompletedAt()) : null);
            st.setString(7, transaction.getStatus().name());
            st.setTimestamp(8, transaction.getExpiryTime() != null ? Timestamp.valueOf(transaction.getExpiryTime()) : null);

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        transaction.setTransactionId(id);
                    }
                }
            } else {
                throw new DbException("Unexpected Error ! No rows affected !");
            }

            System.out.println("[System]: Transaction saved.");
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void delete(Transaction transaction) {
        String sql = "DELETE FROM transaction WHERE transaction_id = ?";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, transaction.getTransactionId());
            int rows = st.executeUpdate();

            if (rows == 0) {
                throw new DbException("Transaction is invalid!");
            }
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void update(Transaction transaction) {
        String sql = "UPDATE transaction "
                +"SET auction_id = ?, buyer_id = ?, seller_id = ?, finalAmount = ?, paidAt = ?, completedAt = ?, status = ?, expiry_time = ? "
                + " WHERE transaction_id = ? ";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, transaction.getAuction().getId());
            st.setInt(2, transaction.getBuyer().getId());
            st.setInt(3, transaction.getSeller().getId());
            st.setDouble(4, transaction.getFinalAmount());
            st.setTimestamp(5, transaction.getPaidAt() != null
                    ? Timestamp.valueOf(transaction.getPaidAt()) : null);
            st.setTimestamp(6, transaction.getCompletedAt() != null
                    ? Timestamp.valueOf(transaction.getCompletedAt()) : null);
            st.setString(7, transaction.getStatus().name());
            st.setTimestamp(8, transaction.getExpiryTime() != null ? Timestamp.valueOf(transaction.getExpiryTime()) : null);
            st.setInt(9, transaction.getTransactionId());

            st.executeUpdate();

        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public Transaction getById(int id) {
        String sql = "SELECT * From transaction WHERE transaction_id = ?";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()){
                    return instantiateTransaction(rs);
                }
            }
            return null;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Transaction> getByUserId(int userId) {
        String sql = "SELECT * FROM transaction WHERE buyer_id = ? OR seller_id = ?";
        List<Transaction> list = new ArrayList<>();

        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, userId);
            st.setInt(2, userId);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Transaction t = instantiateTransaction(rs);
                    if (t != null) list.add(t);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public Transaction getPendingByAuctionAndBuyer(int auctionId, int buyerId) {
        String sql = "SELECT * FROM transaction "
                + "WHERE auction_id = ? AND buyer_id = ? AND status = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, auctionId);
            st.setInt(2, buyerId);
            st.setString(3, Transaction.TransactionStatus.PENDING.name());

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return instantiateTransaction(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Transaction> getAll() {
        String sql = "SELECT * FROM transaction";

        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery()) {

            List<Transaction> list = new ArrayList<>();

            while (rs.next()) {
                list.add(instantiateTransaction(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    private Transaction instantiateTransaction(ResultSet rs) throws SQLException {
        int auctionId = rs.getInt("auction_id");
        if (rs.wasNull()) return null;

        Auction auction = auctionsDAO.getById(auctionId);
        if (auction == null) return null;

        Member buyer = userDAO.getById(rs.getInt("buyer_id"));
        Member seller = userDAO.getById(rs.getInt("seller_id"));
        if (!(buyer instanceof Member) || !(seller instanceof Member)) return null;

        Transaction obj = new Transaction(
                auctionsDAO.getById(rs.getInt("auction_id")),
                userDAO.getById(rs.getInt("buyer_id")),
                userDAO.getById(rs.getInt("seller_id")),
                rs.getDouble("finalAmount"),
                rs.getObject("paidAt", LocalDateTime.class),
                rs.getObject("completedAt", LocalDateTime.class),
                Transaction.TransactionStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("expiry_time") != null ? rs.getTimestamp("expiry_time").toLocalDateTime() : null
        );

        obj.setTransactionId(rs.getInt("transaction_id"));
        return obj;
    }
}