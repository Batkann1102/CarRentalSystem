package mn.edu.num.carrental.core.ports.out;

import java.util.List;
import mn.edu.num.carrental.core.domain.Rental;

public interface IRentalRepositoryPort {

    Rental save(Rental rental);

    List<Rental> findAll();
}

