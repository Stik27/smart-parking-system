package io.github.stanislav.smartparkingsystem.exception;

public class VehicleNotFoundException extends RuntimeException {
	public VehicleNotFoundException(String message) {
		super(message);
	}
}
