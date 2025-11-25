package io.github.stanislav.smartparkingsystem.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class ParkingSession {
	private UUID id;
	private UUID slotId;
	private Vehicle vehicle;
	private LocalDateTime entryTime;
	private LocalDateTime exitTime;
	private BigDecimal fee;
}