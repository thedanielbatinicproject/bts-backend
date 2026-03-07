package online.beneaththestars.btsbackend.services;

import jakarta.transaction.Transactional;
import online.beneaththestars.btsbackend.models.entities.Player;
import online.beneaththestars.btsbackend.repo.PlayerRepo;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    private final PlayerRepo playerRepo;

    public PlayerService(PlayerRepo playerRepo) {
        this.playerRepo = playerRepo;
    }
    @Transactional
    public Player addPlayer(String name) {
        Player p1 = new Player();
        p1.setPlayerName(name);
        playerRepo.save(p1);
        return p1;
    }

    public Player findPlayer(long id) {
        return playerRepo.findByPlayerId(id);
    }
}
