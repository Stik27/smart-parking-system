package io.github.stanislav.smartparkingsystem.config;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import io.github.stanislav.smartparkingsystem.service.strategy.PricingStrategy;
import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "parking.pricing")
public class PricingConfig {
	private String strategy;
	private Map<String, BigDecimal> hourly;
	private Map<String, BigDecimal> flatRates;

	@Bean
	public PricingStrategy pricingStrategy(List<PricingStrategy> strategies) {

		return strategies.stream()
				.filter(s -> s.getStrategyName().equalsIgnoreCase(strategy))
				.findFirst()
				.orElseThrow(() ->
						new IllegalStateException("No pricing strategy found: " + strategy));
	}
}