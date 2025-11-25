package io.github.stanislav.smartparkingsystem.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.stanislav.smartparkingsystem.controller.dto.vehicle.CheckInRequest;
import io.github.stanislav.smartparkingsystem.domain.VehicleType;
import io.github.stanislav.smartparkingsystem.repository.VehicleRepository;
import io.github.stanislav.smartparkingsystem.repository.entity.VehicleEntity;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

	@Mock
	private VehicleRepository vehicleRepository;
	@InjectMocks
	private VehicleService vehicleService;

	@Test
	void findOrCreateVehicle_ShouldReturnExisting() {
		CheckInRequest req = new CheckInRequest(null, "AA1111BB", VehicleType.CAR, false);
		VehicleEntity expected = VehicleEntity.builder()
				.licensePlate("AA1111BB")
				.build();

		given(vehicleRepository.findByLicensePlate("AA1111BB"))
				.willReturn(Optional.of(expected));

		VehicleEntity result = vehicleService.findOrCreateVehicle(req);

		Assertions.assertEquals(expected, result);
		verify(vehicleRepository, never()).save(any());
	}

	@Test
	void findOrCreateVehicle_ShouldCreateNew_WhenNotFound() {
		CheckInRequest req = new CheckInRequest(null, "AA1111BB", VehicleType.CAR, false);

		given(vehicleRepository.findByLicensePlate("AA1111BB"))
				.willReturn(Optional.empty());

		given(vehicleRepository.save(any()))
				.willAnswer(invocation -> invocation.getArgument(0));

		VehicleEntity result = vehicleService.findOrCreateVehicle(req);

		Assertions.assertEquals("AA1111BB", result.getLicensePlate());
		Assertions.assertEquals(VehicleType.CAR, result.getVehicleType());
		verify(vehicleRepository).save(any());
	}
}