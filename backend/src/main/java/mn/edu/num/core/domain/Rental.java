package mn.edu.num.core.domain;

import java.math.BigDecimal;

public class Rental {

	private String id;
	private String carId;
	private String customerName;
	private RentalType rentalType;
	private int duration;
	private String rentedAt;
	private BigDecimal totalPrice;

	public Rental() {
	}

	public Rental(String id, String carId, String customerName, RentalType rentalType, int duration,
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

	public RentalType getRentalType() {
		return rentalType;
	}

	public void setRentalType(RentalType rentalType) {
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
