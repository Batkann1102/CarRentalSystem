package mn.edu.num.core.ports.in;

import java.util.List;
import mn.edu.num.core.domain.Rental;

public interface IGetRentalHistoryUseCase {

    List<Rental> getRentalHistory();
}

