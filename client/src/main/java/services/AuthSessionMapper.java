package services;

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

        // Kiểm tra phân quyền dựa vào chuỗi role trả về từ DTO JSON của Server
        boolean isAdminUser = "true".equalsIgnoreCase(response.getRole())
                || "Admin".equalsIgnoreCase(response.getRole());

        User user = new User(
                response.getFirstName(),
                response.getLastName(),
                response.getEmail(),
                response.getPhoneNumber(),
                SESSION_PASSWORD_PLACEHOLDER,
                response.getBalance(),
                isAdminUser,              // Tham số isAdmin gán trực tiếp
                false,                    // Tham số isBlocked mặc định false khi đăng nhập
                null,                     // Tham số blockedUntil mặc định null
                response.getAvatarPath()
        );

        user.setId(response.getUserId());
        return user;
    }
}
