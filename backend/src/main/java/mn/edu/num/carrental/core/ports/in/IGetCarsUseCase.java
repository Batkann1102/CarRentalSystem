package mn.edu.num.carrental.core.ports.in;

import java.util.List;
import mn.edu.num.carrental.core.domain.Car;

public interface IGetCarsUseCase {

    List<Car> getCars();

    Car getCarById(String carId);
}

