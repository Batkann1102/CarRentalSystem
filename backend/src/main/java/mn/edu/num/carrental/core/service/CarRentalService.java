package mn.edu.num.carrental.core.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;
import mn.edu.num.carrental.core.domain.Car;
import mn.edu.num.carrental.core.domain.Invoice;
import mn.edu.num.carrental.core.domain.Rental;
import mn.edu.num.carrental.core.domain.RentalType;
import mn.edu.num.carrental.core.exception.CarDeletionNotAllowedException;
import mn.edu.num.carrental.core.exception.CarNotAvailableException;
import mn.edu.num.carrental.core.exception.CarNotFoundException;
import mn.edu.num.carrental.core.exception.DuplicateCarException;
import mn.edu.num.carrental.core.exception.InvalidRentalTypeException;
import mn.edu.num.carrental.core.ports.in.IGetAvailableCarsUseCase;
import mn.edu.num.carrental.core.ports.in.IGetCarsUseCase;
import mn.edu.num.carrental.core.ports.in.IGetRentalHistoryUseCase;
import mn.edu.num.carrental.core.ports.in.IManageCarsUseCase;
import mn.edu.num.carrental.core.ports.in.IRentCarUseCase;
import mn.edu.num.carrental.core.ports.out.ICarRepositoryPort;
import mn.edu.num.carrental.core.ports.out.IRentalRepositoryPort;
import mn.edu.num.carrental.core.service.strategy.DailyPricing;
import mn.edu.num.carrental.core.service.strategy.HourlyPricing;
import mn.edu.num.carrental.core.service.strategy.IPricingStrategy;

@Component
public class CarRentalService implements IRentCarUseCase, IGetAvailableCarsUseCase, IGetRentalHistoryUseCase,
        IGetCarsUseCase, IManageCarsUseCase {

    @Autowired
    private ICarRepositoryPort carRepositoryPort;

    @Autowired
    private IRentalRepositoryPort rentalRepositoryPort;

    @Autowired
    private InvoiceGenerator invoiceGenerator;

    @Autowired
    private HourlyPricing hourlyPricing;

    @Autowired
    private DailyPricing dailyPricing;


    @Override
    public List<Car> getAvailableCars() {
        List<Car> cars = new ArrayList<Car>(carRepositoryPort.findAvailableCars());
        cars.sort(Comparator.comparing(Car::getBrand).thenComparing(Car::getModel));
        return cars;
    }

    @Override
    public List<Car> getCars() {
        List<Car> cars = new ArrayList<Car>(carRepositoryPort.findAll());
        cars.sort(Comparator.comparing(Car::getBrand).thenComparing(Car::getModel));
        return cars;
    }

    @Override
    public Car getCarById(String carId) {
        validateCarId(carId);
        return carRepositoryPort.findById(carId.trim())
                .orElseThrow(() -> new CarNotFoundException("Car not found with id: " + carId));
    }

    @Override
    public List<Rental> getRentalHistory() {
        return new ArrayList<Rental>(rentalRepositoryPort.findAll());
    }

    @Override
    public Car createCar(Car car) {
        Car normalizedCar = normalizeCar(car, true);
        if (carRepositoryPort.findById(normalizedCar.getId()).isPresent()) {
            throw new DuplicateCarException("Car already exists with id: " + normalizedCar.getId());
        }

        ensurePlateNumberUnique(normalizedCar.getPlateNumber(), normalizedCar.getId());
        carRepositoryPort.save(normalizedCar);
        return normalizedCar;
    }

    @Override
    public Car updateCar(String carId, Car car) {
        validateCarId(carId);
        Car existingCar = carRepositoryPort.findById(carId.trim())
                .orElseThrow(() -> new CarNotFoundException("Car not found with id: " + carId));

        Car normalizedCar = normalizeCar(car, false);
        ensurePlateNumberUnique(normalizedCar.getPlateNumber(), existingCar.getId());

        existingCar.setBrand(normalizedCar.getBrand());
        existingCar.setModel(normalizedCar.getModel());
        existingCar.setPlateNumber(normalizedCar.getPlateNumber());
        existingCar.setRentalType(normalizedCar.getRentalType());
        existingCar.setPricePerUnit(normalizedCar.getPricePerUnit());
        existingCar.setAvailable(normalizedCar.isAvailable());

        carRepositoryPort.save(existingCar);
        return existingCar;
    }

    @Override
    public void deleteCar(String carId) {
        validateCarId(carId);
        String trimmedCarId = carId.trim();
        carRepositoryPort.findById(trimmedCarId)
                .orElseThrow(() -> new CarNotFoundException("Car not found with id: " + carId));

        if (rentalRepositoryPort.existsByCarId(trimmedCarId)) {
            throw new CarDeletionNotAllowedException("Cannot delete a car that already has rental history.");
        }

        carRepositoryPort.deleteById(trimmedCarId);
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
        validateCarId(carId);

        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required.");
        }

        if (duration <= 0) {
            throw new IllegalArgumentException("Rental duration must be greater than 0.");
        }
    }

    private void validateCarId(String carId) {
        if (carId == null || carId.trim().isEmpty()) {
            throw new IllegalArgumentException("Car id is required.");
        }
    }

    private Car normalizeCar(Car car, boolean requireId) {
        if (car == null) {
            throw new IllegalArgumentException("Car payload is required.");
        }

        String id = car.getId() == null ? "" : car.getId().trim();
        String brand = car.getBrand() == null ? "" : car.getBrand().trim();
        String model = car.getModel() == null ? "" : car.getModel().trim();
        String plateNumber = car.getPlateNumber() == null ? "" : car.getPlateNumber().trim().toUpperCase(Locale.ROOT);

        if (requireId && id.isEmpty()) {
            throw new IllegalArgumentException("Car id is required.");
        }

        if (brand.isEmpty()) {
            throw new IllegalArgumentException("Car brand is required.");
        }

        if (model.isEmpty()) {
            throw new IllegalArgumentException("Car model is required.");
        }

        if (plateNumber.isEmpty()) {
            throw new IllegalArgumentException("Plate number is required.");
        }

        if (car.getRentalType() == null) {
            throw new IllegalArgumentException("Rental type is required.");
        }

        if (car.getPricePerUnit() == null || car.getPricePerUnit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price per unit must be greater than 0.");
        }

        return new Car(id, brand, model, plateNumber, car.getRentalType(), car.getPricePerUnit(), car.isAvailable());
    }

    private void ensurePlateNumberUnique(String plateNumber, String currentCarId) {
        boolean duplicateExists = carRepositoryPort.findAll().stream()
                .anyMatch(car -> !car.getId().equals(currentCarId)
                        && car.getPlateNumber() != null
                        && car.getPlateNumber().trim().equalsIgnoreCase(plateNumber));

        if (duplicateExists) {
            throw new DuplicateCarException("Plate number already exists: " + plateNumber);
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

