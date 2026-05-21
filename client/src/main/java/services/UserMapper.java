package services;

import models.Admin;
import models.Member;
import models.User;
import com.group7.dto.user.UserResponse;

public final class UserMapper {
    private static final String SESSION_PASSWORD_PLACEHOLDER = "server-authenticated";

    private UserMapper() {
    }

    public static User toUser(UserResponse response) {
        String[] nameParts = splitName(response.getFullName());
        User user;
        if ("Admin".equalsIgnoreCase(response.getRole())) {
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
            user = new Member(
                    nameParts[0],
                    nameParts[1],
                    response.getEmail(),
                    response.getPhoneNumber(),
                    SESSION_PASSWORD_PLACEHOLDER,
                    response.getBalance(),
                    response.getAvatarPath()
            );
        }

        user.setId(response.getId());

        user.setFrozenBalance(response.getFrozenBalance());

        if (response.isBlocked()) {
            user.setBlocked(java.time.LocalDateTime.now().plusDays(100));
        }
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
