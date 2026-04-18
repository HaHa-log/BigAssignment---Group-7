package Branch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TempDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/auction_system";
    private static final String USER = "root";
    private static final String PASSWORD = "Auction@Group7";

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static int executeInsert(PreparedStatement stmt) throws SQLException {
        stmt.executeUpdate();
        ResultSet keys = stmt.getGeneratedKeys();
        if (keys.next()) {
            return keys.getInt(1);
        }
        return -1;
    }

    public static void saveUser(User user) {
        String sql = "INSERT INTO users (email, phoneNumber, firstName, lastName, password, isAdmin, isBlocked, balance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPhoneNumber());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getPassword());
            stmt.setBoolean(6, user.isAdmin());
            stmt.setBoolean(7, user.isBlocked());
            stmt.setDouble(8, user.getBalance());

            int generatedId = executeInsert(stmt);
            if (generatedId != -1) {
                user.setId(generatedId);
            }

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

    public static void saveItem(Item item) {
        String sql = "INSERT INTO products (name, startingPrice, description) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getStartingPrice());
            stmt.setString(3, item.getDescription());

            int generatedId = executeInsert(stmt);
            if (generatedId != -1) {
                item.setId(generatedId);
            }

            System.out.println("[System]: User " + item.getName() + " saved to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getItemId(Item item) {
        String sql = "SELECT products_id FROM products WHERE name = ? AND description = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getDescription());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("products_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Cho transaction (đã có database)
    public static void saveTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (auction_id, buyer_id, seller_id, finalAmount, status, paidAt, completedAt) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, transaction.getAuction().getAuctionId());
            stmt.setInt(2, transaction.getBuyer().getId());
            stmt.setInt(3, transaction.getSeller().getId());
            stmt.setDouble(4, transaction.getFinalAmount());
            stmt.setString(5, transaction.getStatus().name());
            stmt.setTimestamp(6, Timestamp.valueOf(transaction.getPaidAt()));
            stmt.setTimestamp(7, transaction.getCompletedAt() != null
                    ? Timestamp.valueOf(transaction.getCompletedAt()) : null);

            int generatedId = executeInsert(stmt);
            if (generatedId != -1) {
                transaction.setTransactionId(generatedId);
            }

            System.out.println("[System]: Transaction saved.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Transaction> getAuctionTransactions() {
        List<Transaction> result = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User buyer = getUserById(rs.getInt("buyer_id"));
                User seller = getUserById(rs.getInt("seller_id"));

                System.out.println("[Warning]: Auction object not restored from DB yet.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}