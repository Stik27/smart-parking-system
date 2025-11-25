package io.github.stanislav.smartparkingsystem.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import io.github.stanislav.smartparkingsystem.controller.dto.parking.LevelInfo;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingAvailabilityResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLevelResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLotInfo;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLotResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.SlotInfo;
import io.github.stanislav.smartparkingsystem.domain.ParkingSlot;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingLevelEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingLotEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingSlotEntity;

@Component
public class ParkingMapper {

	public ParkingSlot toDomain(ParkingSlotEntity entity) {
		ParkingSlot domain = new ParkingSlot();
		domain.setId(entity.getId());
		domain.setName(entity.getName());
		domain.setSlotType(entity.getSlotType());
		domain.setHandicapped(entity.isHandicapped());
		domain.setMaintenance(entity.isMaintenance());
		domain.setOccupied(entity.isOccupied());
		domain.setCurrentVehicleId(entity.getCurrentVehicleId());
		return domain;
	}

	public ParkingLotResponse toLotResponse(ParkingLotEntity entity) {
		int totalSlots = entity.getLevels().stream()
				.mapToInt(level -> level.getSlots().size())
				.sum();

		int availableSlots = entity.getLevels().stream()
				.flatMap(level -> level.getSlots().stream())
				.filter(ParkingSlotEntity::isAvailable)
				.mapToInt(slot -> 1)
				.sum();

		return new ParkingLotResponse(
				entity.getId(),
				entity.getName(),
				entity.getLocation(),
				entity.getLevels().size(),
				totalSlots,
				availableSlots
		);
	}

	public ParkingLevelResponse toLevelResponse(ParkingLevelEntity entity) {
		int availableSlots = (int) entity.getSlots().stream()
				.filter(ParkingSlotEntity::isAvailable)
				.count();

		return new ParkingLevelResponse(
				entity.getId(),
				entity.getName(),
				entity.getParkingLot().getId(),
				entity.getSlots().size(),
				availableSlots
		);
	}

	public ParkingAvailabilityResponse toAvailabilityResponse(List<ParkingLotEntity> entities) {
		List<ParkingLotInfo> lotInfos = entities.stream()
				.map(this::toLotInfo)
				.toList();

		return new ParkingAvailabilityResponse(lotInfos);
	}

	private ParkingLotInfo toLotInfo(ParkingLotEntity entity) {
		int totalSlots = entity.getLevels().stream()
				.mapToInt(level -> level.getSlots().size())
				.sum();

		int availableSlots = entity.getLevels().stream()
				.flatMap(level -> level.getSlots().stream())
				.filter(ParkingSlotEntity::isAvailable)
				.mapToInt(slot -> 1)
				.sum();

		List<LevelInfo> levels = entity.getLevels().stream()
				.map(this::toLevelInfo)
				.toList();

		return new ParkingLotInfo(
				entity.getId().toString(),
				entity.getName(),
				entity.getLocation(),
				totalSlots,
				availableSlots,
				levels
		);
	}

	private LevelInfo toLevelInfo(ParkingLevelEntity entity) {
		int availableSlots = (int) entity.getSlots().stream()
				.filter(ParkingSlotEntity::isAvailable)
				.count();

		List<SlotInfo> slots = entity.getSlots().stream()
				.map(this::toSlotInfo)
				.toList();

		return new LevelInfo(
				entity.getId(),
				entity.getName(),
				entity.getSlots().size(),
				availableSlots,
				slots
		);
	}

	private SlotInfo toSlotInfo(ParkingSlotEntity entity) {
		return new SlotInfo(
				entity.getId(),
				entity.getName(),
				entity.getSlotType().name(),
				entity.isHandicapped(),
				entity.isMaintenance(),
				entity.isOccupied(),
				entity.getCurrentVehicleId()
		);
	}
}