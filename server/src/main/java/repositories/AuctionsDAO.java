package repositories;

import models.Auction;

import java.util.List;

public interface AuctionsDAO extends DAO<Auction> {

    List<Auction> getActiveAuctions();

    List<Auction> getAll(int page, int size, String status);

    List<Auction> getAllByUserId(int userId);
}
