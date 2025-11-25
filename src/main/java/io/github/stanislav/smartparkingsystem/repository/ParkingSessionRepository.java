package io.github.stanislav.smartparkingsystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.github.stanislav.smartparkingsystem.repository.entity.ParkingSessionEntity;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSessionEntity, UUID> {

	@Query("""
			SELECT ps
			FROM ParkingSessionEntity ps
			WHERE ps.vehicle.vehicleId = :vehicleId
			  AND ps.exitTime IS NULL
			""")
	Optional<ParkingSessionEntity> findActiveSessionByVehicleId(UUID vehicleId);

	@Query("""
			SELECT ps
			FROM ParkingSessionEntity ps
			WHERE ps.slotId = :slotId
			  AND ps.exitTime IS NULL
			""")
	Optional<ParkingSessionEntity> findActiveSessionBySlotId(UUID slotId);

	@Query("""
			SELECT ps
			FROM ParkingSessionEntity ps
			WHERE ps.exitTime IS NULL
			""")
	List<ParkingSessionEntity> findAllActiveSessions();


	boolean existsByVehicleVehicleIdAndExitTimeIsNull(UUID vehicleId);
}