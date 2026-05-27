package services;

import models.Admin;
import models.User;
import com.group7.dto.user.UserResponse;

public final class UserMapper {
    private static final String SESSION_PASSWORD_PLACEHOLDER = "server-authenticated";

    private UserMapper() {
    }

    public static User toUser(UserResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("User response is required.");
        }

        String[] nameParts = splitName(response.getFullName());

        boolean isAdminUser = "true".equalsIgnoreCase(response.getRole())
                || "Admin".equalsIgnoreCase(response.getRole());

        java.time.LocalDateTime blockedUntilTime = response.isBlocked()
                ? java.time.LocalDateTime.now().plusDays(100)
                : null;

        User user;

        if (isAdminUser) {
            user = new Admin(
                    nameParts[0],
                    nameParts[1],
                    response.getEmail(),
                    response.getPhoneNumber(),
                    SESSION_PASSWORD_PLACEHOLDER,
                    response.getBalance(),
                    response.getAvatarPath()
            );
        } else {
            user = new User(
                    nameParts[0],
                    nameParts[1],
                    response.getEmail(),
                    response.getPhoneNumber(),
                    SESSION_PASSWORD_PLACEHOLDER,
                    response.getBalance(),
                    isAdminUser,
                    response.isBlocked(),
                    blockedUntilTime,
                    response.getAvatarPath()
            );
        }

        user.setId(response.getId());
        user.setFrozenBalance(response.getFrozenBalance());

        return user;
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
