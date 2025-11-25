package io.github.stanislav.smartparkingsystem.controller.dto.parking;

import java.util.UUID;

public record ParkingLotResponse(UUID lotId,
                                 String lotName,
                                 String location,
                                 int totalLevels,
                                 int totalSlots,
                                 int availableSlots) {
}
