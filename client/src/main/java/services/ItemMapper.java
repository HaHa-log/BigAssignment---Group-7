package services;

import com.group7.dto.item.ItemResponse;
import models.Item;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                response.getImagePath(),
                response.getOwnerId(),
                response.getOwnerName(),
                response.getActiveAuctionId() != null ? response.getActiveAuctionId() : -1,
                response.getCurrentAuctionPrice() != null ? response.getCurrentAuctionPrice() : response.getStartingPrice()
        );

        item.setId(response.getId());
        return item;
    }

    public static List<Item> toItemList(List<ItemResponse> responseList) {
        if (responseList == null || responseList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> items = new ArrayList<>();
        for (ItemResponse dto : responseList) {
            Item item = toItem(dto);
            if (item != null) {
                items.add(item);
            }
        }
        return items;
    }
}