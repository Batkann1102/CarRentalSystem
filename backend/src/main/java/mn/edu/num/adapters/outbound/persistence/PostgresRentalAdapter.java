package mn.edu.num.adapters.outbound.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;
import mn.edu.num.adapters.outbound.persistence.entity.RentalEntity;
import mn.edu.num.adapters.outbound.persistence.mapper.PersistenceMapper;
import mn.edu.num.core.domain.Rental;
import mn.edu.num.core.ports.out.IRentalRepositoryPort;
import mn.edu.num.infrastructure.database.ConnectionPoolManager;

@Component
public class PostgresRentalAdapter implements IRentalRepositoryPort {

    private static final String INSERT_SQL = """
            INSERT INTO rentals (id, car_id, customer_name, rental_type, duration, rented_at, total_price)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                car_id = EXCLUDED.car_id,
                customer_name = EXCLUDED.customer_name,
                rental_type = EXCLUDED.rental_type,
                duration = EXCLUDED.duration,
                rented_at = EXCLUDED.rented_at,
                total_price = EXCLUDED.total_price
            """;

    private static final String SELECT_ALL_SQL = """
            SELECT id, car_id, customer_name, rental_type, duration, rented_at, total_price
            FROM rentals
            ORDER BY rented_at DESC
            """;

    private final Map<String, RentalEntity> fallbackStore = new LinkedHashMap<>();

    @Autowired
    private PersistenceMapper persistenceMapper;

    @Autowired
    private ConnectionPoolManager connectionPoolManager;

    @Override
    public Rental save(Rental rental) {
        RentalEntity rentalEntity = persistenceMapper.toEntity(rental);
        fallbackStore.put(rental.getId(), rentalEntity);

        Optional<Connection> connectionOptional = connectionPoolManager.getConnection();
        if (connectionOptional.isPresent()) {
            try (Connection connection = connectionOptional.get();
                 PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL)) {
                preparedStatement.setString(1, rental.getId());
                preparedStatement.setString(2, rental.getCarId());
                preparedStatement.setString(3, rental.getCustomerName());
                preparedStatement.setString(4, rental.getRentalType().name());
                preparedStatement.setInt(5, rental.getDuration());
                preparedStatement.setString(6, rental.getRentedAt());
                preparedStatement.setBigDecimal(7, rental.getTotalPrice());
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                System.err.println("[Persistence] Could not persist rental to PostgreSQL: " + ex.getMessage());
            }
        }

        return persistenceMapper.toDomain(rentalEntity);
    }

    @Override
    public List<Rental> findAll() {
        Optional<Connection> connectionOptional = connectionPoolManager.getConnection();
        if (connectionOptional.isPresent()) {
            try (Connection connection = connectionOptional.get();
                 PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_SQL);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Rental> rentals = new ArrayList<>();
                while (resultSet.next()) {
                    rentals.add(persistenceMapper.toDomain(mapRental(resultSet)));
                }
                if (!rentals.isEmpty()) {
                    return rentals;
                }
            } catch (SQLException ex) {
                System.err.println("[Persistence] Falling back to in-memory rentals: " + ex.getMessage());
            }
        }

        return fallbackStore.values().stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    private RentalEntity mapRental(ResultSet resultSet) throws SQLException {
        return new RentalEntity(
                resultSet.getString("id"),
                resultSet.getString("car_id"),
                resultSet.getString("customer_name"),
                resultSet.getString("rental_type"),
                resultSet.getInt("duration"),
                resultSet.getString("rented_at"),
                resultSet.getBigDecimal("total_price")
        );
    }
}

