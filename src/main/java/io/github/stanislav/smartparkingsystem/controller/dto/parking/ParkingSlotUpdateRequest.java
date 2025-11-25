package io.github.stanislav.smartparkingsystem.controller.dto.parking;

import io.github.stanislav.smartparkingsystem.domain.SlotType;


public record ParkingSlotUpdateRequest(
		String name,
		SlotType slotType,
		Boolean isHandicapped,
		Boolean isMaintenance
) {
}