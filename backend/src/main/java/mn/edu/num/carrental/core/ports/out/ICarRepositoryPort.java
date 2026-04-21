package mn.edu.num.carrental.core.ports.out;

import java.util.List;
import java.util.Optional;
import mn.edu.num.carrental.core.domain.Car;

public interface ICarRepositoryPort {

    List<Car> findAll();

    List<Car> findAvailableCars();

    Optional<Car> findById(String carId);

    void save(Car car);

    void deleteById(String carId);
}

