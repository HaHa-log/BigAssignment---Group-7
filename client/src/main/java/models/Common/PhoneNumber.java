package models.Common;

public class PhoneNumber {
    private final String phoneNumber;

    public static boolean isValidPhoneNumber(String phone) {
        String regex = "^0\\d{9}$";
        return phone != null && phone.matches(regex);
    }

    public PhoneNumber(String contact) {
        if (!isValidPhoneNumber(contact)) {
            throw new IllegalArgumentException("[Error]: Invalid contact number format");
        }
        this.phoneNumber = contact;
    }

    @Override
    public String toString() {
        return phoneNumber;
    }
}
