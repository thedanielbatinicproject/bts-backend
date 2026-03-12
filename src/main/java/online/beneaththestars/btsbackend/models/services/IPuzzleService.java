package online.beneaththestars.btsbackend.models.services;

import online.beneaththestars.btsbackend.models.dto.PuzzleDTOs.CreatePuzzleRequest;
import online.beneaththestars.btsbackend.models.dto.PuzzleDTOs.UpdatePuzzleRequest;
import online.beneaththestars.btsbackend.models.entities.Puzzle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPuzzleService {
    public Puzzle createNewPuzzle(CreatePuzzleRequest createPuzzleRequest);
    public void deletePuzzle(String puzzleCode);
    public Puzzle editPuzzle(String puzzleCode, UpdatePuzzleRequest updatePuzzleRequest);
    Page<Puzzle> getPuzzles(Integer chapterNumber, Pageable pageable);
    Puzzle getPuzzle(String puzzleCode);
}
