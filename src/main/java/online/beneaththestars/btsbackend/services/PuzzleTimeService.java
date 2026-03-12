package online.beneaththestars.btsbackend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.PuzzleTimeDTOs.PuzzleLeaderboardEntry;
import online.beneaththestars.btsbackend.models.entities.PuzzleTime;
import online.beneaththestars.btsbackend.repo.PuzzleRepository;
import online.beneaththestars.btsbackend.repo.PuzzleTimeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PuzzleTimeService {
    private final PuzzleRepository puzzleRepository;
    private final PuzzleTimeRepository puzzleTimeRepository;

    public void deletePuzzleTimeEntry(String puzzleCode, String steamId) {
        if (!puzzleRepository.existsById(puzzleCode)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Puzzle with code " + puzzleCode + " was not found");
        }

        boolean entryExists = puzzleTimeRepository.existsByPuzzle_PuzzleCodeAndPlayer_SteamId(puzzleCode, steamId);
        if (!entryExists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Time entry for puzzle " + puzzleCode + " and steamId " + steamId + " was not found");
        }

        puzzleTimeRepository.deleteByPuzzle_PuzzleCodeAndPlayer_SteamId(puzzleCode, steamId);
    }

    public Page<PuzzleTime> getAllTimesForPuzzle(String puzzleCode, Pageable pageable) {
        if (!puzzleRepository.existsById(puzzleCode)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "There is no puzzle with code " + puzzleCode);
        }
        return puzzleTimeRepository.findAllByPuzzle_PuzzleCode(puzzleCode, pageable);
    }

    public List<PuzzleLeaderboardEntry> getTopN(String puzzleCode, int limit) {
        if (!puzzleRepository.existsById(puzzleCode)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Puzzle with code " + puzzleCode + " was not found");
        }

        Pageable pageable = PageRequest.of(0, limit);
        Page<PuzzleTime> page = puzzleTimeRepository
                .findAllByPuzzle_PuzzleCodeOrderByTimeMsAscUpdatedAtAsc(puzzleCode, pageable);

        List<PuzzleTime> times = page.getContent();
        List<PuzzleLeaderboardEntry> out = new ArrayList<>(times.size());

        for (int i = 0; i < times.size(); i++) {
            PuzzleTime pt = times.get(i);
            out.add(new PuzzleLeaderboardEntry(
                    pt.getPlayer().getSteamId(),
                    pt.getPlayer().getUsername(),
                    pt.getTimeMs(),
                    pt.getUpdatedAt(),
                    i + 1L
            ));
        }

        return out;
    }
}
