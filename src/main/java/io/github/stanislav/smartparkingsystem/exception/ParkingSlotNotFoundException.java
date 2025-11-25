package io.github.stanislav.smartparkingsystem.exception;

public class ParkingSlotNotFoundException extends RuntimeException {
	public ParkingSlotNotFoundException(String message) {
		super(message);
	}
}
