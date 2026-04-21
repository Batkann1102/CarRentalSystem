package mn.edu.num.carrental.shared.constants;

public final class ApiConstants {

    public static final String APPLICATION_JSON = "application/json;charset=UTF-8";
    public static final String AVAILABLE_CARS_ENDPOINT = "/api/cars/available";
    public static final String RENTAL_ENDPOINT = "/api/rentals";
    public static final String HEALTH_ENDPOINT = "/api/health";
    public static final String ALLOWED_METHODS = "GET,POST,OPTIONS";
    public static final String ALLOWED_HEADERS = "Content-Type,Authorization";

    private ApiConstants() {
    }
}

