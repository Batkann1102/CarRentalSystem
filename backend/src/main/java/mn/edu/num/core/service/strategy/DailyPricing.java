package mn.edu.num.core.service.strategy;

import java.math.BigDecimal;
import mn.edu.num.core.domain.RentalType;

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

