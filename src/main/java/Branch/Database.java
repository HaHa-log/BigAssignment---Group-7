package Branch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/auction_system";
    private static final String USER = "root";
    private static final String PASSWORD = "Auction@Group7";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    //Users
    public static void saveUser(User user) {
        String sql = "INSERT INTO users (email, phoneNumber, firstName, lastName, password, isAdmin, isBlocked, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPhoneNumber());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getPassword());
            stmt.setBoolean(6, user.isAdmin());
            stmt.setBoolean(7, user.isBlocked());
            stmt.setDouble(8, user.getBalance());

            stmt.executeUpdate();

            System.out.println("[System]: User " + user.getFirstName() + " saved to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Member(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("phoneNumber"),
                        rs.getString("password"),
                        rs.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE users_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Member member = new Member(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("phoneNumber"),
                        rs.getString("password"),
                        rs.getDouble("balance")
                );
                member.setId(rs.getInt("users_id"));
                return member;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new Member(
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("phoneNumber"),
                        rs.getString("password"),
                        rs.getDouble("balance")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    //Items
    public static void saveItem(Item item) {
        String sql = "INSERT INTO products (name, startingPrice, description) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getStartingPrice());
            stmt.setString(3, item.getDescription());

            stmt.executeUpdate();

            System.out.println("[System]: User " + item.getName() + " saved to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getUserId(User user) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getEmail());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("users_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer getItemId(Item item) {
        String sql = "SELECT * FROM users WHERE name = ? and description = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getDescription());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("users_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Hiện chưa có database cho transaction nhé
    private static List<Transaction> transactionDatabase = new ArrayList<>();

    public static void saveTransaction(Transaction transaction) {
        transactionDatabase.add(transaction);
        System.out.println("[System]: Transaction saved.");
    }

    public static List<Transaction> getAuctionTransactions() {
        return transactionDatabase;
    }
}