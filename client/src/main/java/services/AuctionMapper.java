package services;

import models.Auction;
import models.Item;
import models.Member;
import com.group7.dto.auction.*;
import java.time.LocalDateTime;

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
                LocalDateTime.now(),
                LocalDateTime.now(),
                response.getItemImagePath()
        );
        item.setId(response.getItemId());
        item.setOwnerId(response.getOwnerId());

        Member winner = null;
        if (response.getWinnerId() != null && response.getWinnerId() > 0) {
            String[] winnerNameParts = splitName(response.getWinnerName());
            winner = new Member(
                    winnerNameParts[0],
                    winnerNameParts[1],
                    "winner-" + response.getWinnerId() + "@server.local",
                    "0000000000",
                    SESSION_PASSWORD_PLACEHOLDER,
                    0,
                    null
            );
            winner.setId(response.getWinnerId());
        }

        Auction auction = new Auction(
                owner,
                item,
                Auction.AuctionStatus.valueOf(response.getStatus()),
                response.getStartingTime(),
                response.getEndingTime(),
                response.getStartingPrice(),
                response.getCurrentPrice(),
                winner
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