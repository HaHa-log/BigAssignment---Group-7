package repositories;

import models.Auction;

import java.sql.Connection;
import java.util.List;

public interface AuctionsDAO extends DAO<Auction> {

    List<Auction> getActiveAuctions();

    List<Auction> getAll(int page, int size, String status);

    List<Auction> getAllByUserId(int userId);

    Auction getByIdWithLock(Connection conn, int id);

    void updateWithConn(Connection conn, Auction auction);
}
