package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import config.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BidStepConfiguration {
    private static final Logger log = LoggerFactory.getLogger(BidStepConfiguration.class);

    public static List<Double> getAllowedSteps(double currentPrice) {
        List<Double> allowedSteps = new ArrayList<>();
        String sql = "SELECT allowed_steps FROM system_bid_steps WHERE ? >= min_price AND ? <= max_price";

        try (Connection conn = DB.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setDouble(1, currentPrice);
            st.setDouble(2, currentPrice);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    String stepsRaw = rs.getString("allowed_steps");
                    String[] tokens = stepsRaw.split(",");
                    for (String token : tokens) {
                        allowedSteps.add(Double.parseDouble(token.trim()));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Failed to retrieve bid step configuration: {}", e.getMessage(), e);
            allowedSteps.add(10.0);
        }
        return allowedSteps;
    }

    public static boolean isValidStep(double currentPrice, double userStep) {
        List<Double> allowed = getAllowedSteps(currentPrice);
        return allowed.contains(userStep);
    }
}