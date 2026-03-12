package online.beneaththestars.btsbackend.controllers.adminAccess;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.PlayerDTOs.AdminPlayerSummary;
import online.beneaththestars.btsbackend.models.dto.PlayerDTOs.PlayerRequest;
import online.beneaththestars.btsbackend.services.Player.PlayerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/players")
@RequiredArgsConstructor
@Validated
public class AdminPlayerController {
    private final PlayerService playerService;

    @GetMapping
    public ResponseEntity<Page<PlayerRequest>> fetchAllPlayers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(playerService.getAllPlayers(pageable));
    }

    @GetMapping("/{steamId}")
    public ResponseEntity<AdminPlayerSummary> fetchPlayer(
            @PathVariable String steamId
    ) {
        return ResponseEntity.ok(playerService.getPlayerSummary(steamId));
    }

    @DeleteMapping("/{steamId}")
    public ResponseEntity<Void> deletePlayerAndEntries(
            @PathVariable String steamId
    ) {
        playerService.deletePlayerAndTimes(steamId);
        return ResponseEntity.noContent().build();
    }

}
