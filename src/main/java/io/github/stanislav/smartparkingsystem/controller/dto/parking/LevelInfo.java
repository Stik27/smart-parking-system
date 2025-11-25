package io.github.stanislav.smartparkingsystem.controller.dto.parking;

import java.util.List;
import java.util.UUID;

public record LevelInfo(UUID id,
                        String name,
                        int totalSlots,
                        int availableSlots,
                        List<SlotInfo> slots) {
}
