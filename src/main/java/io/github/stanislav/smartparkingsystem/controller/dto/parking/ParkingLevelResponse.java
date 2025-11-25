package io.github.stanislav.smartparkingsystem.controller.dto.parking;

import java.util.UUID;

public record ParkingLevelResponse(UUID id,
                                   String name,
                                   UUID parkingLotId,
                                   int totalSlots,
                                   int availableSlots) {

}
