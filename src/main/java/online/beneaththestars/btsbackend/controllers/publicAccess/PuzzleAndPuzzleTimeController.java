package online.beneaththestars.btsbackend.controllers.publicAccess;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.PuzzleTimeDTOs.PuzzleLeaderboardEntry;
import online.beneaththestars.btsbackend.models.entities.Puzzle;
import online.beneaththestars.btsbackend.services.Puzzle.PuzzleLeaderboardService;
import online.beneaththestars.btsbackend.services.Puzzle.PuzzleService;
import online.beneaththestars.btsbackend.services.Puzzle.PuzzleTimeService;
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

    @GetMapping("/{puzzleCode}/leaderboard")
    public Page<PuzzleLeaderboardEntry> getLeaderboard(
            @PathVariable @NotBlank @Size(max = 64) String puzzleCode,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(200) int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return puzzleTimeService.getLeaderboardPage(puzzleCode, pageable);
    }

    @GetMapping("/{puzzleCode}/leaderboard/top")
    public ResponseEntity<List<PuzzleLeaderboardEntry>> getLeaderboard(
            @PathVariable String puzzleCode,
            @RequestParam(defaultValue = "20") @Max(200)  int noOfEntries
    ) {
        return ResponseEntity.ok(puzzleTimeService.getTopN(puzzleCode, noOfEntries));
    }

    @GetMapping("/{puzzleCode}/leaderboard/around/{steamId}")
    public ResponseEntity<List<PuzzleLeaderboardEntry>> getLeaderboardAround(
            @PathVariable String puzzleCode,
            @PathVariable String steamId,
            @RequestParam(defaultValue = "5") @Max(50) int radius
    ) {
        return ResponseEntity.ok(puzzleTimeService.getLeaderboardAroundUser(puzzleCode, steamId, radius));
    }

}
