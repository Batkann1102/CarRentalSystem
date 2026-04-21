package mn.edu.num.carrental.core.ports.in;

import java.util.List;
import mn.edu.num.carrental.core.domain.Rental;

public interface IGetRentalHistoryUseCase {

    List<Rental> getRentalHistory();
}

