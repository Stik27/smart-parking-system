package io.github.stanislav.smartparkingsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "secure")
public class SecureProperties {
	private String jwtSecret;

}
