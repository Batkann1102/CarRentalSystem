package mn.edu.num.carrental.adapters.outbound.persistence.entity;

import java.math.BigDecimal;

public class RentalEntity {

    private String id;
    private String carId;
    private String customerName;
    private String rentalType;
    private int duration;
    private String rentedAt;
    private BigDecimal totalPrice;

    public RentalEntity() {
    }

    public RentalEntity(String id, String carId, String customerName, String rentalType, int duration,
                        String rentedAt, BigDecimal totalPrice) {
        this.id = id;
        this.carId = carId;
        this.customerName = customerName;
        this.rentalType = rentalType;
        this.duration = duration;
        this.rentedAt = rentedAt;
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getRentalType() {
        return rentalType;
    }

    public void setRentalType(String rentalType) {
        this.rentalType = rentalType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getRentedAt() {
        return rentedAt;
    }

    public void setRentedAt(String rentedAt) {
        this.rentedAt = rentedAt;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}

