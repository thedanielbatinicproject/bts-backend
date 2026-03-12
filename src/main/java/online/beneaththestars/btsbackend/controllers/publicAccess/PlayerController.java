package online.beneaththestars.btsbackend.controllers.publicAccess;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.PlayerDTOs.PlayerRequest;
import online.beneaththestars.btsbackend.models.dto.PuzzleTimeDTOs.PlayerPuzzleTimeEntry;
import online.beneaththestars.btsbackend.models.dto.PuzzleTimeDTOs.SubmitPuzzleTimeRequest;
import online.beneaththestars.btsbackend.services.Player.PlayerService;
import online.beneaththestars.btsbackend.services.Puzzle.PuzzleService;
import online.beneaththestars.btsbackend.services.Puzzle.PuzzleTimeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
@Validated
public class PlayerController {
    private final PlayerService playerService;
    private final PuzzleService puzzleService;
    private final PuzzleTimeService puzzleTimeService;

    @GetMapping("/{steamId}")
    public ResponseEntity<PlayerRequest> fetchPlayer(
            @PathVariable String steamId
    ) {
        return ResponseEntity.ok(PlayerService.playerToPlayerRequest(playerService.getPlayer(steamId)));
    }

    @GetMapping("/{steamId}/times")
    public ResponseEntity<Page<PlayerPuzzleTimeEntry>> getPlayerTimes(
            @PathVariable String steamId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlayerPuzzleTimeEntry> result = puzzleTimeService.getPlayerTimes(steamId, pageable);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{puzzleCode}/time")
    public ResponseEntity<Void> submitPuzzleTime(
            @PathVariable String puzzleCode,
            @Valid @RequestBody SubmitPuzzleTimeRequest submitReq
    ) {
        PuzzleTimeService.UpsertResult result = puzzleTimeService.submitTime(puzzleCode, submitReq);

        return result.created()
                ? ResponseEntity.status(HttpStatus.CREATED).build()
                : ResponseEntity.ok().build();
    }
}
