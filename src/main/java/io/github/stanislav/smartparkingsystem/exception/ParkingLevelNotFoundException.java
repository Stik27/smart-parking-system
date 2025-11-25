package io.github.stanislav.smartparkingsystem.exception;

public class ParkingLevelNotFoundException extends RuntimeException {
	public ParkingLevelNotFoundException(String message) {
		super(message);
	}
}
