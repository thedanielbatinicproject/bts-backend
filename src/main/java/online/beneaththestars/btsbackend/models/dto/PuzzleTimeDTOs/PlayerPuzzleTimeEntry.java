package online.beneaththestars.btsbackend.models.dto.PuzzleTimeDTOs;

import java.time.Instant;

public record PlayerPuzzleTimeEntry(
        String puzzleCode,
        int chapterNumber,
        int timeMs,
        Instant updatedAt
) {}
