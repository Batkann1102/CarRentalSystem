package mn.edu.num.carrental.core.service.strategy;

import java.math.BigDecimal;
import mn.edu.num.annotation.Component;
import mn.edu.num.carrental.core.domain.RentalType;

@Component
public class DailyPricing implements IPricingStrategy {

    @Override
    public boolean supports(RentalType rentalType) {
        return RentalType.DAILY == rentalType;
    }

    @Override
    public BigDecimal calculatePrice(BigDecimal unitPrice, int duration) {
        validateDuration(duration);
        return unitPrice.multiply(BigDecimal.valueOf(duration));
    }

    private void validateDuration(int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Daily rental duration must be greater than 0.");
        }
    }
}

