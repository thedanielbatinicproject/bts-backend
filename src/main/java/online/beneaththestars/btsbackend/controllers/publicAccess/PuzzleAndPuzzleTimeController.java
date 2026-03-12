package online.beneaththestars.btsbackend.controllers.publicAccess;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.PuzzleTimeDTOs.PuzzleLeaderboardEntry;
import online.beneaththestars.btsbackend.models.entities.Puzzle;
import online.beneaththestars.btsbackend.models.entities.PuzzleTime;
import online.beneaththestars.btsbackend.services.PuzzleLeaderboardService;
import online.beneaththestars.btsbackend.services.PuzzleService;
import online.beneaththestars.btsbackend.services.PuzzleTimeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/puzzles")
@RequiredArgsConstructor
@Validated
public class PuzzleAndPuzzleTimeController {
    private final PuzzleTimeService puzzleTimeService;
    private final PuzzleService puzzleService;
    private final PuzzleLeaderboardService puzzleLeaderboardService;

    @GetMapping
    public ResponseEntity<Page<Puzzle>> getAllPuzzles(
            @RequestParam @Min(1) @Max(7) int chapterNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Puzzle> result = puzzleService.getPuzzles(chapterNumber, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{puzzleCode}")
    public ResponseEntity<Puzzle> getAllPuzzles(
            @PathVariable String puzzleCode
    ) {
        return ResponseEntity.ok(puzzleService.getPuzzle(puzzleCode));
    }

    @GetMapping("/{chapterNumber}/puzzles")
    public ResponseEntity<Page<Puzzle>> getAllPuzzlesInsideChapter(
            @PathVariable int chapterNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Puzzle> result = puzzleService.getPuzzles(chapterNumber, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{puzzleCode}/leaderboard/top")
    public ResponseEntity<List<PuzzleLeaderboardEntry>> getLeaderboard(
            @PathVariable String puzzleCode,
            @RequestParam(defaultValue = "20") @Max(200)  int noOfEntries
    ) {
        return ResponseEntity.ok(puzzleTimeService.getTopN(puzzleCode, noOfEntries));
    }

}
