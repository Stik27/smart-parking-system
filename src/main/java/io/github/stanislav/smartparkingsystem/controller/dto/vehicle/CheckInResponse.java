package io.github.stanislav.smartparkingsystem.controller.dto.vehicle;

import java.time.Instant;
import java.util.UUID;

public record CheckInResponse(UUID vehicleId,
                              String licensePlate,
                              String slot,
                              String Level,
                              String parkingLotName,
                              String parkingLotLocation,
                              Instant entryTime) {
}
