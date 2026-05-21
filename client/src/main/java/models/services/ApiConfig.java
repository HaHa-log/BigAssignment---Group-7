package models.services;

public final class ApiConfig {
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private ApiConfig() {
    }

    public static String baseUrl() {
        String configured = System.getProperty("server.url", DEFAULT_BASE_URL);
        if (configured.endsWith("/")) {
            return configured.substring(0, configured.length() - 1);
        }
        return configured;
    }
}
