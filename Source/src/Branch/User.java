package Branch;

public abstract class User extends Entity {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;

    public User(int id, String firstName, String lastName, String email, String phoneNumber, String password) {
        super(id, firstName + " " + lastName);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public void setFirstName(String fstName) {
        this.firstName = fstName;
    }

    public void setLastName(String lstName) {
        this.lastName = lstName;
    }

    public void setEmail(String userEmail) {
        this.email = userEmail;
    }

    public void setPhoneNumber(String contactNumber) {
        this.phoneNumber = contactNumber;
    }

    public void setPassword(String pass) {
        this.password = pass;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

}
