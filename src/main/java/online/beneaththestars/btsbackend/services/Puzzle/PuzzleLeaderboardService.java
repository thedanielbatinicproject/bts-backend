package online.beneaththestars.btsbackend.services.Puzzle;

import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.PuzzleTimeDTOs.PuzzleLeaderboardEntry;
import online.beneaththestars.btsbackend.models.entities.PuzzleTime;
import online.beneaththestars.btsbackend.repo.PuzzleRepository;
import online.beneaththestars.btsbackend.repo.PuzzleTimeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.PageImpl;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PuzzleLeaderboardService {
    private final PuzzleRepository puzzleRepository;
    private final PuzzleTimeRepository puzzleTimeRepository;

    public Page<PuzzleLeaderboardEntry> getLeaderboard(String puzzleCode, Pageable pageable) {
        if (!puzzleRepository.existsById(puzzleCode)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Puzzle with code " + puzzleCode + " was not found");
        }

        Page<PuzzleTime> timesPage =
                puzzleTimeRepository.findAllByPuzzle_PuzzleCodeOrderByTimeMsAscUpdatedAtAsc(puzzleCode, pageable);

        long startRank = (long) pageable.getPageNumber() * pageable.getPageSize();

        List<PuzzleLeaderboardEntry> content = new ArrayList<>(timesPage.getNumberOfElements());
        List<PuzzleTime> times = timesPage.getContent();

        for (int i = 0; i < times.size(); i++) {
            PuzzleTime pt = times.get(i);
            content.add(new PuzzleLeaderboardEntry(
                    pt.getPlayer().getSteamId(),
                    pt.getPlayer().getUsername(),
                    pt.getTimeMs(),
                    pt.getUpdatedAt(),
                    startRank + i + 1
            ));
        }

        return new PageImpl<>(content, pageable, timesPage.getTotalElements());
    }
}