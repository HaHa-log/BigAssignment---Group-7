package services;

import com.group7.dto.user.*;
import models.Auction;
import models.Member;
import org.springframework.stereotype.Service;
import repositories.AuctionsDAO;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
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

    public UserResponse block(int id) {
        Member member = requireUser(id);
        member.setBlocked(LocalDateTime.now().plusDays(100));
        usersDAO.update(member);
        return toResponse(member);
    }

    public UserResponse unblock(int id) {
        Member member = requireUser(id);
        member.isUnblocked();
        usersDAO.update(member);
        return toResponse(member);
    }

    public UserResponse deposit(int id, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        Member member = requireUser(id);
        member.depositMoney(amount);
        usersDAO.update(member);
        return toResponse(member);
    }

    public UserResponse withdraw(int id, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        Member member = requireUser(id);
        boolean success = member.withdrawMoney(amount);
        if (!success) throw new IllegalArgumentException("Insufficient balance.");
        usersDAO.update(member);
        return toResponse(member);
    }

    public UserResponse freeze(int id, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        Member member = requireUser(id);
        boolean success = member.freezeMoney(amount);
        if (!success) throw new IllegalArgumentException("Insufficient balance.");
        usersDAO.update(member);
        return toResponse(member);
    }

    public UserResponse unfreeze(int id, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        Member member = requireUser(id);
        boolean success = member.unfreezeMoney(amount);
        if (!success) throw new IllegalArgumentException("Insufficient frozen balance.");
        usersDAO.update(member);
        return toResponse(member);
    }

    public UserResponse spendFrozen(int id, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        Member member = requireUser(id);
        boolean success = member.spendFrozenMoney(amount);
        if (!success) throw new IllegalArgumentException("Insufficient frozen balance.");
        usersDAO.update(member);
        return toResponse(member);
    }

    private Member requireUser(int id) {
        Member member = usersDAO.getById(id);
        if (member == null) {
            throw new IllegalArgumentException("[Error]: User not found.");
        }
        return member;
    }

    private Member requireUser(String email) {
        Member member = usersDAO.getByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("[Error]: User not found with email: " + email);
        }
        return member;
    }

    private UserResponse toResponse(Member member) {
        return new UserResponse(
                member.getId(),
                member.getFullName(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getRole(),
                member.isBlocked(),
                member.getBalance(),
                member.getAvatarPath(),
                member.getFrozenBalance()
        );
    }

    public List<NotificationResponse> getNotifications(int id) {
        Member member = requireUser(id);
        List<Auction> allAuctions = auctionsDAO.getAll();

        return member.getNotifications(allAuctions).stream()
                .map(alert -> new NotificationResponse(
                        alert.type().name(),
                        alert.itemName(),
                        alert.currentPrice()
                ))
                .toList();
    }

    public List<HistoryEntryResponse> getAuctionHistory(int id) {
        Member member = requireUser(id);
        List<Auction> allAuctions = auctionsDAO.getAll();
        return member.getTableHistory(allAuctions).stream()
                .map(entry -> new HistoryEntryResponse(
                        entry.auctionId(),
                        entry.itemName(),
                        entry.auctionStatus(),
                        entry.userState()
                ))
                .toList();
    }
}