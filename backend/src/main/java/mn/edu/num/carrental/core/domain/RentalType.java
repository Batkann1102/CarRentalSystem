package mn.edu.num.carrental.core.domain;

import mn.edu.num.carrental.core.exception.InvalidRentalTypeException;

public enum RentalType {

	HOURLY,
	DAILY;

	public static RentalType from(String value) {
		if (value == null || value.trim().isEmpty()) {
			throw new InvalidRentalTypeException("Rental type is required.");
		}

		try {
			return RentalType.valueOf(value.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new InvalidRentalTypeException("Unsupported rental type: " + value);
		}
	}
}
