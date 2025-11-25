package io.github.stanislav.smartparkingsystem.controller;

import static io.github.stanislav.smartparkingsystem.util.Constants.API_ACTIVE_SESSION_PATH;
import static io.github.stanislav.smartparkingsystem.util.Constants.API_PARKING_LOTS_PATH;
import static io.github.stanislav.smartparkingsystem.util.Constants.API_PATH;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingActiveSession;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingAvailabilityResponse;
import io.github.stanislav.smartparkingsystem.service.ParkingService;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(API_PATH)
public class ParkingInfoController {
	private final ParkingService parkingService;

	@GetMapping(API_PARKING_LOTS_PATH)
	public ResponseEntity<ParkingAvailabilityResponse> getParkingLots() {
		ParkingAvailabilityResponse res = parkingService.getParkingLots();
		return ResponseEntity.ok(res);
	}

	@GetMapping(API_ACTIVE_SESSION_PATH)
	public ResponseEntity<ParkingActiveSession> getActiveSessionByVehicleId(@PathVariable UUID vehicleId) {
		ParkingActiveSession res = parkingService.getActiveSessionByVehicleId(vehicleId);
		return ResponseEntity.ok(res);
	}

}
