package io.github.stanislav.smartparkingsystem.controller.dto.parking;

import java.util.UUID;

public record SlotInfo(
		UUID id,
		String name,
		String slotType,
		boolean isHandicapped,
		boolean isMaintenance,
		boolean occupied,
		UUID currentVehicleId) {
}