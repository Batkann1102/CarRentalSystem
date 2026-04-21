package mn.edu.num.carrentaltests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import mn.edu.num.carrental.App;
import mn.edu.num.container.ApplicationContext;
import mn.edu.num.carrental.core.domain.Car;
import mn.edu.num.carrental.core.domain.Invoice;
import mn.edu.num.carrental.core.domain.RentalType;
import mn.edu.num.carrental.core.exception.CarDeletionNotAllowedException;
import mn.edu.num.carrental.core.exception.InvalidRentalTypeException;
import mn.edu.num.carrental.core.ports.in.IGetAvailableCarsUseCase;
import mn.edu.num.carrental.core.ports.in.IGetCarsUseCase;
import mn.edu.num.carrental.core.ports.in.IGetRentalHistoryUseCase;
import mn.edu.num.carrental.core.ports.in.IManageCarsUseCase;
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

    @Test
    void carCrudOperationsShouldWorkThroughUseCases() {
        ApplicationContext applicationContext = ApplicationContext.run(App.class);

        IGetCarsUseCase getCarsUseCase = applicationContext.getBean(IGetCarsUseCase.class);
        IManageCarsUseCase manageCarsUseCase = applicationContext.getBean(IManageCarsUseCase.class);

        Car newCar = new Car("CAR-900", "BMW", "X5", "UBA-9000", RentalType.DAILY,
                new BigDecimal("250000"), true);

        Car createdCar = manageCarsUseCase.createCar(newCar);
        assertNotNull(createdCar);
        assertTrue(getCarsUseCase.getCars().stream().anyMatch(car -> car.getId().equals("CAR-900")));

        Car updateRequest = new Car(null, "BMW", "X7", "UBA-9001", RentalType.HOURLY,
                new BigDecimal("50000"), false);
        Car updatedCar = manageCarsUseCase.updateCar("CAR-900", updateRequest);

        assertTrue(updatedCar.getModel().equals("X7"));
        assertTrue(updatedCar.getRentalType() == RentalType.HOURLY);
        assertTrue(updatedCar.getPlateNumber().equals("UBA-9001"));
        assertFalse(updatedCar.isAvailable());

        manageCarsUseCase.deleteCar("CAR-900");
        assertTrue(getCarsUseCase.getCars().stream().noneMatch(car -> car.getId().equals("CAR-900")));
    }

    @Test
    void deletingCarWithRentalHistoryShouldBeBlocked() {
        ApplicationContext applicationContext = ApplicationContext.run(App.class);

        IGetAvailableCarsUseCase getAvailableCarsUseCase = applicationContext.getBean(IGetAvailableCarsUseCase.class);
        IRentCarUseCase rentCarUseCase = applicationContext.getBean(IRentCarUseCase.class);
        IManageCarsUseCase manageCarsUseCase = applicationContext.getBean(IManageCarsUseCase.class);

        Car selectedCar = getAvailableCarsUseCase.getAvailableCars().get(0);
        rentCarUseCase.rentCar(selectedCar.getId(), "Delete Guard", selectedCar.getRentalType(), 1);

        assertThrows(CarDeletionNotAllowedException.class,
                () -> manageCarsUseCase.deleteCar(selectedCar.getId()));
    }
}
