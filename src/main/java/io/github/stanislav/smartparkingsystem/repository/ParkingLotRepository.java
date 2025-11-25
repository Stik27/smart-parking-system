package io.github.stanislav.smartparkingsystem.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.stanislav.smartparkingsystem.repository.entity.ParkingLotEntity;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLotEntity, UUID> {

}