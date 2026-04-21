package mn.edu.num.core.ports.out;

import java.util.List;
import java.util.Optional;
import mn.edu.num.core.domain.Car;

public interface ICarRepositoryPort {

    List<Car> findAvailableCars();

    Optional<Car> findById(String carId);

    void save(Car car);
}

