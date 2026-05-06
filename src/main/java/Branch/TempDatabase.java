package Branch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;


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
        String sql = "INSERT INTO products (name, startingPrice, description, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getStartingPrice());
            stmt.setString(3, item.getDescription());
            stmt.setString(4, item.getStatus().name());

            int generatedId = executeInsert(stmt);
            if (generatedId != -1) {
                item.setId(generatedId);
            }

            System.out.println("[System]: User " + item.getName() + " saved to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateItemStatus(int itemId, Item.Status status) {
        String sql = "UPDATE products SET status = ? WHERE products_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            stmt.setInt(2, itemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getItemId(Item item) {
        String sql = "SELECT products_id FROM products WHERE name = ? AND description = ? AND status = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getDescription());
            stmt.setString(3, item.getStatus().name());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("products_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Item getItemById(int id) {
        String sql = "SELECT * FROM products WHERE products_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Item item = new Item(
                        rs.getString("name"),
                        rs.getFloat("startingPrice"),
                        rs.getString("description")
                );
                item.setId(rs.getInt("products_id"));

                String statusFromDB = rs.getString("status");
                if (statusFromDB != null) {
                    item.setStatus(Item.Status.valueOf(statusFromDB));
                }

                return item;
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

            stmt.setInt(1, transaction.getAuction().getId());
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

    //Đối với Auction database
    public static void saveAuction(Auction auction) {
        String sql = "INSERT INTO auctions (owner_id, products_id, startingPrice, currentPrice, status, createdAt) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, auction.getOwner().getId());
            stmt.setInt(2, auction.getItem().getId());
            stmt.setDouble(3, auction.getStartingPrice());
            stmt.setDouble(4, auction.getCurrentPrice());
            stmt.setString(5, auction.getStatus().name());
            stmt.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

            int generatedId = executeInsert(stmt);
            if (generatedId != -1) {
                auction.setAuctionId(generatedId);
            }

            System.out.println("[System]: Auction saved to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateAuction(Auction auction) {
        String sql = "UPDATE auctions SET currentPrice=?, status=?, terminatedAt=?, winner_id=? WHERE auction_id=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, auction.getCurrentPrice());
            stmt.setString(2, auction.getStatus().name());
            stmt.setTimestamp(3, auction.getEndingTime() != null
                    ? Timestamp.valueOf(auction.getEndingTime()) : null);
            stmt.setObject(4, auction.getWinner() != null
                    ? ((User) auction.getWinner()).getId() : null);
            stmt.setInt(5, auction.getId());

            stmt.executeUpdate();
            System.out.println("[System]: Auction updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Auction> getActiveAuctions() {
        List<Auction> result = new ArrayList<>();
        String sql = "SELECT * FROM auctions WHERE status = 'RUNNING' OR status = 'OPEN'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User owner = getUserById(rs.getInt("owner_id"));
                Item item = getItemById(rs.getInt("products_id"));

                if (owner instanceof Member && item != null) {
                    Auction auction = new Auction((Member) owner, item);
                    auction.setAuctionId(rs.getInt("auction_id"));
                    auction.setStartingPrice(rs.getDouble("currentPrice"));
                    result.add(auction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}