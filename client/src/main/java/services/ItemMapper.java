package services;

import com.group7.dto.item.ItemResponse;
import models.Item;
import java.time.LocalDateTime;

public final class ItemMapper {

    private ItemMapper() {}

    public static Item toItem(ItemResponse response) {
        if (response == null) return null;

        Item.Status mappedStatus = Item.Status.valueOf(response.getStatus());

        Item item = new Item(
                response.getName(),
                response.getStartingPrice(),
                response.getDescription(),
                mappedStatus,
                LocalDateTime.now(),
                LocalDateTime.now(),
                response.getImagePath()
        );

        item.setId(response.getId());
        return item;
    }
}