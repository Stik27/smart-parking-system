package io.github.stanislav.smartparkingsystem.service.strategy;

import java.math.BigDecimal;
import java.time.Duration;

import org.springframework.stereotype.Component;

import io.github.stanislav.smartparkingsystem.config.PricingConfig;
import io.github.stanislav.smartparkingsystem.domain.VehicleType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FlatRatePricingStrategy implements PricingStrategy {

	private final PricingConfig pricingConfig;

	@Override
	public BigDecimal calculateFee(VehicleType type, Duration duration) {
		long hours = duration.toHours();
		if(duration.toMinutesPart() > 0) hours++;

		BigDecimal flat = pricingConfig.getFlatRates().get("flatFee");
		BigDecimal rate = pricingConfig.getFlatRates().get("hourlyRate");

		return flat.add(rate.multiply(BigDecimal.valueOf(hours)));
	}

	@Override
	public String getStrategyName() {
		return "FLAT_RATE";
	}
}