package mn.edu.num.carrentaltests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import mn.edu.num.carrental.App;
import mn.edu.num.container.ApplicationContext;
import mn.edu.num.carrental.core.domain.Car;
import mn.edu.num.carrental.core.domain.Invoice;
import mn.edu.num.carrental.core.domain.RentalType;
import mn.edu.num.carrental.core.exception.InvalidRentalTypeException;
import mn.edu.num.carrental.core.ports.in.IGetAvailableCarsUseCase;
import mn.edu.num.carrental.core.ports.in.IGetRentalHistoryUseCase;
import mn.edu.num.carrental.core.ports.in.IRentCarUseCase;
import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void contextShouldProvideSeededAvailableCars() {
        ApplicationContext applicationContext = ApplicationContext.run(App.class);

        IGetAvailableCarsUseCase useCase = applicationContext.getBean(IGetAvailableCarsUseCase.class);
        List<Car> availableCars = useCase.getAvailableCars();

        assertFalse(availableCars.isEmpty());
    }

    @Test
    void rentingCarShouldGenerateInvoiceAndHideCarFromAvailabilityList() {
        ApplicationContext applicationContext = ApplicationContext.run(App.class);

        IGetAvailableCarsUseCase getAvailableCarsUseCase = applicationContext.getBean(IGetAvailableCarsUseCase.class);
        IGetRentalHistoryUseCase getRentalHistoryUseCase = applicationContext.getBean(IGetRentalHistoryUseCase.class);
        IRentCarUseCase rentCarUseCase = applicationContext.getBean(IRentCarUseCase.class);

        Car selectedCar = getAvailableCarsUseCase.getAvailableCars().get(0);
        Invoice invoice = rentCarUseCase.rentCar(
                selectedCar.getId(),
                "JUnit Customer",
                selectedCar.getRentalType(),
                2
        );

        assertNotNull(invoice.getInvoiceId());
        assertTrue(getAvailableCarsUseCase.getAvailableCars().stream()
                .noneMatch(car -> car.getId().equals(selectedCar.getId())));
        assertTrue(getRentalHistoryUseCase.getRentalHistory().stream()
                .anyMatch(rental -> rental.getId().equals(invoice.getRentalId())));
    }

    @Test
    void incompatibleRentalTypeShouldThrowException() {
        ApplicationContext applicationContext = ApplicationContext.run(App.class);

        IGetAvailableCarsUseCase getAvailableCarsUseCase = applicationContext.getBean(IGetAvailableCarsUseCase.class);
        IRentCarUseCase rentCarUseCase = applicationContext.getBean(IRentCarUseCase.class);

        Car hourlyCar = getAvailableCarsUseCase.getAvailableCars().stream()
                .filter(car -> car.getRentalType() == RentalType.HOURLY)
                .findFirst()
                .orElseThrow();

        assertThrows(InvalidRentalTypeException.class,
                () -> rentCarUseCase.rentCar(hourlyCar.getId(), "JUnit Customer", RentalType.DAILY, 1));
    }
}
