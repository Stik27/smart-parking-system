package io.github.stanislav.smartparkingsystem.controller;


import static io.github.stanislav.smartparkingsystem.util.Constants.API_ADD_LEVEL_PARKING_PATH;
import static io.github.stanislav.smartparkingsystem.util.Constants.API_ADD_SLOT_PATH;
import static io.github.stanislav.smartparkingsystem.util.Constants.API_ADMIN_PARKING_PATH;
import static io.github.stanislav.smartparkingsystem.util.Constants.API_CREATE_PARKING_PATH;
import static io.github.stanislav.smartparkingsystem.util.Constants.API_DELETE_LEVEL_PARKING_PATH;
import static io.github.stanislav.smartparkingsystem.util.Constants.API_PARKING_PATH;
import static io.github.stanislav.smartparkingsystem.util.Constants.API_SLOT_PATH;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLevelRequest;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLevelResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLotRequest;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLotResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingSlotRequest;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingSlotUpdateRequest;
import io.github.stanislav.smartparkingsystem.domain.ParkingSlot;
import io.github.stanislav.smartparkingsystem.service.ParkingService;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(API_ADMIN_PARKING_PATH)
public class ParkingManagementController {
	private final ParkingService parkingService;

	@PostMapping(API_CREATE_PARKING_PATH)
	public ResponseEntity<ParkingLotResponse> createParkingLot(@RequestBody ParkingLotRequest request) {
		ParkingLotResponse res = parkingService.createParkingLot(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(res);
	}

	@DeleteMapping(API_PARKING_PATH)
	public ResponseEntity<Void> deleteParkingLot(@PathVariable UUID lotId) {
		parkingService.deleteParkingLot(lotId);
		return ResponseEntity.noContent()
				.build();
	}

	@PostMapping(API_ADD_LEVEL_PARKING_PATH)
	public ResponseEntity<ParkingLevelResponse> createParkingLevel(@PathVariable UUID lotId,
	                                                               @RequestBody ParkingLevelRequest dto) {
		ParkingLevelResponse res = parkingService.createParkingLevel(lotId, dto);
		return ResponseEntity.ok(res);
	}

	@DeleteMapping(API_DELETE_LEVEL_PARKING_PATH)
	public ResponseEntity<Void> deleteLevel(@PathVariable UUID levelId) {
		parkingService.deleteLevel(levelId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping(API_ADD_SLOT_PATH)
	public ResponseEntity<ParkingSlot> createParkingSlot(@PathVariable UUID levelId,
	                                                     @RequestBody ParkingSlotRequest dto) {
		ParkingSlot res = parkingService.createParkingSlot(levelId, dto);
		return ResponseEntity.ok(res);
	}

	@DeleteMapping(API_SLOT_PATH)
	public ResponseEntity<Void> deleteSlot(@PathVariable UUID slotId) {
		parkingService.deleteSlot(slotId);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping(API_SLOT_PATH)
	public ResponseEntity<ParkingSlot> updateSlot(@PathVariable UUID slotId,
	                                              @RequestBody ParkingSlotUpdateRequest dto) {
		ParkingSlot res = parkingService.updateSlot(slotId, dto);
		return ResponseEntity.ok(res);
	}

}
