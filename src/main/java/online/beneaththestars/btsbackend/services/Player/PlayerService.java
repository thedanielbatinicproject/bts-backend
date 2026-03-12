package online.beneaththestars.btsbackend.services.Player;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.PlayerDTOs.PlayerRequest;
import online.beneaththestars.btsbackend.models.entities.Player;
import online.beneaththestars.btsbackend.repo.PlayerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {
    private final PlayerRepository playerRepository;

    public boolean playerExists(String steamId) {
        if (!playerRepository.existsBySteamId(steamId)) throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Requested player with steamId " + steamId + " was not found!"
        );
        return true;
    }

    public Player getPlayer(String steamId) {
        if (!playerRepository.existsBySteamId(steamId)) throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Requested player with steamId " + steamId + " was not found!"
        );
        return playerRepository.getPlayersBySteamId(steamId);
    }

    public static PlayerRequest playerToRequest (Player player) {
        return new PlayerRequest(player.getSteamId(), player.getUsername(), player.getUpdatedAt());
    }
}
