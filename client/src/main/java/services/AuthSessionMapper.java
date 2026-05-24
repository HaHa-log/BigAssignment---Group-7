package services;

import models.Admin;
import models.Member;
import com.group7.dto.auth.*;

public final class AuthSessionMapper {
    private static final String SESSION_PASSWORD_PLACEHOLDER = "server-authenticated";

    private AuthSessionMapper() {
    }

    public static Member toMember(AuthResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("Auth response is required.");
        }

        boolean isAdminUser = "true".equalsIgnoreCase(response.getRole())
                || "Admin".equalsIgnoreCase(response.getRole());

        Member member = isAdminUser ?
                new Admin(
                        response.getFirstName(),
                        response.getLastName(),
                        response.getEmail(),
                        response.getPhoneNumber(),
                        SESSION_PASSWORD_PLACEHOLDER,
                        response.getBalance(),
                        response.getAvatarPath()
                ) :
                new Member(
                        response.getFirstName(),
                        response.getLastName(),
                        response.getEmail(),
                        response.getPhoneNumber(),
                        SESSION_PASSWORD_PLACEHOLDER,
                        response.getBalance(),
                        response.getAvatarPath()
                );

        member.setId(response.getUserId());
        return member;
    }
}