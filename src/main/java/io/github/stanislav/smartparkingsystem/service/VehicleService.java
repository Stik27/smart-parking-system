package io.github.stanislav.smartparkingsystem.service;

import org.springframework.stereotype.Service;

import io.github.stanislav.smartparkingsystem.controller.dto.vehicle.CheckInRequest;
import io.github.stanislav.smartparkingsystem.repository.VehicleRepository;
import io.github.stanislav.smartparkingsystem.repository.entity.VehicleEntity;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VehicleService {
	private final VehicleRepository vehicleRepository;

	public VehicleEntity findOrCreateVehicle(CheckInRequest request) {
		return vehicleRepository
				.findByLicensePlate(request.licensePlate())
				.orElseGet(() -> createNewVehicle(request));
	}

	private VehicleEntity createNewVehicle(CheckInRequest request) {
		VehicleEntity vehicle = VehicleEntity.builder()
				.licensePlate(request.licensePlate())
				.vehicleType(request.vehicleType())
				.isHandicapped(request.isHandicapped())
				.build();
		return vehicleRepository.save(vehicle);
	}
}
