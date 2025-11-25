package io.github.stanislav.smartparkingsystem.exception;

public class NoAvailableSlotException extends RuntimeException {
	public NoAvailableSlotException(String message) {
		super(message);
	}
}
