package online.beneaththestars.btsbackend.repo;

import online.beneaththestars.btsbackend.models.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepo extends JpaRepository<Player, Long> {
    Player findByPlayerId(long playerId);
}
