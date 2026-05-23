package services;

import com.group7.dto.auth.*;
import models.Common.Email;
import models.Common.PhoneNumber;
import models.Member;
import org.springframework.stereotype.Service;
import repositories.UsersDAO;
import repositories.impl.DaoFactory;

@Service
public class AuthService {

    private final UsersDAO userdb = DaoFactory.createUsersDAO();

    public AuthResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        Member existing = userdb.getByEmail(request.getEmail());
        if (existing != null) {
            throw new IllegalArgumentException("[Failure]: An account with this email already exists.");
        }

        double initialBalance = 0.0;
        Member member = new Member(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getPassword(),
                initialBalance,
                request.getAvatarPath()
        );

        userdb.save(member);

        return toAuthResponse(member);
    }

    public AuthResponse login(LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("[Failure]: Request body is required.");
        }

        if (isBlank(request.getEmail()) || isBlank(request.getPassword())) {
            throw new IllegalArgumentException("[Failure]: Email and password are required.");
        }

        Member member = userdb.getByEmail(request.getEmail());

        if (member == null) {
            throw new IllegalArgumentException("[Failure]: No account found with this email.");
        }

        if (!member.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("[Failure]: Incorrect password.");
        }

        return toAuthResponse(member);
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

    private AuthResponse toAuthResponse(Member member) {
        return new AuthResponse(
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getFullName(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getRole(),
                member.getBalance(),
                member.getAvatarPath()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}