package io.github.stanislav.smartparkingsystem.controller.dto.parking;

import java.util.List;

public record ParkingLotInfo(
		String id,
		String name,
		String location,
		int totalSlots,
		int availableSlots,
		List<LevelInfo> levels
) {
}