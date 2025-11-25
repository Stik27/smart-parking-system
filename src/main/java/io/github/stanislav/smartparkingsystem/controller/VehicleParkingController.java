package io.github.stanislav.smartparkingsystem.controller;

import static io.github.stanislav.smartparkingsystem.util.Constants.API_CHECK_IN_PATH;
import static io.github.stanislav.smartparkingsystem.util.Constants.API_CHECK_OUT_PATH;
import static io.github.stanislav.smartparkingsystem.util.Constants.API_PARKING_INFO_PATH;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.stanislav.smartparkingsystem.controller.dto.vehicle.CheckInRequest;
import io.github.stanislav.smartparkingsystem.controller.dto.vehicle.CheckInResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.vehicle.CheckOutResponse;
import io.github.stanislav.smartparkingsystem.service.ParkingSessionService;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(API_PARKING_INFO_PATH)
public class VehicleParkingController {

	private final ParkingSessionService parkingSessionService;

	@PostMapping(API_CHECK_IN_PATH)
	public ResponseEntity<CheckInResponse> checkInVehicle(@RequestBody CheckInRequest dto) {
		CheckInResponse res = parkingSessionService.checkInVehicle(dto);
		return ResponseEntity.ok(res);
	}

	@PostMapping(API_CHECK_OUT_PATH)
	public ResponseEntity<CheckOutResponse> checkOutVehicle(@PathVariable UUID vehicleId) {
		CheckOutResponse res = parkingSessionService.checkOutVehicle(vehicleId);
		return ResponseEntity.ok(res);
	}

}
