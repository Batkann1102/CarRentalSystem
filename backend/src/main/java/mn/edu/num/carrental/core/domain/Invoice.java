package mn.edu.num.carrental.core.domain;

import java.math.BigDecimal;

public class Invoice {

	private String invoiceId;
	private String rentalId;
	private String carId;
	private String customerName;
	private String carDescription;
	private RentalType rentalType;
	private int duration;
	private BigDecimal unitPrice;
	private BigDecimal totalPrice;
	private String issuedAt;

	public Invoice() {
	}

	public Invoice(String invoiceId, String rentalId, String carId, String customerName, String carDescription,
				   RentalType rentalType, int duration, BigDecimal unitPrice, BigDecimal totalPrice,
				   String issuedAt) {
		this.invoiceId = invoiceId;
		this.rentalId = rentalId;
		this.carId = carId;
		this.customerName = customerName;
		this.carDescription = carDescription;
		this.rentalType = rentalType;
		this.duration = duration;
		this.unitPrice = unitPrice;
		this.totalPrice = totalPrice;
		this.issuedAt = issuedAt;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getRentalId() {
		return rentalId;
	}

	public void setRentalId(String rentalId) {
		this.rentalId = rentalId;
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

	public String getCarDescription() {
		return carDescription;
	}

	public void setCarDescription(String carDescription) {
		this.carDescription = carDescription;
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

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(String issuedAt) {
		this.issuedAt = issuedAt;
	}
}
