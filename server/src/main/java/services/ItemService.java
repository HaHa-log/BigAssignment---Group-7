package services;

import com.group7.dto.item.ItemRequest;
import com.group7.dto.item.ItemResponse;
import com.group7.dto.item.UpdateItemRequest;
import models.Item;
import models.User;
import org.springframework.stereotype.Service;
import repositories.ItemsDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import java.util.List;

@Service
public class ItemService {

    private final ItemsDAO itemsDb = DaoFactory.createItemDAO();
    private final UsersDAO usersDb = DaoFactory.createUsersDAO();

    public List<ItemResponse> getAll() {
        return itemsDb.getAll().stream().map(this::toResponse).toList();
    }

    public ItemResponse getById(int id) {
        return toResponse(requireItem(id));
    }

    public List<ItemResponse> getByOwnerId(int ownerId) {
        return itemsDb.getByOwnerId(ownerId).stream().map(this::toResponse).toList();
    }

    public Item createNewItem(ItemRequest request) {
        validateCreateRequest(request);

        User owner = usersDb.getById(request.getOwnerId());
        if (owner == null) {
            throw new IllegalArgumentException("[Error]: Owner not found or not a member.");
        }
        Item item = new Item(request.getName(), request.getStartingPrice(), request.getDescription());
        item.setOwner(owner);
        item.setStatus(Item.Status.AVAILABLE);

        return item;
    }

    public List<ItemResponse> getItemsByOwner(int ownerId, int page, int size) {
        List<Item> items = itemsDb.getByOwnerId(ownerId, page, size);

        return items.stream().map(item -> {
            ItemResponse response = new ItemResponse();
            response.setId(item.getId());
            response.setName(item.getName());
            response.setStartingPrice(item.getStartingPrice());
            response.setDescription(item.getDescription());
            response.setStatus(item.getStatus() != null ? item.getStatus().name() : "AVAILABLE");
            response.setImagePath(item.getImagePath());
            response.setActiveAuctionId(item.getActiveAuctionId());
            response.setCurrentAuctionPrice(item.getCurrentAuctionPrice());
            return response;
        }).toList();
    }

    public List<ItemResponse> getItemsByOwner(int ownerId) {
        return getItemsByOwner(ownerId, 0, 10); // Mặc định lấy trang đầu, 10 items
    }

    public ItemResponse update(int id, UpdateItemRequest req) {
        Item item = requireItem(id);

        if (req.getName() != null && !req.getName().isBlank()) {
            item.setName(req.getName().trim());
        }
        if (req.getDescription() != null) {
            item.setDescription(req.getDescription().trim());
        }
        if (req.getStartingPrice() != null) {
            if (req.getStartingPrice() <= 0) {
                throw new IllegalArgumentException("[Error]: startingPrice must be > 0.");
            }
            item.setStartingPrice(req.getStartingPrice());
        }
        if (req.getImagePath() != null) {
            item.setImagePath(req.getImagePath().trim());
        }
        if (req.getStatus() != null && !req.getStatus().isBlank()) {
            item.setStatus(parseStatus(req.getStatus()));
        }

        itemsDb.update(item);
        return toResponse(item);
    }

    public ItemResponse updateImage(int id, String filename) {
        Item item = requireItem(id);
        item.setImagePath(filename); // save filename
        itemsDb.update(item);
        return toResponse(item);
    }

    public void delete(int id) {
        Item item = requireItem(id);
        itemsDb.delete(item);
    }

    private void validateCreateRequest(ItemRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("[Error]: Request body is required.");
        }
        if (req.getName() == null || req.getName().isBlank()) {
            throw new IllegalArgumentException("[Error]: Item name is required.");
        }
        if (req.getStartingPrice() <= 0) {
            throw new IllegalArgumentException("[Error]: startingPrice must be > 0.");
        }
        if (req.getOwnerId() <= 0) {
            throw new IllegalArgumentException("[Error]: ownerId must be valid.");
        }
    }

    private Item requireItem(int id) {
        Item item = itemsDb.getById(id);
        if (item == null) {
            throw new IllegalArgumentException("[Error]: Item not found.");
        }
        return item;
    }

    private Item.Status parseStatus(String raw) {
        try {
            return Item.Status.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("[Error]: Invalid item status: " + raw);
        }
    }

    private ItemResponse toResponse(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getStartingPrice(),
                item.getDescription(),
                item.getStatus().name(),
                item.getImagePath(),
                item.getOwnerId(),
                item.getOwner().getFullName()
        );
    }
}