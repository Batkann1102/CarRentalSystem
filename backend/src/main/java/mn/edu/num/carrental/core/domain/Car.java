package mn.edu.num.carrental.core.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Car {

	private String id;
	private String brand;
	private String model;
	private String plateNumber;
	private RentalType rentalType;
	private BigDecimal pricePerUnit;
	private boolean available;

	public Car() {
	}

	public Car(String id, String brand, String model, String plateNumber, RentalType rentalType,
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

	public RentalType getRentalType() {
		return rentalType;
	}

	public void setRentalType(RentalType rentalType) {
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

	public void markUnavailable() {
		this.available = false;
	}

	public void markAvailable() {
		this.available = true;
	}

	public String getDisplayName() {
		return brand + " " + model + " (" + plateNumber + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Car)) {
			return false;
		}
		Car car = (Car) o;
		return Objects.equals(id, car.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
