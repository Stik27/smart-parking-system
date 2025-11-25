package io.github.stanislav.smartparkingsystem.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingActiveSession;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingAvailabilityResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLotRequest;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingLotResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.parking.ParkingSlotRequest;
import io.github.stanislav.smartparkingsystem.domain.ParkingSlot;
import io.github.stanislav.smartparkingsystem.domain.SlotType;
import io.github.stanislav.smartparkingsystem.domain.VehicleType;
import io.github.stanislav.smartparkingsystem.mapper.ParkingMapper;
import io.github.stanislav.smartparkingsystem.repository.ParkingLevelRepository;
import io.github.stanislav.smartparkingsystem.repository.ParkingLotRepository;
import io.github.stanislav.smartparkingsystem.repository.ParkingSessionRepository;
import io.github.stanislav.smartparkingsystem.repository.ParkingSlotRepository;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingLevelEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingLotEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingSessionEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingSlotEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.VehicleEntity;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

	@Mock
	private ParkingLotRepository parkingLotRepository;
	@Mock
	private ParkingLevelRepository parkingLevelRepository;
	@Mock
	private ParkingSlotRepository parkingSlotRepository;
	@Mock
	private ParkingSessionRepository parkingSessionRepository;
	@Mock
	private ParkingMapper parkingMapper;

	@InjectMocks
	private ParkingService parkingService;

	@Test
	void getParkingLots_ShouldReturnMappedResponse() {

		List<ParkingLotEntity> lots = List.of(new ParkingLotEntity());
		ParkingAvailabilityResponse response = new ParkingAvailabilityResponse(List.of());

		given(parkingLotRepository.findAll()).willReturn(lots);
		given(parkingMapper.toAvailabilityResponse(lots)).willReturn(response);

		ParkingAvailabilityResponse result = parkingService.getParkingLots();

		Assertions.assertEquals(response, result);
		verify(parkingLotRepository).findAll();
		verify(parkingMapper).toAvailabilityResponse(lots);
	}

	@Test
	void deleteParkingLot_ShouldThrow_WhenOccupiedSlotsExist() {
		UUID lotId = UUID.randomUUID();

		ParkingSlotEntity occupiedSlot = ParkingSlotEntity.builder()
				.occupied(true)
				.build();

		ParkingLevelEntity level = ParkingLevelEntity.builder()
				.slots(List.of(occupiedSlot))
				.build();

		ParkingLotEntity lot = ParkingLotEntity.builder()
				.levels(List.of(level))
				.build();

		given(parkingLotRepository.findById(lotId)).willReturn(Optional.of(lot));

		assertThrows(IllegalStateException.class, () ->
				parkingService.deleteParkingLot(lotId)
		);
	}

	@Test
	void createParkingLot_ShouldSaveAndReturnResponse() {
		ParkingLotRequest request = new ParkingLotRequest("A1", "Center");
		ParkingLotEntity saved = ParkingLotEntity.builder().id(UUID.randomUUID()).build();
		ParkingLotResponse response = new ParkingLotResponse(null, null, null, 0, 0, 0);

		given(parkingLotRepository.save(any())).willReturn(saved);
		given(parkingMapper.toLotResponse(saved)).willReturn(response);

		ParkingLotResponse result = parkingService.createParkingLot(request);

		Assertions.assertEquals(response, result);
		verify(parkingLotRepository).save(any(ParkingLotEntity.class));
		verify(parkingMapper).toLotResponse(saved);
	}

	@Test
	void getActiveSessionByVehicleId_ShouldReturnActiveSessionInfo() {
		UUID vehicleId = UUID.randomUUID();
		UUID lotId = UUID.randomUUID();
		UUID levelId = UUID.randomUUID();
		UUID slotId = UUID.randomUUID();

		VehicleEntity vehicle = VehicleEntity.builder()
				.vehicleId(vehicleId)
				.licensePlate("AA1111BB")
				.vehicleType(VehicleType.CAR)
				.build();

		ParkingLotEntity lot = ParkingLotEntity.builder()
				.id(lotId)
				.name("Main Lot")
				.location("Center")
				.build();

		ParkingLevelEntity level = ParkingLevelEntity.builder()
				.id(levelId)
				.name("L1")
				.parkingLot(lot)
				.build();

		ParkingSlotEntity slot = ParkingSlotEntity.builder()
				.id(slotId)
				.name("S1")
				.parkingLevel(level)
				.isHandicapped(false)
				.build();

		ParkingSessionEntity session = ParkingSessionEntity.builder()
				.id(UUID.randomUUID())
				.slotId(slotId)
				.vehicle(vehicle)
				.entryTime(LocalDateTime.now().minusMinutes(10))
				.build();

		given(parkingSessionRepository.findActiveSessionByVehicleId(vehicleId))
				.willReturn(Optional.of(session));
		given(parkingSlotRepository.findById(slotId))
				.willReturn(Optional.of(slot));

		ParkingActiveSession result = parkingService.getActiveSessionByVehicleId(vehicleId);

		Assertions.assertEquals("AA1111BB", result.licensePlate());
		Assertions.assertEquals("S1", result.slotName());
		Assertions.assertEquals(lotId, result.lotId());
		verify(parkingSessionRepository).findActiveSessionByVehicleId(vehicleId);
		verify(parkingSlotRepository).findById(slotId);
	}

	@Test
	void createParkingSlot_ShouldCreateAndMapDomain() {
		UUID levelId = UUID.randomUUID();

		ParkingLevelEntity level = ParkingLevelEntity.builder()
				.id(levelId)
				.name("L1")
				.build();

		ParkingSlotRequest req = new ParkingSlotRequest(
				"1A",
				SlotType.COMPACT,
				false,
				false
		);

		ParkingSlotEntity saved = ParkingSlotEntity.builder()
				.id(UUID.randomUUID())
				.name("1A")
				.slotType(SlotType.COMPACT)
				.parkingLevel(level)
				.build();

		ParkingSlot domainSlot = new ParkingSlot();
		domainSlot.setId(saved.getId());
		domainSlot.setName(saved.getName());

		given(parkingLevelRepository.findById(levelId)).willReturn(Optional.of(level));
		given(parkingSlotRepository.save(any(ParkingSlotEntity.class))).willReturn(saved);
		given(parkingMapper.toDomain(saved)).willReturn(domainSlot);

		ParkingSlot result = parkingService.createParkingSlot(levelId, req);

		Assertions.assertEquals(domainSlot, result);
		verify(parkingLevelRepository).findById(levelId);
		verify(parkingSlotRepository).save(any(ParkingSlotEntity.class));
		verify(parkingMapper).toDomain(saved);
	}

	@Test
	void deleteSlot_ShouldDelete_WhenNotOccupied() {
		UUID slotId = UUID.randomUUID();

		ParkingSlotEntity slot = ParkingSlotEntity.builder()
				.id(slotId)
				.occupied(false)
				.build();

		given(parkingSlotRepository.findById(slotId)).willReturn(Optional.of(slot));

		parkingService.deleteSlot(slotId);

		verify(parkingSlotRepository).findById(slotId);
		verify(parkingSlotRepository).delete(slot);
	}
}
