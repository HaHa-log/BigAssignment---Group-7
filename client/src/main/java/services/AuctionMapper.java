package services;

import models.Auction;
import models.Item;
import models.Bid;
import models.User; // ĐỒNG BỘ: Sử dụng lớp User phẳng mới thay thế Member cũ
import com.group7.dto.auction.*;
import com.group7.dto.bid.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class AuctionMapper {
    private static final String SESSION_PASSWORD_PLACEHOLDER = "server-authenticated";

    private AuctionMapper() {
    }

    public static List<Auction> toAuctionList(List<AuctionResponse> responses) {
        if (responses == null) {
            return new ArrayList<>();
        }
        return responses.stream()
                .map(AuctionMapper::toAuction)
                .collect(Collectors.toList());
    }

    public static Auction toAuction(AuctionResponse response) {
        if (response == null) return null;

        String[] nameParts = splitName(response.getOwnerName());

        // ĐỒNG BỘ: Sửa đổi truyền đầy đủ chính xác 10 tham số cho Constructor lớp User mới
        User owner = new User(
                nameParts[0],
                nameParts[1],
                "owner-" + response.getOwnerId() + "@server.local",
                "0000000000",
                SESSION_PASSWORD_PLACEHOLDER,
                0,
                false, // isAdmin
                false, // isBlocked
                null,  // blockedUntil
                null   // avatarPath
        );
        owner.setId(response.getOwnerId());

        Item item = new Item(
                response.getItemName(),
                response.getStartingPrice(),
                response.getItemDescription(),
                Item.Status.IN_AUCTION,
                response.getStartingTime() != null ? response.getStartingTime() : LocalDateTime.now(),
                LocalDateTime.now(),
                response.getItemImagePath()
        );
        item.setId(response.getItemId());
        item.setOwnerId(response.getOwnerId());

        User winner = null; // ĐỒNG BỘ: Sửa kiểu dữ liệu sang User phẳng
        if (response.getWinnerId() != null && response.getWinnerId() > 0) {
            String[] winnerNameParts = splitName(response.getWinnerName());
            // ĐỒNG BỘ: Truyền đầy đủ chính xác 10 tham số
            winner = new User(
                    winnerNameParts[0],
                    winnerNameParts[1],
                    "winner-" + response.getWinnerId() + "@server.local",
                    "0000000000",
                    SESSION_PASSWORD_PLACEHOLDER,
                    0,
                    false, // isAdmin
                    false, // isBlocked
                    null,  // blockedUntil
                    null   // avatarPath
            );
            winner.setId(response.getWinnerId());
        }

        // ĐỒNG BỘ: Gọi Constructor nhận tham số winner kiểu User
        Auction auction = new Auction(
                owner,
                item,
                parseAuctionStatus(response.getStatus()),
                response.getStartingTime(),
                response.getEndingTime(),
                response.getStartingPrice(),
                response.getCurrentPrice(),
                winner
        );
        auction.setAuctionId(response.getId());

        if (response.getBids() != null && !response.getBids().isEmpty()) {
            List<Bid> domainBids = new ArrayList<>();
            for (BidResponse bidDto : response.getBids()) {
                User bidder = null; // ĐỒNG BỘ: Sửa sang User phẳng
                if (bidDto.getBidderId() > 0) {
                    String[] bidderNameParts = splitName(bidDto.getBidderName());
                    // ĐỒNG BỘ: Truyền đầy đủ chính xác 10 tham số
                    bidder = new User(
                            bidderNameParts[0],
                            bidderNameParts[1],
                            "bidder-" + bidDto.getBidderId() + "@server.local",
                            "0000000000",
                            SESSION_PASSWORD_PLACEHOLDER,
                            0,
                            false, // isAdmin
                            false, // isBlocked
                            null,  // blockedUntil
                            null   // avatarPath
                    );
                    bidder.setId(bidDto.getBidderId());
                }

                domainBids.add(new Bid(
                        auction,
                        bidder,
                        bidDto.getBidPrice(),
                        bidDto.getBidTime()
                ));
            }
            auction.setBids(domainBids);
        } else {
            auction.setBids(new ArrayList<>());
        }

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

    private static Auction.AuctionStatus parseAuctionStatus(String statusStr) {
        try {
            if (statusStr != null) {
                return Auction.AuctionStatus.valueOf(statusStr.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            return Auction.AuctionStatus.OPEN;
        }
        return Auction.AuctionStatus.OPEN;
    }
}
