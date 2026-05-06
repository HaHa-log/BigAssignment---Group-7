package model.impl;

import Branch.*;
import DB.DB;
import DB.DbException;
import model.AuctionsDAO;
import model.ItemsDAO;
import model.UsersDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionsDAOImpl implements AuctionsDAO {

    private UsersDAO userDb = DaoFactory.createUsersDAO();
    private ItemsDAO itemDb = DaoFactory.createItemDAO();

    protected AuctionsDAOImpl() {}

    @Override
    public void save(Auction auction) {
        String sql = "INSERT INTO auctions "
                + "(owner_id, item_id, startingPrice, currentPrice) "
                + "VALUES (?, ?, ?, ?)";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            st.setInt(1, auction.getSeller().getId());
            st.setInt(2, auction.getItem().getId());
            st.setDouble(3, auction.getStartingPrice());
            st.setDouble(4, auction.getCurrentPrice());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = st.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        auction.setAuctionId(id);
                        auction.setStartingTime(LocalDateTime.now());
                    }
                }
            } else {
                throw new DbException("Unexpected Error ! No rows affected !");
            }
            System.out.println("[System]: Auction saved to database.");
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void delete(Auction auction) {
        String sql = "DELETE FROM auctions WHERE auctions_id = ?";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, auction.getId());
            int rows = st.executeUpdate();

            if (rows == 0) {
                throw new DbException("Auction is invalid!");
            }
        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public void update(Auction auction) {
        String sql = "UPDATE auctions "
                +"SET owner_id = ?, item_id = ?, status = ?, startingPrice = ?, currentPrice = ?, terminatedAt = ?, winner_id = ? "
                + " WHERE auctions_id = ? ";
        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, auction.getSeller().getId());
            st.setInt(2, auction.getItem().getId());
            st.setString(3, auction.getStatus().name());
            st.setDouble(4, auction.getStartingPrice());
            st.setDouble(5, auction.getCurrentPrice());
            st.setTimestamp(6, auction.getEndingTime() != null
                    ? Timestamp.valueOf(auction.getEndingTime()) : null);
            st.setObject(7, auction.getWinner() != null
                    ? (auction.getWinner()).getId() : null);
            st.setInt(8, auction.getId());

            st.executeUpdate();

        } catch(SQLException ex){
            throw new DbException(ex.getMessage());
        }
    }

    @Override
    public Auction getById(int id) {
        String sql = "SELECT * From auctions WHERE auctions_id = ?";

        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()){
                    Auction auction = instantiateAuction(rs);
                    return auction;
                }
            }
            return null;
        } catch (SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Auction> getAll() {
        String sql = "SELECT * FROM auctions";

        try(Connection conn = DB.getConnection();
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
    public List<Auction> getActiveAuctions() {
        String sql = "SELECT * FROM auctions WHERE status = ? OR status = ?";
        List<Auction> list = new ArrayList<>();

        try(Connection conn = DB.getConnection();
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, "RUNNING");
            st.setString(2, "OPEN");

            try (ResultSet rs = st.executeQuery()) {
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
        Auction obj = new Auction(
                (Member) userDb.getById(rs.getInt("owner_id")),
                itemDb.getById(rs.getInt("item_id")),
                rs.getObject("createdAt", LocalDateTime.class),
                rs.getObject("terminatedAt", LocalDateTime.class),
                Auction.AuctionStatus.valueOf(rs.getString("status")),
                rs.getDouble("startingPrice"),
                rs.getDouble("currentPrice"),
                (Bidder) userDb.getById(rs.getInt("winner_id"))
        );

        obj.setAuctionId(rs.getInt("auctions_id"));
        return obj;
    }
}
