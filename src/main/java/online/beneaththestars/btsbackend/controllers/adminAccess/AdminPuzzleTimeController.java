package online.beneaththestars.btsbackend.controllers.adminAccess;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.entities.PuzzleTime;
import online.beneaththestars.btsbackend.services.Admin.AdminAuthService;
import online.beneaththestars.btsbackend.services.Puzzle.PuzzleTimeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/admin/puzzles")
public class AdminPuzzleTimeController {
    private final PuzzleTimeService puzzleTimeService;
    private final AdminAuthService adminAuthService;

    @DeleteMapping("/{puzzleCode}/times/{steamId}")
    public ResponseEntity<Void> deletePuzzleTimeEntryForSteamId(
            @PathVariable @Valid String puzzleCode,
            @PathVariable @Valid String steamId,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        puzzleTimeService.deletePuzzleTimeEntry(puzzleCode, steamId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{puzzleCode}/times")
    public ResponseEntity<Page<PuzzleTime>> getAllPuzzleTimes(
            @PathVariable String puzzleCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireSuperAdmin(httpRequest);

        Pageable pageable = PageRequest.of(page, size);
        Page<PuzzleTime> result = puzzleTimeService.getAllTimesForPuzzle(puzzleCode, pageable);
        return ResponseEntity.ok(result);
    }
}
