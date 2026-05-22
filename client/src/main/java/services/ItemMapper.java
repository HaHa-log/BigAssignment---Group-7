package services;

import com.group7.dto.item.ItemResponse;
import models.Item;

public final class ItemMapper {

    private ItemMapper() {}

    public static Item toItem(ItemResponse response) {
        if (response == null) return null;

        // Map String status back to your local Java enum safety layer
        Item.Status mappedStatus = Item.Status.valueOf(response.getStatus());

        Item item = new Item(
                response.getName(),
                response.getStartingPrice(),
                response.getDescription(),
                mappedStatus,
                null, // Winner (Null if just sitting in inventory)
                null, // Auction reference
                response.getImagePath()
        );

        item.setId(response.getId());
        return item;
    }
}