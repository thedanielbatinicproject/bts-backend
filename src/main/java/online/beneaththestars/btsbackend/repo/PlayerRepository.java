package online.beneaththestars.btsbackend.repo;

import online.beneaththestars.btsbackend.models.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findBySteamId(String steamId);

    Player getPlayersBySteamId(String steamId);

    boolean existsBySteamId(String steamId);
}
