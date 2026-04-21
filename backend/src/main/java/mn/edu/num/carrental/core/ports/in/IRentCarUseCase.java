package mn.edu.num.carrental.core.ports.in;

import mn.edu.num.carrental.core.domain.Invoice;
import mn.edu.num.carrental.core.domain.RentalType;

public interface IRentCarUseCase {

	Invoice rentCar(String carId, String customerName, RentalType rentalType, int duration);
}

