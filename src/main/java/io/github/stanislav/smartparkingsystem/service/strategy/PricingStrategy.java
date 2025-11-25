package io.github.stanislav.smartparkingsystem.service.strategy;

import java.math.BigDecimal;
import java.time.Duration;

import io.github.stanislav.smartparkingsystem.domain.VehicleType;

public interface PricingStrategy {
	BigDecimal calculateFee(VehicleType vehicleType, Duration parkingDuration);

	String getStrategyName();
}