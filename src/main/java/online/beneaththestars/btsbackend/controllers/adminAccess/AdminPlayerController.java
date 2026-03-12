package online.beneaththestars.btsbackend.controllers.adminAccess;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.PlayerDTOs.AdminPlayerSummary;
import online.beneaththestars.btsbackend.models.dto.PlayerDTOs.PlayerRequest;
import online.beneaththestars.btsbackend.services.Admin.AdminAuthService;
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
    private final AdminAuthService adminAuthService;

    @GetMapping
    public ResponseEntity<Page<PlayerRequest>> fetchAllPlayers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(playerService.getAllPlayers(pageable));
    }

    @GetMapping("/{steamId}")
    public ResponseEntity<AdminPlayerSummary> fetchPlayer(
            @PathVariable String steamId,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        return ResponseEntity.ok(playerService.getPlayerSummary(steamId));
    }

    @DeleteMapping("/{steamId}")
    public ResponseEntity<Void> deletePlayerAndEntries(
            @PathVariable String steamId,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireLoggedInAdmin(httpRequest);
        playerService.deletePlayerAndTimes(steamId);
        return ResponseEntity.noContent().build();
    }

}
