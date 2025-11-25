package io.github.stanislav.smartparkingsystem.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.stanislav.smartparkingsystem.repository.entity.VehicleEntity;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, UUID> {

	Optional<VehicleEntity> findByLicensePlate(String licensePlate);

}