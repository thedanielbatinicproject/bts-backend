package online.beneaththestars.btsbackend.repo;

import online.beneaththestars.btsbackend.models.entities.Puzzle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PuzzleRepository extends JpaRepository<Puzzle, String> {
    Optional<Puzzle> findByPuzzleCode(String puzzleCode);
    Page<Puzzle> findAllByPuzzleChapterNumber(int chapterNumber, Pageable pageable);
    boolean existsByPuzzleCode(String puzzleCode);
}
