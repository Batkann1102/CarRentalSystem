package mn.edu.num.adapters.bridge.service;

import java.util.List;
import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;
import mn.edu.num.adapters.bridge.service.strategy.DailyPricingComponent;
import mn.edu.num.adapters.bridge.service.strategy.HourlyPricingComponent;
import mn.edu.num.core.domain.Car;
import mn.edu.num.core.domain.Invoice;
import mn.edu.num.core.domain.Rental;
import mn.edu.num.core.domain.RentalType;
import mn.edu.num.core.ports.in.IGetAvailableCarsUseCase;
import mn.edu.num.core.ports.in.IGetRentalHistoryUseCase;
import mn.edu.num.core.ports.in.IRentCarUseCase;
import mn.edu.num.core.ports.out.ICarRepositoryPort;
import mn.edu.num.core.ports.out.IRentalRepositoryPort;
import mn.edu.num.core.service.CarRentalService;

@Component
public class CarRentalServiceComponent extends CarRentalService
        implements IRentCarUseCase, IGetAvailableCarsUseCase, IGetRentalHistoryUseCase {

    @Autowired
    private ICarRepositoryPort carRepositoryPort;

    @Autowired
    private IRentalRepositoryPort rentalRepositoryPort;

    @Autowired
    private InvoiceGeneratorComponent invoiceGenerator;

    @Autowired
    private HourlyPricingComponent hourlyPricing;

    @Autowired
    private DailyPricingComponent dailyPricing;

    private void prepare() {
        setCarRepositoryPort(carRepositoryPort);
        setRentalRepositoryPort(rentalRepositoryPort);
        setInvoiceGenerator(invoiceGenerator);
        setHourlyPricing(hourlyPricing);
        setDailyPricing(dailyPricing);
    }

    @Override
    public List<Car> getAvailableCars() {
        prepare();
        return super.getAvailableCars();
    }

    @Override
    public List<Rental> getRentalHistory() {
        prepare();
        return super.getRentalHistory();
    }

    @Override
    public Invoice rentCar(String carId, String customerName, RentalType rentalType, int duration) {
        prepare();
        return super.rentCar(carId, customerName, rentalType, duration);
    }
}

