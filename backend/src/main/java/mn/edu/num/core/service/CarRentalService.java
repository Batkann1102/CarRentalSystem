package mn.edu.num.core.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import mn.edu.num.core.domain.Car;
import mn.edu.num.core.domain.Invoice;
import mn.edu.num.core.domain.Rental;
import mn.edu.num.core.domain.RentalType;
import mn.edu.num.core.exception.CarNotAvailableException;
import mn.edu.num.core.exception.CarNotFoundException;
import mn.edu.num.core.exception.InvalidRentalTypeException;
import mn.edu.num.core.ports.in.IGetAvailableCarsUseCase;
import mn.edu.num.core.ports.in.IGetRentalHistoryUseCase;
import mn.edu.num.core.ports.in.IRentCarUseCase;
import mn.edu.num.core.ports.out.ICarRepositoryPort;
import mn.edu.num.core.ports.out.IRentalRepositoryPort;
import mn.edu.num.core.service.strategy.DailyPricing;
import mn.edu.num.core.service.strategy.HourlyPricing;
import mn.edu.num.core.service.strategy.IPricingStrategy;

public class CarRentalService implements IRentCarUseCase, IGetAvailableCarsUseCase, IGetRentalHistoryUseCase {

    private ICarRepositoryPort carRepositoryPort;

    private IRentalRepositoryPort rentalRepositoryPort;

    private InvoiceGenerator invoiceGenerator;

    private HourlyPricing hourlyPricing;

    private DailyPricing dailyPricing;

    public void setCarRepositoryPort(ICarRepositoryPort carRepositoryPort) {
        this.carRepositoryPort = carRepositoryPort;
    }

    public void setRentalRepositoryPort(IRentalRepositoryPort rentalRepositoryPort) {
        this.rentalRepositoryPort = rentalRepositoryPort;
    }

    public void setInvoiceGenerator(InvoiceGenerator invoiceGenerator) {
        this.invoiceGenerator = invoiceGenerator;
    }

    public void setHourlyPricing(HourlyPricing hourlyPricing) {
        this.hourlyPricing = hourlyPricing;
    }

    public void setDailyPricing(DailyPricing dailyPricing) {
        this.dailyPricing = dailyPricing;
    }

    @Override
    public List<Car> getAvailableCars() {
        List<Car> cars = new ArrayList<Car>(carRepositoryPort.findAvailableCars());
        cars.sort(Comparator.comparing(Car::getBrand).thenComparing(Car::getModel));
        return cars;
    }

    @Override
    public List<Rental> getRentalHistory() {
        return new ArrayList<Rental>(rentalRepositoryPort.findAll());
    }

    @Override
    public Invoice rentCar(String carId, String customerName, RentalType rentalType, int duration) {
        validateRequest(carId, customerName, duration);

        Car car = carRepositoryPort.findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found with id: " + carId));

        if (!car.isAvailable()) {
            throw new CarNotAvailableException("Car is currently not available: " + car.getDisplayName());
        }

        if (car.getRentalType() != rentalType) {
            throw new InvalidRentalTypeException(
                    "Car supports only " + car.getRentalType().name().toLowerCase() + " rentals."
            );
        }

        IPricingStrategy pricingStrategy = resolveStrategy(rentalType);
        BigDecimal totalPrice = pricingStrategy.calculatePrice(car.getPricePerUnit(), duration);
        Rental rental = new Rental(
                UUID.randomUUID().toString(),
                car.getId(),
                customerName.trim(),
                rentalType,
                duration,
                Instant.now().toString(),
                totalPrice
        );

        car.markUnavailable();
        carRepositoryPort.save(car);
        Rental savedRental = rentalRepositoryPort.save(rental);

        return invoiceGenerator.generate(car, savedRental);
    }

    private void validateRequest(String carId, String customerName, int duration) {
        if (carId == null || carId.trim().isEmpty()) {
            throw new IllegalArgumentException("Car id is required.");
        }

        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required.");
        }

        if (duration <= 0) {
            throw new IllegalArgumentException("Rental duration must be greater than 0.");
        }
    }

    private IPricingStrategy resolveStrategy(RentalType rentalType) {
        if (hourlyPricing.supports(rentalType)) {
            return hourlyPricing;
        }

        if (dailyPricing.supports(rentalType)) {
            return dailyPricing;
        }

        throw new InvalidRentalTypeException("Unsupported rental type: " + rentalType);
    }
}

