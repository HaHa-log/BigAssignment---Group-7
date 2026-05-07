package model.impl;

import Branch.*;
import DB.DB;
import DB.DbException;
import model.AuctionsDAO;
import model.BidsDAO;
import model.UsersDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BidsDAOImpl implements BidsDAO {
    private AuctionsDAO auctionDb = DaoFactory.createAuctionsDAO();
    private UsersDAO bidderDb = DaoFactory.createUsersDAO();

    protected BidsDAOImpl() {}

    @Override
    public void save(Bid bid) {
        String sql = "INSERT INTO bids "
                + "(auction_id, bidder_id, bidPrice) "
                + "VALUES (?, ?, ?)";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setInt(1, bid.getAuction().getId());
            st.setInt(2, bid.getBidder().getId());
            st.setDouble(3, bid.getBidPrice());
            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        bid.setId(id);
                    }
                }
            } else {
                throw new DbException("Unexpected Error ! No rows affected !");
            }
            System.out.println("[System]: Bid saved to database.");
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void delete(Bid bid) {
        String sql = "DELETE FROM bids WHERE bids_id = ?";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, bid.getId());
            int rows = st.executeUpdate();

            if (rows == 0) {
                throw new DbException("Bid is invalid!");
            }
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void update(Bid bid) {
        String sql = "UPDATE bids "
                +"SET auction_id = ?, bidder_id = ?, bidPrice = ?, bidTime = ? "
                + " WHERE bids_id = ? ";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, bid.getAuction().getId());
            st.setInt(2, bid.getBidder().getId());
            st.setDouble(3, bid.getBidPrice());
            st.setTimestamp(4, bid.getBidTime() != null
                    ? Timestamp.valueOf(bid.getBidTime()) : null);
            st.setDouble(5, bid.getId());

            st.executeUpdate();

        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public Bid getById(int id) {
        String sql = "SELECT * From bids WHERE bids_id = ?";

        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()){
                    Bid bid = instantiateBid(rs);
                    return bid;
                }
            }
            return null;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Bid> getByAuctionId(int id) {
        String sql = "SELECT * From bids WHERE auction_id = ?";
        List<Bid> list = new ArrayList<>();

        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()){
                    list.add(instantiateBid(rs));
                }
            }
            return list;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Bid> getAll() {
        String sql = "SELECT * FROM bids";

        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery()) {

            List<Bid> list = new ArrayList<>();

            while (rs.next()) {
                list.add(instantiateBid(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    private Bid instantiateBid(ResultSet rs) throws SQLException {
        Bid obj = new Bid(
                auctionDb.getById(rs.getInt("auction_id")),
                (Member) bidderDb.getById(rs.getInt("auction_id")),
                rs.getDouble("bidPrice"),
                rs.getObject("bidTime", LocalDateTime.class)
        );

        obj.setId(rs.getInt("bids_id"));
        return obj;
    }
}
