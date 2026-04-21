package mn.edu.num.core.ports.out;

import java.util.List;
import mn.edu.num.core.domain.Rental;

public interface IRentalRepositoryPort {

    Rental save(Rental rental);

    List<Rental> findAll();
}

