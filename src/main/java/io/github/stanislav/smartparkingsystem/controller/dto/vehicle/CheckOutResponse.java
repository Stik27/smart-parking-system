package io.github.stanislav.smartparkingsystem.controller.dto.vehicle;

import java.time.Instant;

public record CheckOutResponse(String licensePlate,
                               Instant entryTime,
                               Instant exitTime,
                               String totalDuration,
                               double totalFee) {
}
