package mn.edu.num.adapters.outbound.persistence.mapper;

import mn.edu.num.annotation.Component;
import mn.edu.num.adapters.outbound.persistence.entity.CarEntity;
import mn.edu.num.adapters.outbound.persistence.entity.RentalEntity;
import mn.edu.num.core.domain.Car;
import mn.edu.num.core.domain.Rental;
import mn.edu.num.core.domain.RentalType;

@Component
public class PersistenceMapper {

    public Car toDomain(CarEntity entity) {
        return new Car(
                entity.getId(),
                entity.getBrand(),
                entity.getModel(),
                entity.getPlateNumber(),
                RentalType.from(entity.getRentalType()),
                entity.getPricePerUnit(),
                entity.isAvailable()
        );
    }

    public CarEntity toEntity(Car car) {
        return new CarEntity(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getPlateNumber(),
                car.getRentalType().name(),
                car.getPricePerUnit(),
                car.isAvailable()
        );
    }

    public Rental toDomain(RentalEntity entity) {
        return new Rental(
                entity.getId(),
                entity.getCarId(),
                entity.getCustomerName(),
                RentalType.from(entity.getRentalType()),
                entity.getDuration(),
                entity.getRentedAt(),
                entity.getTotalPrice()
        );
    }

    public RentalEntity toEntity(Rental rental) {
        return new RentalEntity(
                rental.getId(),
                rental.getCarId(),
                rental.getCustomerName(),
                rental.getRentalType().name(),
                rental.getDuration(),
                rental.getRentedAt(),
                rental.getTotalPrice()
        );
    }
}

