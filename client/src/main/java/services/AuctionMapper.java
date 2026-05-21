package services;

import models.Auction;
import models.Item;
import models.Member;
import services.dto.auction.AuctionResponse;

public final class AuctionMapper {
    private static final String SESSION_PASSWORD_PLACEHOLDER = "server-authenticated";

    private AuctionMapper() {
    }

    public static Auction toAuction(AuctionResponse response) {
        String[] nameParts = splitName(response.getOwnerName());
        Member owner = new Member(
                nameParts[0],
                nameParts[1],
                "owner-" + response.getOwnerId() + "@server.local",
                "0000000000",
                SESSION_PASSWORD_PLACEHOLDER,
                0,
                null
        );
        owner.setId(response.getOwnerId());

        Item item = new Item(
                response.getItemName(),
                response.getStartingPrice(),
                response.getItemDescription(),
                Item.Status.IN_AUCTION,
                null,
                null,
                response.getItemImagePath()
        );
        item.setId(response.getItemId());
        item.setOwnerId(response.getOwnerId());

        Auction auction = new Auction(
                owner,
                item,
                Auction.AuctionStatus.valueOf(response.getStatus()),
                response.getStartingTime(),
                response.getEndingTime(),
                response.getStartingPrice(),
                response.getCurrentPrice(),
                null
        );
        auction.setAuctionId(response.getId());
        return auction;
    }

    private static String[] splitName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[] {"Unknown", "User"};
        }

        String[] parts = fullName.trim().split("\\s+", 2);
        if (parts.length == 1) {
            return new String[] {parts[0], "User"};
        }
        return parts;
    }
}
