package online.beneaththestars.btsbackend.controllers.adminAccess;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.PuzzleDTOs.CreatePuzzleRequest;
import online.beneaththestars.btsbackend.models.dto.PuzzleDTOs.UpdatePuzzleRequest;
import online.beneaththestars.btsbackend.models.entities.Puzzle;
import online.beneaththestars.btsbackend.services.AdminAuthService;
import online.beneaththestars.btsbackend.services.PuzzleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/admin/puzzles")
public class AdminPuzzleController {
    private final PuzzleService puzzleService;
    private final AdminAuthService adminAuthService;

    @PostMapping
    public ResponseEntity<Puzzle> addNewPuzzle(@Valid @RequestBody CreatePuzzleRequest request,
                                               HttpServletRequest httpRequest) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        Puzzle created = puzzleService.createNewPuzzle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<Puzzle>> getPuzzles(
            @RequestParam(required = false) Integer chapterNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        Pageable pageable = PageRequest.of(page, size);
        Page<Puzzle> result = puzzleService.getPuzzles(chapterNumber, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{puzzleCode}")
    public ResponseEntity<Puzzle> getPuzzle(
            @PathVariable String puzzleCode,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        return ResponseEntity.ok(puzzleService.getPuzzle(puzzleCode));
    }

    @PatchMapping("/{puzzleCode}")
    public ResponseEntity<Puzzle> editPuzzle(
            @PathVariable String puzzleCode,
            @Valid @RequestBody UpdatePuzzleRequest updatePuzzleRequest,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        return ResponseEntity.ok(puzzleService.editPuzzle(puzzleCode, updatePuzzleRequest));
    }

    @DeleteMapping("/{puzzleCode}")
    public ResponseEntity<Void> deletePuzzle(
            @PathVariable String puzzleCode,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        puzzleService.deletePuzzle(puzzleCode);
        return ResponseEntity.noContent().build();
    }

}
