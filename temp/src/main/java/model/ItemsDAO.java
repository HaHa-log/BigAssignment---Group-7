package model;

import Branch.Item;

import java.util.List;

public interface ItemsDAO extends DAO<Item> {

    Item getByName(String name);
    List<Item> getByOwnerId(int ownerId);

}
