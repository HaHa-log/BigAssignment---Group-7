package services.dto.auth;

public class LoginRequest {
    private String email;
    private String password;

    public LoginRequest(String emailInput, String passwordInput) {
        this.email = emailInput;
        this.password = passwordInput;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}