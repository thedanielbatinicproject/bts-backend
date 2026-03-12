package online.beneaththestars.btsbackend.models.dto.PlayerDTOs;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

public record PlayerRequest(
        @NotBlank
        String steamId,
        @NotBlank
        String username,
        Instant updatedAt
) {}
