package services;

import models.Admin;
import models.Member;

import com.group7.dto.user.UserResponse;

public final class UserMapper {
    private static final String SESSION_PASSWORD_PLACEHOLDER = "server-authenticated";

    private UserMapper() {
    }

    public static Member toMember(UserResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("User response is required.");
        }

        String[] nameParts = splitName(response.getFullName());

        // Catch stringified booleans ("true") or direct strings ("Admin") securely
        boolean isAdminUser = "true".equalsIgnoreCase(response.getRole())
                || "Admin".equalsIgnoreCase(response.getRole());

        Member member = isAdminUser ?
                new Admin(
                        nameParts[0],
                        nameParts[1],
                        response.getEmail(),
                        response.getPhoneNumber(),
                        SESSION_PASSWORD_PLACEHOLDER,
                        response.getBalance(),
                        response.getAvatarPath()
                ) :
                new Member(
                        nameParts[0],
                        nameParts[1],
                        response.getEmail(),
                        response.getPhoneNumber(),
                        SESSION_PASSWORD_PLACEHOLDER,
                        response.getBalance(),
                        response.getAvatarPath()
                );

        member.setId(response.getId());
        member.setFrozenBalance(response.getFrozenBalance());

        if (response.isBlocked()) {
            member.setBlocked(java.time.LocalDateTime.now().plusDays(100));
        }
        return member;
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