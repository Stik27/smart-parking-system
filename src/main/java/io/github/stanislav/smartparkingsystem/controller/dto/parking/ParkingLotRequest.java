package io.github.stanislav.smartparkingsystem.controller.dto.parking;

import jakarta.validation.constraints.NotBlank;

public record ParkingLotRequest(
		@NotBlank(message = "Name is required") String name,
		String location) {
}
