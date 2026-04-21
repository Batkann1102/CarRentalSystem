package mn.edu.num.carrental.core.ports.in;

import mn.edu.num.carrental.core.domain.Car;

public interface IManageCarsUseCase {

    Car createCar(Car car);

    Car updateCar(String carId, Car car);

    void deleteCar(String carId);
}

