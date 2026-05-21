package services;

import models.Admin;
import models.Member;
import models.User;
import com.group7.dto.auth.*;

public final class AuthSessionMapper {
    private static final String SESSION_PASSWORD_PLACEHOLDER = "server-authenticated";

    private AuthSessionMapper() {
    }

    public static User toUser(AuthResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("Auth response is required.");
        }

        User user;
        if ("Admin".equalsIgnoreCase(response.getRole())) {
            user = new Admin(
                    response.getFirstName(),
                    response.getLastName(),
                    response.getEmail(),
                    response.getPhoneNumber(),
                    SESSION_PASSWORD_PLACEHOLDER,
                    response.getBalance(),
                    response.getAvatarPath()
            );
        } else {
            user = new Member(
                    response.getFirstName(),
                    response.getLastName(),
                    response.getEmail(),
                    response.getPhoneNumber(),
                    SESSION_PASSWORD_PLACEHOLDER,
                    response.getBalance(),
                    response.getAvatarPath()
            );
        }

        user.setId(response.getUserId());
        return user;
    }
}
