package io.github.stanislav.smartparkingsystem.service;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingActiveSession;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingAvailabilityResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLevelRequest;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLevelResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLotRequest;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLotResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingSlotRequest;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingSlotUpdateRequest;
import io.github.stanislav.smartparkingsystem.domain.ParkingSlot;
import io.github.stanislav.smartparkingsystem.exception.ParkingLevelNotFoundException;
import io.github.stanislav.smartparkingsystem.exception.ParkingLotNotFoundException;
import io.github.stanislav.smartparkingsystem.exception.ParkingSlotNotFoundException;
import io.github.stanislav.smartparkingsystem.exception.VehicleNotFoundException;
import io.github.stanislav.smartparkingsystem.mapper.ParkingMapper;
import io.github.stanislav.smartparkingsystem.repository.ParkingLevelRepository;
import io.github.stanislav.smartparkingsystem.repository.ParkingLotRepository;
import io.github.stanislav.smartparkingsystem.repository.ParkingSessionRepository;
import io.github.stanislav.smartparkingsystem.repository.ParkingSlotRepository;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingLevelEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingLotEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingSessionEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingSlotEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingService {

	private final ParkingLotRepository parkingLotRepository;
	private final ParkingLevelRepository parkingLevelRepository;
	private final ParkingSlotRepository parkingSlotRepository;
	private final ParkingSessionRepository parkingSessionRepository;
	private final ParkingMapper parkingMapper;

	@Transactional(readOnly = true)
	public ParkingAvailabilityResponse getParkingLots() {
		log.info("Fetching all parking lots with availability info");
		List<ParkingLotEntity> lots = parkingLotRepository.findAll();
		return parkingMapper.toAvailabilityResponse(lots);
	}

	@Transactional(readOnly = true)
	public ParkingActiveSession getActiveSessionByVehicleId(UUID vehicleId) {
		log.info("Fetching active session for vehicle ID: {}", vehicleId);

		ParkingSessionEntity session = parkingSessionRepository
				.findActiveSessionByVehicleId(vehicleId)
				.orElseThrow(() -> new VehicleNotFoundException(
						"No active session found for vehicle ID: " + vehicleId
				));

		ParkingSlotEntity slot = parkingSlotRepository.findById(session.getSlotId())
				.orElseThrow(() -> new ParkingSlotNotFoundException(
						"Slot not found: " + session.getSlotId()
				));

		return new ParkingActiveSession(
				session.getId(),
				slot.getParkingLevel().getParkingLot().getId(),
				session.getVehicle().getLicensePlate(),
				session.getVehicle().getVehicleType().name(),
				slot.getName(),
				slot.getParkingLevel().getName(),
				slot.getParkingLevel().getParkingLot().getName(),
				slot.isHandicapped(),
				session.getEntryTime().atZone(ZoneId.systemDefault()).toInstant()
		);
	}

	@Transactional
	public ParkingLotResponse createParkingLot(ParkingLotRequest request) {
		log.info("Creating parking lot: {}", request.name());

		ParkingLotEntity entity = ParkingLotEntity.builder()
				.name(request.name())
				.location(request.location())
				.build();

		ParkingLotEntity saved = parkingLotRepository.save(entity);
		log.info("Parking lot created with ID: {}", saved.getId());

		return parkingMapper.toLotResponse(saved);
	}

	@Transactional
	public void deleteParkingLot(UUID lotId) {
		log.info("Deleting parking lot: {}", lotId);

		ParkingLotEntity lot = parkingLotRepository.findById(lotId)
				.orElseThrow(() -> new ParkingLotNotFoundException(
						"Parking lot not found: " + lotId
				));

		boolean hasOccupiedSlots = lot.getLevels().stream()
				.flatMap(level -> level.getSlots().stream())
				.anyMatch(ParkingSlotEntity::isOccupied);

		if(hasOccupiedSlots) {
			throw new IllegalStateException(
					"Cannot delete parking lot with occupied slots"
			);
		}

		parkingLotRepository.delete(lot);
		log.info("Parking lot deleted: {}", lotId);
	}

	@Transactional
	public ParkingLevelResponse createParkingLevel(UUID lotId, ParkingLevelRequest request) {
		log.info("Creating level '{}' for parking lot: {}", request.name(), lotId);

		ParkingLotEntity lot = parkingLotRepository.findById(lotId)
				.orElseThrow(() -> new ParkingLotNotFoundException(
						"Parking lot not found: " + lotId
				));

		ParkingLevelEntity level = ParkingLevelEntity.builder()
				.name(request.name())
				.parkingLot(lot)
				.build();

		lot.addLevel(level);
		ParkingLevelEntity saved = parkingLevelRepository.save(level);

		log.info("Level created with ID: {}", saved.getId());
		return parkingMapper.toLevelResponse(saved);
	}

	@Transactional
	public void deleteLevel(UUID levelId) {
		log.info("Deleting level: {}", levelId);

		ParkingLevelEntity level = parkingLevelRepository.findById(levelId)
				.orElseThrow(() -> new ParkingLevelNotFoundException(
						"Level not found: " + levelId
				));

		boolean hasOccupiedSlots = level.getSlots().stream()
				.anyMatch(ParkingSlotEntity::isOccupied);

		if(hasOccupiedSlots) {
			throw new IllegalStateException(
					"Cannot delete level with occupied slots"
			);
		}

		parkingLevelRepository.delete(level);
		log.info("Level deleted: {}", levelId);
	}

	@Transactional
	public ParkingSlot createParkingSlot(UUID levelId, ParkingSlotRequest request) {
		log.info("Creating slot '{}' for level: {}", request.name(), levelId);

		ParkingLevelEntity level = parkingLevelRepository.findById(levelId)
				.orElseThrow(() -> new ParkingLevelNotFoundException(
						"Level not found: " + levelId
				));

		ParkingSlotEntity slot = ParkingSlotEntity.builder()
				.name(request.name())
				.slotType(request.slotType())
				.isHandicapped(request.isHandicapped())
				.isMaintenance(request.isMaintenance())
				.occupied(false)
				.parkingLevel(level)
				.build();

		level.addSlot(slot);
		ParkingSlotEntity saved = parkingSlotRepository.save(slot);

		log.info("Slot created with ID: {}", saved.getId());
		return parkingMapper.toDomain(saved);
	}

	@Transactional
	public void deleteSlot(UUID slotId) {
		log.info("Deleting slot: {}", slotId);

		ParkingSlotEntity slot = parkingSlotRepository.findById(slotId)
				.orElseThrow(() -> new ParkingSlotNotFoundException(
						"Slot not found: " + slotId
				));

		if(slot.isOccupied()) {
			throw new IllegalStateException(
					"Cannot delete occupied slot: " + slotId
			);
		}

		parkingSlotRepository.delete(slot);
		log.info("Slot deleted: {}", slotId);
	}

	@Transactional
	public ParkingSlot updateSlot(UUID slotId, ParkingSlotUpdateRequest request) {
		log.info("Updating slot: {}", slotId);

		ParkingSlotEntity slot = parkingSlotRepository.findById(slotId)
				.orElseThrow(() -> new ParkingSlotNotFoundException(
						"Slot not found: " + slotId
				));

		if(slot.isOccupied()) {
			boolean tryingToChangeImportantFields =
					!slot.getSlotType().equals(request.slotType()) ||
							slot.isHandicapped() != request.isHandicapped() ||
							slot.isMaintenance() != request.isMaintenance();

			if(tryingToChangeImportantFields) {
				throw new IllegalStateException(
						"Cannot modify slot properties while it is occupied. " +
								"Only name changes are allowed. Please wait for vehicle checkout."
				);
			}
		}
		if(request.name() != null) {
			slot.setName(request.name());
		}
		if(request.slotType() != null) {
			slot.setSlotType(request.slotType());
		}
		if(request.isHandicapped() != null) {
			slot.setHandicapped(request.isHandicapped());
		}
		if(request.isMaintenance() != null) {
			slot.setMaintenance(request.isMaintenance());
		}

		ParkingSlotEntity updated = parkingSlotRepository.save(slot);
		log.info("Slot updated: {}", slotId);

		return parkingMapper.toDomain(updated);
	}
}
