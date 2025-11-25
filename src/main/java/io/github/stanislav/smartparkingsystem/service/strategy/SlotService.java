package io.github.stanislav.smartparkingsystem.service.strategy;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.github.stanislav.smartparkingsystem.domain.SlotType;
import io.github.stanislav.smartparkingsystem.domain.VehicleType;
import io.github.stanislav.smartparkingsystem.exception.NoAvailableSlotException;
import io.github.stanislav.smartparkingsystem.repository.ParkingSlotRepository;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingSlotEntity;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SlotService {
	private final ParkingSlotRepository parkingSlotRepository;

	public ParkingSlotEntity findSuitableSlot(UUID parkingLotId,
	                                          VehicleType vehicleType,
	                                          boolean isHandicapped) {
		SlotType requiredSlotType = mapVehicleTypeToSlotType(vehicleType);

		if(isHandicapped) {
			List<ParkingSlotEntity> handicappedSlots = parkingSlotRepository
					.findAvailableHandicappedSlotsByLotIdAndType(parkingLotId, requiredSlotType);

			if(!handicappedSlots.isEmpty()) {
				return handicappedSlots.getFirst();
			}
		}

		List<ParkingSlotEntity> availableSlots = parkingSlotRepository
				.findAvailableSlotsByLotIdAndType(parkingLotId, requiredSlotType);

		if(availableSlots.isEmpty()) {
			throw new NoAvailableSlotException(
					"No available slots for vehicle type: " + vehicleType
			);
		}

		return availableSlots.getFirst();
	}

	private SlotType mapVehicleTypeToSlotType(VehicleType vehicleType) {
		return switch(vehicleType) {
			case MOTORCYCLE, BICYCLE -> SlotType.MOTORCYCLE;
			case CAR -> SlotType.COMPACT;
			case TRUCK -> SlotType.LARGE;
		};
	}
}
