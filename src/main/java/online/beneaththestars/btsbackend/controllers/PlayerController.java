package online.beneaththestars.btsbackend.controllers;

import online.beneaththestars.btsbackend.models.Player;
import online.beneaththestars.btsbackend.services.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/player")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/add")
    public ResponseEntity<Player> addPlayer(@RequestParam String name) {
        return ResponseEntity.ok(playerService.addPlayer(name));
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable long id) {
        return ResponseEntity.ok(playerService.findPlayer(id));
    }
}
