package mn.edu.num.carrental.adapters.outbound.persistence;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;
import mn.edu.num.carrental.adapters.outbound.persistence.entity.CarEntity;
import mn.edu.num.carrental.adapters.outbound.persistence.mapper.PersistenceMapper;
import mn.edu.num.carrental.core.domain.Car;
import mn.edu.num.carrental.core.ports.out.ICarRepositoryPort;
import mn.edu.num.carrental.infrastructure.database.ConnectionPoolManager;

@Component
public class PostgresCarAdapter implements ICarRepositoryPort {

    private static final String SELECT_ALL_SQL = """
            SELECT id, brand, model, plate_number, rental_type, price_per_unit, available
            FROM cars
            ORDER BY brand, model
            """;

    private static final String SELECT_AVAILABLE_SQL = """
            SELECT id, brand, model, plate_number, rental_type, price_per_unit, available
            FROM cars
            WHERE available = TRUE
            ORDER BY brand, model
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT id, brand, model, plate_number, rental_type, price_per_unit, available
            FROM cars
            WHERE id = ?
            """;

    private static final String UPSERT_SQL = """
            INSERT INTO cars (id, brand, model, plate_number, rental_type, price_per_unit, available)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                brand = EXCLUDED.brand,
                model = EXCLUDED.model,
                plate_number = EXCLUDED.plate_number,
                rental_type = EXCLUDED.rental_type,
                price_per_unit = EXCLUDED.price_per_unit,
                available = EXCLUDED.available
            """;

    private static final String DELETE_SQL = """
            DELETE FROM cars
            WHERE id = ?
            """;

    private final Map<String, CarEntity> fallbackStore = new LinkedHashMap<>();

    @Autowired
    private PersistenceMapper persistenceMapper;

    @Autowired
    private ConnectionPoolManager connectionPoolManager;

    public PostgresCarAdapter() {
        seedFallbackCars();
    }

    @Override
    public List<Car> findAll() {
        if (!connectionPoolManager.isDatabaseEnabled()) {
            return getFallbackCars();
        }

        Optional<Connection> connectionOptional = connectionPoolManager.getConnection();
        if (connectionOptional.isEmpty()) {
            return getFallbackCars();
        }

        try (Connection connection = connectionOptional.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(persistenceMapper.toDomain(mapCar(resultSet)));
            }
            return cars;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not load cars from PostgreSQL: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Car> findAvailableCars() {
        if (!connectionPoolManager.isDatabaseEnabled()) {
            return getFallbackAvailableCars();
        }

        Optional<Connection> connectionOptional = connectionPoolManager.getConnection();
        if (connectionOptional.isEmpty()) {
            return getFallbackAvailableCars();
        }

        try (Connection connection = connectionOptional.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_AVAILABLE_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Car> cars = new ArrayList<>();
            while (resultSet.next()) {
                cars.add(persistenceMapper.toDomain(mapCar(resultSet)));
            }
            return cars;
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not load cars from PostgreSQL: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Car> findById(String carId) {
        if (!connectionPoolManager.isDatabaseEnabled()) {
            return findFallbackById(carId);
        }

        Optional<Connection> connectionOptional = connectionPoolManager.getConnection();
        if (connectionOptional.isEmpty()) {
            return findFallbackById(carId);
        }

        try (Connection connection = connectionOptional.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            preparedStatement.setString(1, carId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(persistenceMapper.toDomain(mapCar(resultSet)));
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not load the car from PostgreSQL: " + ex.getMessage(), ex);
        }

        return Optional.empty();
    }

    @Override
    public void save(Car car) {
        if (!connectionPoolManager.isDatabaseEnabled()) {
            fallbackStore.put(car.getId(), persistenceMapper.toEntity(car));
            return;
        }

        Optional<Connection> connectionOptional = connectionPoolManager.getConnection();
        if (connectionOptional.isEmpty()) {
            fallbackStore.put(car.getId(), persistenceMapper.toEntity(car));
            return;
        }

        try (Connection connection = connectionOptional.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPSERT_SQL)) {
            preparedStatement.setString(1, car.getId());
            preparedStatement.setString(2, car.getBrand());
            preparedStatement.setString(3, car.getModel());
            preparedStatement.setString(4, car.getPlateNumber());
            preparedStatement.setString(5, car.getRentalType().name());
            preparedStatement.setBigDecimal(6, car.getPricePerUnit());
            preparedStatement.setBoolean(7, car.isAvailable());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not persist the car to PostgreSQL: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deleteById(String carId) {
        if (!connectionPoolManager.isDatabaseEnabled()) {
            fallbackStore.remove(carId);
            return;
        }

        Optional<Connection> connectionOptional = connectionPoolManager.getConnection();
        if (connectionOptional.isEmpty()) {
            fallbackStore.remove(carId);
            return;
        }

        try (Connection connection = connectionOptional.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setString(1, carId);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not delete the car from PostgreSQL: " + ex.getMessage(), ex);
        }
    }

    private void seedFallbackCars() {
        fallbackStore.put("CAR-001", new CarEntity("CAR-001", "Toyota", "Prius", "UBA-1024",
                "DAILY", new BigDecimal("120000"), true));
        fallbackStore.put("CAR-002", new CarEntity("CAR-002", "Hyundai", "Sonata", "UBA-2048",
                "DAILY", new BigDecimal("180000"), true));
        fallbackStore.put("CAR-003", new CarEntity("CAR-003", "Tesla", "Model 3", "UBA-4096",
                "HOURLY", new BigDecimal("35000"), true));
        fallbackStore.put("CAR-004", new CarEntity("CAR-004", "Kia", "Morning", "UBA-8192",
                "HOURLY", new BigDecimal("22000"), true));
    }

    private List<Car> getFallbackAvailableCars() {
        return getFallbackCars().stream()
                .filter(Car::isAvailable)
                .toList();
    }

    private List<Car> getFallbackCars() {
        return fallbackStore.values().stream()
                .map(persistenceMapper::toDomain)
                .sorted(Comparator.comparing(Car::getBrand).thenComparing(Car::getModel))
                .toList();
    }

    private Optional<Car> findFallbackById(String carId) {
        return Optional.ofNullable(fallbackStore.get(carId)).map(persistenceMapper::toDomain);
    }

    private CarEntity mapCar(ResultSet resultSet) throws SQLException {
        return new CarEntity(
                resultSet.getString("id"),
                resultSet.getString("brand"),
                resultSet.getString("model"),
                resultSet.getString("plate_number"),
                resultSet.getString("rental_type"),
                resultSet.getBigDecimal("price_per_unit"),
                resultSet.getBoolean("available")
        );
    }
}

