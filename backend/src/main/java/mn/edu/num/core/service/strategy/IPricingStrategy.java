package mn.edu.num.core.service.strategy;

import java.math.BigDecimal;
import mn.edu.num.core.domain.RentalType;

public interface IPricingStrategy {

    boolean supports(RentalType rentalType);

    BigDecimal calculatePrice(BigDecimal unitPrice, int duration);
}

