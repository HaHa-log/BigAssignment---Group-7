package services;

import dto.user.UserResponse;
import models.User;
import org.springframework.stereotype.Service;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UsersDAO usersDAO = DaoFactory.createUsersDAO();

    public List<UserResponse> getAll() {
        return usersDAO.getAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse getById(int id) {
        return toResponse(requireUser(id));
    }

    public UserResponse block(int id) {
        User user = requireUser(id);
        user.setBlocked(LocalDateTime.now().plusDays(100));
        usersDAO.update(user);
        return toResponse(user);
    }

    public UserResponse unblock(int id) {
        User user = requireUser(id);
        user.isUnblocked();
        usersDAO.update(user);
        return toResponse(user);
    }

    private User requireUser(int id) {
        User user = usersDAO.getById(id);
        if (user == null) {
            throw new IllegalArgumentException("[Error]: User not found.");
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
                user.getAvatarPath()
        );
    }
}
