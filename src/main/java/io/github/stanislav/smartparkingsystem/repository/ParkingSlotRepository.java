package io.github.stanislav.smartparkingsystem.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.stanislav.smartparkingsystem.domain.SlotType;
import io.github.stanislav.smartparkingsystem.repository.entity.ParkingSlotEntity;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlotEntity, UUID> {

	@Query("""
			SELECT ps
			FROM ParkingSlotEntity ps
			WHERE ps.parkingLevel.parkingLot.id = :lotId
			  AND ps.occupied = false
			  AND ps.isMaintenance = false
			  AND ps.slotType = :slotType
			  AND ps.isHandicapped = false
			""")
	List<ParkingSlotEntity> findAvailableSlotsByLotIdAndType(UUID lotId, SlotType slotType);

	@Query("""
			SELECT ps
			FROM ParkingSlotEntity ps
			WHERE ps.parkingLevel.parkingLot.id = :lotId
			  AND ps.occupied = false
			  AND ps.isMaintenance = false
			  AND ps.slotType = :slotType
			  AND ps.isHandicapped = true
			""")
	List<ParkingSlotEntity> findAvailableHandicappedSlotsByLotIdAndType(
			@Param("lotId") UUID lotId,
			@Param("slotType") SlotType slotType
	);
}