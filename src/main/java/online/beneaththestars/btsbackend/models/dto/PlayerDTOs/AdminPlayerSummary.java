package online.beneaththestars.btsbackend.models.dto.PlayerDTOs;

import java.time.Instant;

public record AdminPlayerSummary(
        String steamId,
        String username,
        Instant updatedAt,
        long timesCount
) {
}
