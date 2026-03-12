package online.beneaththestars.btsbackend.services.Player;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.PlayerDTOs.AdminPlayerSummary;
import online.beneaththestars.btsbackend.models.dto.PlayerDTOs.PlayerRequest;
import online.beneaththestars.btsbackend.models.entities.Player;
import online.beneaththestars.btsbackend.repo.PlayerRepository;
import online.beneaththestars.btsbackend.repo.PuzzleTimeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {
    private final PlayerRepository playerRepository;
    private final PuzzleTimeRepository puzzleTimeRepository;

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

    public static PlayerRequest playerToPlayerRequest(Player player) {
        return new PlayerRequest(player.getSteamId(), player.getUsername(), player.getUpdatedAt());
    }

    public Page<PlayerRequest> getAllPlayers(Pageable pageable) {
        return playerRepository
                .findAll(pageable)
                .map(PlayerService::playerToPlayerRequest);
    }

    public AdminPlayerSummary getPlayerSummary(String steamId) {
        Player player = playerRepository.findBySteamId(steamId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Requested player with steamId " + steamId + " was not found!")
        );

        long timesCount = puzzleTimeRepository.countByPlayer_SteamId(steamId);

        return new AdminPlayerSummary(
                player.getSteamId(),
                player.getUsername(),
                player.getUpdatedAt(),
                timesCount
        );
    }

    public void deletePlayerAndTimes(String steamId) {
        Player player = playerRepository.findBySteamId(steamId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Requested player with steamId " + steamId + " was not found!")
        );

        // delete times first
        puzzleTimeRepository.deleteAllByPlayer_SteamId(steamId);

        playerRepository.delete(player);
    }

}
