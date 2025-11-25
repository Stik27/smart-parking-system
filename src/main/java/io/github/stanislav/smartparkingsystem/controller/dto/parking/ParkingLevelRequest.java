package io.github.stanislav.smartparkingsystem.controller.dto.parking;

import jakarta.validation.constraints.NotBlank;

public record ParkingLevelRequest(
		@NotBlank(message = "Level name is required")
		String name
) {
}