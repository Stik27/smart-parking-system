package io.github.stanislav.smartparkingsystem.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.stanislav.smartparkingsystem.controller.dto.vehicle.CheckInRequest;
import io.github.stanislav.smartparkingsystem.controller.dto.vehicle.CheckInResponse;
import io.github.stanislav.smartparkingsystem.controller.dto.vehicle.CheckOutResponse;
import io.github.stanislav.smartparkingsystem.domain.VehicleType;
import io.github.stanislav.smartparkingsystem.repository.ParkingSessionRepository;
import io.github.stanislav.smartparkingsystem.repository.ParkingSlotRepository;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingLevelEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingLotEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingSessionEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingSlotEntity;
import io.github.stanislav.smartparkingsystem.repository.entity.VehicleEntity;
import io.github.stanislav.smartparkingsystem.service.strategy.PricingStrategy;
import io.github.stanislav.smartparkingsystem.service.strategy.SlotService;

@ExtendWith(MockitoExtension.class)
class ParkingSessionServiceTest {

	@Mock
	private ParkingSlotRepository slotRepository;
	@Mock
	private ParkingSessionRepository sessionRepository;
	@Mock
	private SlotService slotService;
	@Mock
	private VehicleService vehicleService;
	@Mock
	private PricingStrategy pricingStrategy;

	@InjectMocks
	private ParkingSessionService parkingSessionService;

	@Test
	void checkInVehicle_ShouldCreateSession() {
		CheckInRequest request = new CheckInRequest(
				UUID.randomUUID(), "AA1111BB", VehicleType.CAR, false
		);

		VehicleEntity vehicle = VehicleEntity.builder()
				.vehicleId(UUID.randomUUID())
				.vehicleType(VehicleType.CAR)
				.licensePlate("AA1111BB")
				.build();

		ParkingSlotEntity slot = ParkingSlotEntity.builder()
				.id(UUID.randomUUID())
				.name("S1")
				.parkingLevel(
						ParkingLevelEntity.builder()
								.name("L1")
								.parkingLot(
										ParkingLotEntity.builder()
												.name("Main Lot")
												.location("Center")
												.build())
								.build())
				.build();

		given(vehicleService.findOrCreateVehicle(request)).willReturn(vehicle);
		given(sessionRepository.existsByVehicleVehicleIdAndExitTimeIsNull(vehicle.getVehicleId()))
				.willReturn(false);

		given(slotService.findSuitableSlot(
				any(UUID.class),
				any(VehicleType.class),
				anyBoolean()
		)).willReturn(slot);

		CheckInResponse response = parkingSessionService.checkInVehicle(request);

		Assertions.assertEquals("AA1111BB", response.licensePlate());
		Assertions.assertEquals("S1", response.slot());
		verify(sessionRepository).save(any());
		verify(slotRepository).save(any());
	}

	@Test
	void checkOutVehicle_ShouldCalculateFee() {
		UUID vehicleId = UUID.randomUUID();

		VehicleEntity vehicle = VehicleEntity.builder()
				.vehicleType(VehicleType.CAR)
				.licensePlate("AA1111BB")
				.build();

		ParkingSessionEntity session = ParkingSessionEntity.builder()
				.slotId(UUID.randomUUID())
				.entryTime(LocalDateTime.now().minusMinutes(10))
				.vehicle(vehicle)
				.build();

		ParkingSlotEntity slot = ParkingSlotEntity.builder()
				.id(session.getSlotId())
				.build();

		given(sessionRepository.findActiveSessionByVehicleId(vehicleId))
				.willReturn(Optional.of(session));
		given(slotRepository.findById(session.getSlotId()))
				.willReturn(Optional.of(slot));

		given(pricingStrategy.calculateFee(any(), any()))
				.willReturn(BigDecimal.TEN);

		CheckOutResponse response = parkingSessionService.checkOutVehicle(vehicleId);

		Assertions.assertEquals("AA1111BB", response.licensePlate());
		Assertions.assertEquals(10.0, response.totalFee());
		verify(sessionRepository).save(any());
		verify(slotRepository).save(any());
	}
}