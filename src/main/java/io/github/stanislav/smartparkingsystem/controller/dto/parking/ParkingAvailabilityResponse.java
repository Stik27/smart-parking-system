package io.github.stanislav.smartparkingsystem.controller.dto.parking;

import java.util.List;

public record ParkingAvailabilityResponse(List<ParkingLotInfo> parkingLots) {

}
