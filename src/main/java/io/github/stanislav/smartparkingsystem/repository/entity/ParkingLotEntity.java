package io.github.stanislav.smartparkingsystem.repository.entity;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parking_lots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingLotEntity {

	@Id
	@GeneratedValue
	private UUID id;

	@Column(nullable = false, length = 100)
	private String name;

	private String location;

	@OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ParkingLevelEntity> levels = new ArrayList<>();

	public void addLevel(ParkingLevelEntity level) {
		levels.add(level);
		level.setParkingLot(this);
	}

	public void removeLevel(ParkingLevelEntity level) {
		levels.remove(level);
		level.setParkingLot(null);
	}
}