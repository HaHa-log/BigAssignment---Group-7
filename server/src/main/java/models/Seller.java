package models;

import models.Exceptions.AuthenticationException;
import models.Exceptions.CustomisedException;
import java.time.LocalDateTime;

public interface Seller {
    default void createAuction(Item item, LocalDateTime createdAt, LocalDateTime terminatedAt) {
        if (this instanceof User owner) {
            if (owner.isBlocked()) {
                throw new AuthenticationException("Your account is currently blocked and cannot create auctions.");
            }

            if (terminatedAt == null || createdAt == null) {
                throw new CustomisedException("Scheduled auctions must have both starting and ending time");
            }

            if (terminatedAt.isBefore(LocalDateTime.now())) {
                throw new CustomisedException("The auction termination time must be in the future.");
            }

            if (!terminatedAt.isAfter(createdAt)) {
                throw new CustomisedException("Ending time must be after starting time.");
            }

            try {
                AuctionManager.getInstance().createAuction(owner, item, createdAt, terminatedAt);
                System.out.println("[System]: Auction created successfully for item: " + item.getName());
            } catch (Exception e) {
                throw new CustomisedException("System error: " + e.getMessage());
            }
        }
    }
}