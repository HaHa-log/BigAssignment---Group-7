public abstract class User extends Entity {
    protected String email;
    protected String phoneNumber;

    public User(String id, String name, String email, String phoneNumber) {
        super(id, name);
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
