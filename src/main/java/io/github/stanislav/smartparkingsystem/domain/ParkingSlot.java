package io.github.stanislav.smartparkingsystem.domain;

import java.util.UUID;

import lombok.Data;

@Data
public class ParkingSlot {
	private UUID id;
	private String name;
	SlotType slotType;
	private boolean isHandicapped;
	private boolean isMaintenance;
	private boolean occupied;
	private UUID currentVehicleId;
}
