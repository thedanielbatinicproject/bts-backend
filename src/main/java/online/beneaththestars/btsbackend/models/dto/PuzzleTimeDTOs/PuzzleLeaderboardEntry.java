package online.beneaththestars.btsbackend.models.dto.PuzzleTimeDTOs;

import java.time.Instant;

public record PuzzleLeaderboardEntry(
        String steamId,
        String username,
        int timeMs,
        Instant updatedAt,
        long rank
) {}