package io.github.stanislav.smartparkingsystem.controller.dto.vehicle;

import java.util.UUID;

import io.github.stanislav.smartparkingsystem.domain.VehicleType;

public record CheckInRequest(UUID parkingLotId,
                             String licensePlate,
                             VehicleType vehicleType,
                             boolean isHandicapped) {
}
