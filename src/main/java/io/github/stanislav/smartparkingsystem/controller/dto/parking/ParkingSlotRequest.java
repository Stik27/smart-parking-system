package io.github.stanislav.smartparkingsystem.controller.dto.parking;

import io.github.stanislav.smartparkingsystem.domain.SlotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record ParkingSlotRequest(
		@NotBlank(message = "Slot name is required")
		String name,
		@NotNull(message = "Slot type is required")
		SlotType slotType,
		boolean isHandicapped,
		boolean isMaintenance
) {
}