package online.beneaththestars.btsbackend.services.Puzzle;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.PuzzleDTOs.CreatePuzzleRequest;
import online.beneaththestars.btsbackend.models.dto.PuzzleDTOs.UpdatePuzzleRequest;
import online.beneaththestars.btsbackend.models.entities.Puzzle;
import online.beneaththestars.btsbackend.models.services.IPuzzleService;
import online.beneaththestars.btsbackend.repo.PuzzleRepository;
import online.beneaththestars.btsbackend.repo.PuzzleTimeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
@Transactional
public class PuzzleService implements IPuzzleService {
    private final PuzzleRepository puzzleRepository;
    private final PuzzleTimeRepository puzzleTimeRepository;
    public Puzzle createNewPuzzle(CreatePuzzleRequest request) {
        if (puzzleRepository.existsById(request.getPuzzleCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Puzzle with code " + request.getPuzzleCode() + " already exists");
        }

        Puzzle puzzle = new Puzzle(
                request.getPuzzleCode(),
                request.getChapterNumber(),
                request.getPuzzleDescription(),
                request.isPuzzleActive(),
                null,
                null
        );
        return puzzleRepository.save(puzzle);
    }

    public void deletePuzzle(String puzzleCode) {
        if (!puzzleRepository.existsById(puzzleCode)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Puzzle with code " + puzzleCode + " was not found");
        }
        puzzleTimeRepository.deleteAllByPuzzle_PuzzleCode(puzzleCode);
        puzzleRepository.deleteById(puzzleCode);
    }

    public Puzzle editPuzzle(String puzzleCode, UpdatePuzzleRequest req) {
        Puzzle puzzle = puzzleRepository.findById(puzzleCode).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Puzzle with code " + puzzleCode + " was not found")
        );

        boolean anyChange = false;

        if (req.getPuzzleDescription() != null) {
            puzzle.setPuzzleDescription(req.getPuzzleDescription());
            anyChange = true;
        }
        if (req.getPuzzleActive() != null) {
            puzzle.setPuzzleActive(req.getPuzzleActive());
            anyChange = true;
        }
        if (req.getChapterNumber() != null) {
            puzzle.setPuzzleChapterNumber(req.getChapterNumber());
            anyChange = true;
        }
        if (!anyChange) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No fields provided to update puzzle!");
        }

        return puzzleRepository.save(puzzle);
    }

    public Puzzle getPuzzle(String puzzleCode) {
        return puzzleRepository.findById(puzzleCode).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Puzzle with code " + puzzleCode + " was not found")
        );
    }

    public Page<Puzzle> getPuzzles(Integer chapterNumber, Pageable pageable) {
        return (chapterNumber == null)
                ? puzzleRepository.findAll(pageable)
                : puzzleRepository.findAllByPuzzleChapterNumber(chapterNumber, pageable);
    }
}