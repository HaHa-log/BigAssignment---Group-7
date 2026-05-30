package services;

import com.group7.dto.user.*;
import models.Auction;
import models.User;
import org.springframework.stereotype.Service;
import repositories.AuctionsDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UsersDAO usersDAO = DaoFactory.createUsersDAO();
    private final AuctionsDAO auctionsDAO = DaoFactory.createAuctionsDAO();

    public List<UserResponse> getAll() {
        return usersDAO.getAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getById(int id) {
        return toResponse(requireUser(id));
    }

    public UserResponse getByEmail(String email) {
        return toResponse(requireUser(email));
    }

    public UserResponse updateAvatar(int id, String filename) {
        User user = requireUser(id);
        user.setAvatarPath(filename);
        usersDAO.update(user);
        log.info("[User System]: Updated avatar for user ID: {}, file: {}", id, filename);
        return toResponse(user);
    }

    public UserResponse changePassword(int id, ChangePasswordRequest request) {
        if (request == null || request.getOldPassword() == null || request.getNewPassword() == null) {
            throw new IllegalArgumentException("Old password and new password are required.");
        }

        User user = requireUser(id);
        if (!user.getPassword().equals(request.getOldPassword())) {
            log.warn("[Security Alert]: Failed password change attempt for user ID: {}", id);
            throw new IllegalArgumentException("Old password failed.");
        }

        user.setPassword(request.getNewPassword());
        usersDAO.update(user);
        log.info("[Security]: User ID {} successfully changed password.", id);
        return toResponse(user);
    }

    public UserResponse block(int id) {
        User user = requireUser(id);
        LocalDateTime blockUntil = LocalDateTime.now().plusDays(100);
        user.setBlocked(blockUntil);
        usersDAO.update(user);

        log.info("[Admin Action]: Blocked user ID: {} ({}) until {}", user.getId(), user.getEmail(), blockUntil);

        return toResponse(user);
    }

    public UserResponse unblock(int id) {
        User user = requireUser(id);
        user.isUnblocked();
        usersDAO.update(user);

        log.info("[Admin Action]: Unblocked user ID: {} ({}) successfully.", user.getId(), user.getEmail());

        return toResponse(user);
    }

    public UserResponse deposit(int id, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        User user = requireUser(id);
        user.depositMoney(amount);
        usersDAO.update(user);
        log.info("[Finance]: User ID {} deposited +{} USD. New balance: {}", id, amount, user.getBalance());
        return toResponse(user);
    }

    public UserResponse withdraw(int id, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        User user = requireUser(id);
        boolean success = user.withdrawMoney(amount);
        if (!success) {
            log.warn("[Finance Error]: User ID {} failed to withdraw {} USD due to insufficient funds.", id, amount);
            throw new IllegalArgumentException("Insufficient balance.");
        }
        usersDAO.update(user);
        log.info("[Finance]: User ID {} withdrew -{} USD. Remaining balance: {}", id, amount, user.getBalance());
        return toResponse(user);
    }

    public UserResponse freeze(int id, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        User user = requireUser(id);
        boolean success = user.freezeMoney(amount);
        if (!success) throw new IllegalArgumentException("Insufficient balance.");
        usersDAO.update(user);
        log.info("[Finance]: Frozen {} USD for User ID: {}. Current frozen: {}", amount, id, user.getFrozenBalance());
        return toResponse(user);
    }

    public UserResponse unfreeze(int id, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        User user = requireUser(id);
        boolean success = user.unfreezeMoney(amount);
        if (!success) throw new IllegalArgumentException("Insufficient frozen balance.");
        usersDAO.update(user);
        log.info("[Finance]: Unfrozen {} USD for User ID: {}. Current frozen: {}", amount, id, user.getFrozenBalance());
        return toResponse(user);
    }

    public UserResponse spendFrozen(int id, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        User user = requireUser(id);
        boolean success = user.spendFrozenMoney(amount);
        if (!success) throw new IllegalArgumentException("Insufficient frozen balance.");
        usersDAO.update(user);
        log.info("[Finance]: Deducted frozen money: -{} USD from User ID: {} for payment.", amount, id);
        return toResponse(user);
    }

    private User requireUser(int id) {
        User user = usersDAO.getById(id);
        if (user == null) {
            log.warn("[System Query]: User lookup failed for ID: {}", id);
            throw new IllegalArgumentException("[Error]: User not found.");
        }
        return user;
    }

    private User requireUser(String email) {
        User user = usersDAO.getByEmail(email);
        if (user == null) {
            log.warn("[System Query]: User lookup failed for Email: {}", email);
            throw new IllegalArgumentException("[Error]: User not found with email: " + email);
        }
        return user;
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.isBlocked(),
                user.getBalance(),
                user.getAvatarPath(),
                user.getFrozenBalance()
        );
    }

    public List<NotificationResponse> getNotifications(int id) {
        User user = requireUser(id);
        List<Auction> allAuctions = auctionsDAO.getAllByUserId(id);

        return user.getNotifications(allAuctions).stream()
                .map(alert -> new NotificationResponse(
                        alert.type().name(),
                        alert.itemName(),
                        alert.currentPrice()
                ))
                .toList();
    }

    public List<HistoryEntryResponse> getAuctionHistory(int id) {
        User user = requireUser(id);
        List<Auction> allAuctions = auctionsDAO.getAllByUserId(id);
        return user.getTableHistory(allAuctions).stream()
                .map(entry -> new HistoryEntryResponse(
                        entry.auctionId(),
                        entry.itemName(),
                        entry.auctionStatus(),
                        entry.userState()
                ))
                .toList();
    }
}