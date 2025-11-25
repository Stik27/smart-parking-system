package io.github.stanislav.smartparkingsystem.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.stanislav.smartparkingsystem.repository.entity.ParkingLevelEntity;

@Repository
public interface ParkingLevelRepository extends JpaRepository<ParkingLevelEntity, UUID> {

}