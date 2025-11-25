package io.github.stanislav.smartparkingsystem.controller.dto.parking;

import java.time.Instant;
import java.util.UUID;

public record ParkingActiveSession(UUID sessionId,
                                   UUID lotId,
                                   String licensePlate,
                                   String vehicleType,
                                   String slotName,
                                   String levelName,
                                   String parkingLotName,
                                   boolean isHandicapped,
                                   Instant entryTime) {


}
