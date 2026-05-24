package services;

import com.group7.dto.auth.*;
import models.Common.Email;
import models.Common.PhoneNumber;
import models.User;
import org.springframework.stereotype.Service;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

@Service
public class AuthService {

    private final UsersDAO userdb = DaoFactory.createUsersDAO();

    public AuthResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        User existing = userdb.getByEmail(request.getEmail());
        if (existing != null) {
            throw new IllegalArgumentException("[Failure]: An account with this email already exists.");
        }

        double initialBalance = 0.0;
        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getPassword(),
                initialBalance,
                request.getAvatarPath()
        );

        userdb.save(user);

        return toAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("[Failure]: Request body is required.");
        }

        if (isBlank(request.getEmail()) || isBlank(request.getPassword())) {
            throw new IllegalArgumentException("[Failure]: Email and password are required.");
        }

        User user = userdb.getByEmail(request.getEmail());

        if (user == null) {
            throw new IllegalArgumentException("[Failure]: No account found with this email.");
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("[Failure]: Incorrect password.");
        }

        return toAuthResponse(user);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("[Failure]: Request body is required.");
        }

        if (isBlank(request.getFirstName())) {
            throw new IllegalArgumentException("[Failure]: First name is required.");
        }

        if (isBlank(request.getLastName())) {
            throw new IllegalArgumentException("[Failure]: Last name is required.");
        }

        if (isBlank(request.getEmail())) {
            throw new IllegalArgumentException("[Failure]: Email is required.");
        }

        if (!Email.isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("[Error]: Invalid email format");
        }

        if (isBlank(request.getPhoneNumber())) {
            throw new IllegalArgumentException("[Failure]: Phone number is required.");
        }

        if (!PhoneNumber.isValidPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("[Error]: Invalid contact number format");
        }

        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException("[Failure]: Password is required.");
        }

        if (request.getPassword().length() < 6) {
            throw new IllegalArgumentException("[Error]: Password must have more than 6 digits");
        }
    }

    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getBalance(),
                user.getAvatarPath()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}