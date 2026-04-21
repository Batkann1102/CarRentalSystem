package mn.edu.num.core.service;

import java.time.Instant;
import java.util.UUID;
import mn.edu.num.core.domain.Car;
import mn.edu.num.core.domain.Invoice;
import mn.edu.num.core.domain.Rental;

public class InvoiceGenerator {

    public Invoice generate(Car car, Rental rental) {
        return new Invoice(
                UUID.randomUUID().toString(),
                rental.getId(),
                car.getId(),
                rental.getCustomerName(),
                car.getDisplayName(),
                rental.getRentalType(),
                rental.getDuration(),
                car.getPricePerUnit(),
                rental.getTotalPrice(),
                Instant.now().toString()
        );
    }
}

