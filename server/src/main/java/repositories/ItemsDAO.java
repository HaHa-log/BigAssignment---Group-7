package repositories;

import models.Item;

import java.util.List;

public interface ItemsDAO extends DAO<Item> {

    Item getByName(String name);
    List<Item> getByOwnerId(int ownerId);
    List<Item> getByOwnerId(int ownerId, int page, int size);
}
