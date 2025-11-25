package io.github.stanislav.smartparkingsystem.repository.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.github.stanislav.smartparkingsystem.domain.SlotType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "parking_slots", indexes = {
		@Index(name = "idx_slot_level_id", columnList = "parking_level_id"),
		@Index(name = "idx_slot_occupied", columnList = "occupied"),
		@Index(name = "idx_slot_type", columnList = "slot_type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSlotEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false, length = 50)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private SlotType slotType;

	@Column(nullable = false)
	private boolean isHandicapped;

	@Column(nullable = false)
	private boolean isMaintenance;

	@Column(nullable = false)
	private boolean occupied;

	@Column(name = "current_vehicle_id")
	private UUID currentVehicleId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parking_level_id", nullable = false)
	private ParkingLevelEntity parkingLevel;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public boolean isAvailable() {
		return !occupied && !isMaintenance;
	}
}