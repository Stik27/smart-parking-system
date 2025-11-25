package io.github.stanislav.smartparkingsystem.domain;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ParkingLevel {
	private UUID id;
	private String name;
	private List<ParkingSlot> slots;
}