package mn.edu.num.carrental.adapters.outbound.persistence.entity;

import java.math.BigDecimal;

public class CarEntity {

    private String id;
    private String brand;
    private String model;
    private String plateNumber;
    private String rentalType;
    private BigDecimal pricePerUnit;
    private boolean available;

    public CarEntity() {
    }

    public CarEntity(String id, String brand, String model, String plateNumber, String rentalType,
                     BigDecimal pricePerUnit, boolean available) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.plateNumber = plateNumber;
        this.rentalType = rentalType;
        this.pricePerUnit = pricePerUnit;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getRentalType() {
        return rentalType;
    }

    public void setRentalType(String rentalType) {
        this.rentalType = rentalType;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

