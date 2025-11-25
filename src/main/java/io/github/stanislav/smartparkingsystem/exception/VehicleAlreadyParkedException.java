package io.github.stanislav.smartparkingsystem.exception;

public class VehicleAlreadyParkedException extends RuntimeException {
	public VehicleAlreadyParkedException(String message) {
		super(message);
	}
}
