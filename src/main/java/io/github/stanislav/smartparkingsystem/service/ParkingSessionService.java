package io.github.stanislav.smartparkingsystem.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.stanislav.smartparkingsystem.controller.dto.vehicle.CheckInRequest;
import io.github.stanislav.smartparkingsystem.controller.dto.vehicle.CheckInResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.vehicle.CheckOutResponse;
import io.github.stanislav.smartparkingsystem.exception.VehicleAlreadyParkedException;
import io.github.stanislav.smartparkingsystem.exception.VehicleNotFoundException;
import io.github.stanislav.smartparkingsystem.repository.ParkingSessionRepository;
import io.github.stanislav.smartparkingsystem.repository.ParkingSlotRepository;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingSessionEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingSlotEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.VehicleEntity;
import io.github.stanislav.smartparkingsystem.service.strategy.PricingStrategy;
import io.github.stanislav.smartparkingsystem.service.strategy.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingSessionService {


	private final ParkingSlotRepository slotRepository;
	private final ParkingSessionRepository sessionRepository;
	private final SlotService slotService;
	private final VehicleService vehicleService;
	private final PricingStrategy pricingStrategy;

	@Transactional
	public CheckInResponse checkInVehicle(CheckInRequest request) {
		log.info("Check-in request for vehicle: {}", request.licensePlate());

		VehicleEntity vehicle = vehicleService.findOrCreateVehicle(request);

		if(sessionRepository.existsByVehicleVehicleIdAndExitTimeIsNull(vehicle.getVehicleId())) {
			throw new VehicleAlreadyParkedException(
					"Vehicle " + request.licensePlate() + " is already parked"
			);
		}

		ParkingSlotEntity slot = slotService.findSuitableSlot(
				request.parkingLotId(),
				request.vehicleType(),
				request.isHandicapped()
		);

		slot.setOccupied(true);
		slot.setCurrentVehicleId(vehicle.getVehicleId());
		slotRepository.save(slot);

		LocalDateTime entryTime = LocalDateTime.now();
		ParkingSessionEntity session = ParkingSessionEntity.builder()
				.slotId(slot.getId())
				.vehicle(vehicle)
				.entryTime(entryTime)
				.build();
		sessionRepository.save(session);

		log.info("Vehicle {} checked in to slot {} at level {}",
				request.licensePlate(), slot.getName(), slot.getParkingLevel().getName());

		return new CheckInResponse(
				vehicle.getVehicleId(),
				vehicle.getLicensePlate(),
				slot.getName(),
				slot.getParkingLevel().getName(),
				slot.getParkingLevel().getParkingLot().getName(),
				slot.getParkingLevel().getParkingLot().getLocation(),
				toInstant(entryTime)
		);
	}

	@Transactional
	public CheckOutResponse checkOutVehicle(UUID vehicleId) {
		log.info("Check-out request for vehicle ID: {}", vehicleId);

		ParkingSessionEntity session = sessionRepository
				.findActiveSessionByVehicleId(vehicleId)
				.orElseThrow(() -> new VehicleNotFoundException(
						"No active parking session for vehicle ID: " + vehicleId
				));

		ParkingSlotEntity slot = slotRepository.findById(session.getSlotId())
				.orElseThrow(() -> new RuntimeException("Slot not found: " + session.getSlotId()));

		LocalDateTime exitTime = LocalDateTime.now();
		Duration parkingDuration = Duration.between(session.getEntryTime(), exitTime);
		String totalTime = formatDuration(parkingDuration);
		BigDecimal fee = pricingStrategy.calculateFee(
				session.getVehicle().getVehicleType(),
				parkingDuration
		);

		session.setExitTime(exitTime);
		session.setFee(fee);
		sessionRepository.save(session);

		slot.setOccupied(false);
		slot.setCurrentVehicleId(null);
		slotRepository.save(slot);

		log.info("Vehicle {} checked out. Fee: ${}",
				session.getVehicle().getLicensePlate(), fee);

		return new CheckOutResponse(
				session.getVehicle().getLicensePlate(),
				toInstant(session.getEntryTime()),
				toInstant(exitTime),
				totalTime,
				fee.doubleValue()
		);
	}

	private Instant toInstant(LocalDateTime localDateTime) {
		return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
	}

	private String formatDuration(Duration d) {
		long hours = d.toHours();
		long minutes = d.toMinutes() % 60;
		long seconds = d.getSeconds() % 60;

		if(hours > 0) {
			return String.format("%dh %02dm %02ds", hours, minutes, seconds);
		} else if(minutes > 0) {
			return String.format("%dm %02ds", minutes, seconds);
		} else {
			return String.format("%ds", seconds);
		}
	}
}