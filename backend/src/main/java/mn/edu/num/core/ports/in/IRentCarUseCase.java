package mn.edu.num.core.ports.in;

import mn.edu.num.core.domain.Invoice;
import mn.edu.num.core.domain.RentalType;

public interface IRentCarUseCase {

	Invoice rentCar(String carId, String customerName, RentalType rentalType, int duration);
}

