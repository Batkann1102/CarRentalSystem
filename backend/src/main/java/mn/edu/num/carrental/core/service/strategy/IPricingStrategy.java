package mn.edu.num.carrental.core.service.strategy;

import java.math.BigDecimal;
import mn.edu.num.carrental.core.domain.RentalType;

public interface IPricingStrategy {

    boolean supports(RentalType rentalType);

    BigDecimal calculatePrice(BigDecimal unitPrice, int duration);
}

