package model.impl;

import Branch.Auction;
import Branch.Member;
import Branch.Transaction;
import DB.DB;
import DB.DbException;
import model.AuctionsDAO;
import model.TransactionDAO;
import model.UsersDAO;

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
                + "(aucion_id, buyer_id, seller_id, finalAnount, paidAt, completedAt, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setInt(1, transaction.getAuction().getId());
            st.setInt(2, transaction.getBuyer().getId());
            st.setInt(3, transaction.getSeller().getId());
            st.setDouble(4, transaction.getFinalAmount());
            st.setTimestamp(5, transaction.getPaidAt() != null
                    ? Timestamp.valueOf(transaction.getPaidAt()) : null);;
            st.setTimestamp(6, transaction.getCompletedAt() != null
                    ? Timestamp.valueOf(transaction.getCompletedAt()) : null);;
            st.setString(7, transaction.getStatus().name());

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
        String sql = "UPDATE users "
                +"SET auction_id = ?, buyer_id = ?, seller_id = ?, finalAmount = ?, paidAt = ?, completedAt = ?, status =? "
                + " WHERE transaction_id = ? ";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, transaction.getAuction().getId());
            st.setInt(2, transaction.getBuyer().getId());
            st.setInt(3, transaction.getSeller().getId());
            st.setDouble(4, transaction.getFinalAmount());
            st.setTimestamp(5, transaction.getPaidAt() != null
                    ? Timestamp.valueOf(transaction.getPaidAt()) : null);;
            st.setTimestamp(6, transaction.getCompletedAt() != null
                    ? Timestamp.valueOf(transaction.getCompletedAt()) : null);;
            st.setString(7, transaction.getStatus().name());
            st.setInt(8, transaction.getTransactionId());

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
                    Transaction transaction = instantiateTransaction(rs);
                    return transaction;
                }
            }
            return null;
        } catch (SQLException e){
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
        Transaction obj = new Transaction(
                auctionsDAO.getById(rs.getInt("auction_id")),
                (Member) userDAO.getById(rs.getInt("buyer_id")),
                (Member) userDAO.getById(rs.getInt("seller_id")),
                rs.getDouble("finalAmount"),
                rs.getObject("paidAt", LocalDateTime.class),
                rs.getObject("completedAt", LocalDateTime.class),
                Transaction.TransactionStatus.valueOf(rs.getString("status"))
        );

        obj.setTransactionId(rs.getInt("transaction_id"));
        return obj;
    }
}
