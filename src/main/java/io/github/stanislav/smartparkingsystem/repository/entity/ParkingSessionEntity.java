package io.github.stanislav.smartparkingsystem.repository.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parking_sessions", indexes = {
		@Index(name = "idx_session_vehicle_id", columnList = "vehicle_id"),
		@Index(name = "idx_session_slot_id", columnList = "slot_id"),
		@Index(name = "idx_session_exit_time", columnList = "exit_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSessionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private UUID slotId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vehicle_id", nullable = false)
	private VehicleEntity vehicle;

	@Column(nullable = false, updatable = false)
	private LocalDateTime entryTime;

	@Column
	private LocalDateTime exitTime;

	@Column(precision = 10, scale = 2)
	private BigDecimal fee;

	public boolean isActive() {
		return exitTime == null;
	}
}