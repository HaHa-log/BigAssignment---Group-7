package models;

import java.util.ArrayList;
import java.util.List;

public class BidStepConfiguration {
    public static List<Double> getAllowedSteps(double currentPrice) {
        List<Double> allowedSteps = new ArrayList<>();

        if (currentPrice < 100) {
            allowedSteps.add(10.0);
            allowedSteps.add(20.0);
            allowedSteps.add(50.0);
        } else if (currentPrice < 1_000) {
            allowedSteps.add(50.0);
            allowedSteps.add(100.0);
            allowedSteps.add(200.0);
        } else {
            allowedSteps.add(100.0);
            allowedSteps.add(500.0);
            allowedSteps.add(1_000.0);
        }

        if (allowedSteps.isEmpty()) {
            allowedSteps.add(10.0);
        }
        return allowedSteps;
    }

    public static boolean isValidStep(double currentPrice, double userStep) {
        List<Double> allowed = getAllowedSteps(currentPrice);
        return allowed.contains(userStep);
    }
}