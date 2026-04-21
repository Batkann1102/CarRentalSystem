package mn.edu.num.core.ports.in;

import java.util.List;
import mn.edu.num.core.domain.Car;

public interface IGetAvailableCarsUseCase {

	List<Car> getAvailableCars();
}

