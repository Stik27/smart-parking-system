package io.github.stanislav.smartparkingsystem.domain;

import java.util.UUID;

import lombok.Data;

@Data
public class Vehicle {
	private UUID vehicleId;
	private String licensePlate;
	private VehicleType vehicleType;
	private boolean isHandicapped;
}