package services;

import com.group7.dto.item.ItemRequest;
import com.group7.dto.item.ItemResponse;
import models.Item;
import org.springframework.stereotype.Service;
import repositories.ItemsDAO;
import repositories.impl.DaoFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    private final ItemsDAO itemsDb = DaoFactory.createItemDAO();

    public void createNewItem(ItemRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Item payload cannot be null.");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required.");
        }

        Item item = new Item(request.getName(), request.getStartingPrice(), request.getDescription());
        item.setImagePath(request.getImagePath());
        item.setOwnerId(request.getOwnerId().intValue());
        item.setStatus(Item.Status.AVAILABLE);

        itemsDb.save(item);
    }

    public List<ItemResponse> getItemsByOwner(int ownerId) {
        List<Item> databaseItems = itemsDb.getByOwnerId(ownerId); // Assumes this exists in your DAO
        List<ItemResponse> responses = new ArrayList<>();

        for (Item item : databaseItems) {
            responses.add(new ItemResponse(
                    (int) item.getId(),
                    item.getName(),
                    item.getStartingPrice(),
                    item.getDescription(),
                    item.getStatus().name(),
                    item.getImagePath(),
                    (int) item.getOwnerId()
            ));
        }

        return responses;
    }
}