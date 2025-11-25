package io.github.stanislav.smartparkingsystem.service.strategy;

import java.math.BigDecimal;
import java.time.Duration;

import org.springframework.stereotype.Component;

import io.github.stanislav.smartparkingsystem.config.PricingConfig;
import io.github.stanislav.smartparkingsystem.domain.VehicleType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HourlyPricingStrategy implements PricingStrategy {

	private final PricingConfig pricingConfig;

	@Override
	public BigDecimal calculateFee(VehicleType type, Duration duration) {
		long minutes = duration.toMinutes();

		long hours = (long) Math.ceil(minutes / 60.0);
		if(hours == 0) hours = 1;

		BigDecimal rate = pricingConfig.getHourly().get(type.name());
		return rate.multiply(BigDecimal.valueOf(hours));
	}

	@Override
	public String getStrategyName() {
		return "HOURLY";
	}
}