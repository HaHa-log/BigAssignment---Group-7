package Branch;

import Branch.Exceptions.*;
import java.time.LocalDateTime;

public interface Seller {
    default void createAuction(Item item, LocalDateTime createdAt, LocalDateTime terminatedAt) {
        if (this instanceof Member owner) {
            if (owner.isBlocked()) {
                throw new AuthenticationException("Your account is currently blocked and cannot create auctions.");
            }

            if (terminatedAt == null || createdAt == null) {
                throw new CustomisedException("Scheduled auctions must have both starting and ending time");
            }

            if (terminatedAt.isBefore(LocalDateTime.now())) {
                throw new CustomisedException("The auction termination time must be in the future.");
            }

            if (terminatedAt.isBefore(createdAt)) {
                throw new CustomisedException("Ending time cannot be before starting time.");
            }

            try {
                AuctionManager.getInstance().createAuction(owner, item, createdAt, terminatedAt);
                System.out.println("[System]: Auction created successfully for item: " + item.getName());
            } catch (Exception e) {
                throw new CustomisedException("System error: " + e.getMessage());            }
        }   }
}