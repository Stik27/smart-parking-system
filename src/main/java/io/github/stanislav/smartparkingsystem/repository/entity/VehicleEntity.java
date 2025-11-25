package io.github.stanislav.smartparkingsystem.repository.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.github.stanislav.smartparkingsystem.domain.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vehicles", indexes = {
		@Index(name = "idx_vehicle_license_plate", columnList = "license_plate", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID vehicleId;

	@Column(nullable = false, unique = true, length = 20)
	private String licensePlate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private VehicleType vehicleType;

	@Column(nullable = false)
	private boolean isHandicapped;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;
}