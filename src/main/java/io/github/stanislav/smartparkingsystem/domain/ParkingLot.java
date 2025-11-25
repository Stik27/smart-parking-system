package io.github.stanislav.smartparkingsystem.domain;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ParkingLot {
	private UUID id;
	private String name;
	private String location;
	private List<ParkingLevel> levels;
}
