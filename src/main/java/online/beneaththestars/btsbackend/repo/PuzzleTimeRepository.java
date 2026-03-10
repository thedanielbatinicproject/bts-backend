package online.beneaththestars.btsbackend.repo;

import online.beneaththestars.btsbackend.models.entities.Puzzle;
import online.beneaththestars.btsbackend.models.entities.PuzzleTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PuzzleTimeRepository extends JpaRepository<PuzzleTime, Long> {
    Optional<PuzzleTime> findByPuzzle_PuzzleCodeAndPlayer_SteamId(String puzzleCode, String steamId);
    Page<PuzzleTime> findAllByPuzzle_PuzzleCode(String puzzleCode, Pageable pageable);
    Page<PuzzleTime> findAllByPlayer_SteamId(String steamId, Pageable pageable);
    void deleteByPuzzle_PuzzleCodeAndPlayer_SteamId(String puzzleCode, String playerId);
    void deleteAllByPlayer_SteamId(String steamId);
}
