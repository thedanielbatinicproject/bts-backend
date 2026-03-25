package online.beneaththestars.btsbackend.services.Puzzle;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.PlayerDTOs.PlayerRequest;
import online.beneaththestars.btsbackend.models.dto.PuzzleTimeDTOs.PlayerPuzzleTimeEntry;
import online.beneaththestars.btsbackend.models.dto.PuzzleTimeDTOs.PuzzleLeaderboardEntry;
import online.beneaththestars.btsbackend.models.dto.PuzzleTimeDTOs.SubmitPuzzleTimeRequest;
import online.beneaththestars.btsbackend.models.entities.Player;
import online.beneaththestars.btsbackend.models.entities.Puzzle;
import online.beneaththestars.btsbackend.models.entities.PuzzleTime;
import online.beneaththestars.btsbackend.repo.PlayerRepository;
import online.beneaththestars.btsbackend.repo.PuzzleRepository;
import online.beneaththestars.btsbackend.repo.PuzzleTimeRepository;
import online.beneaththestars.btsbackend.services.GameSignatureService;
import online.beneaththestars.btsbackend.services.Player.PlayerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PuzzleTimeService {

    public record UpsertResult(PuzzleTime saved, boolean created) {}

    private final PuzzleRepository puzzleRepository;
    private final PuzzleTimeRepository puzzleTimeRepository;
    private final PlayerService playerService;
    private final PlayerRepository playerRepository;
    private final GameSignatureService gameSignatureService;

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

    public Page<PuzzleLeaderboardEntry> getLeaderboardPage(String puzzleCode, Pageable pageable) {
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

    public List<PuzzleLeaderboardEntry> getLeaderboardAroundUser(String puzzleCode, String steamId, int radius) {
        if (radius < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "radius must be >= 0");
        }
        if (radius > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "radius must be <= 50");
        }

        if (!puzzleRepository.existsById(puzzleCode)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Puzzle with code " + puzzleCode + " was not found");
        }
        PuzzleTime target = puzzleTimeRepository
                .findByPuzzle_PuzzleCodeAndPlayer_SteamId(puzzleCode, steamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Time entry for puzzle " + puzzleCode + " and steamId " + steamId + " was not found"));

        List<PuzzleTime> all = puzzleTimeRepository
                .findAllByPuzzle_PuzzleCodeOrderByTimeMsAscUpdatedAtAsc(puzzleCode, Pageable.unpaged())
                .toList();

        int targetIndex = -1;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(target.getId())) {
                targetIndex = i;
                break;
            }
        }
        if (targetIndex == -1) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Leaderboard entry could not be located after sorting");
        }

        int from = Math.max(0, targetIndex - radius);
        int toExclusive = Math.min(all.size(), targetIndex + radius + 1);

        List<PuzzleLeaderboardEntry> out = new java.util.ArrayList<>(toExclusive - from);
        for (int i = from; i < toExclusive; i++) {
            PuzzleTime pt = all.get(i);
            out.add(new PuzzleLeaderboardEntry(
                    pt.getPlayer().getSteamId(),
                    pt.getPlayer().getUsername(),
                    pt.getTimeMs(),
                    pt.getUpdatedAt(),
                    i + 1L // rank is 1-based
            ));
        }

        return out;
    }

    public Page<PlayerPuzzleTimeEntry> getPlayerTimes(String steamId, Pageable pageable) {
        playerService.playerExists(steamId);

        Page<PuzzleTime> page = puzzleTimeRepository.findAllByPlayer_SteamId(steamId, pageable);

        return page.map(pt -> new PlayerPuzzleTimeEntry(
                pt.getPuzzle().getPuzzleCode(),
                pt.getPuzzle().getPuzzleChapterNumber(),
                pt.getTimeMs(),
                pt.getUpdatedAt()
        ));
    }


    public UpsertResult submitTime(String puzzleCode, SubmitPuzzleTimeRequest submitReq) {
        Puzzle puzzle = puzzleRepository.findById(puzzleCode).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Puzzle with code " + puzzleCode + " was not found")
        );

        long now = Instant.now().toEpochMilli();
        long ts = submitReq.getClientTimestamp().toEpochMilli();
        if (ts > now + 5 * 60000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client timestamp too far in the future, entry denied!");
        }
        if (ts < now - (long)30 * 24 * 60 * 60000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client timestamp too old, time entry denied!");
        }

        gameSignatureService.verifyOrThrow(
                submitReq.getSteamId(),
                puzzleCode,
                submitReq.getTimeMs(),
                submitReq.getClientTimestamp().toEpochMilli(),
                submitReq.getSignature()
        );

        Player player = playerRepository.findBySteamId(submitReq.getSteamId()).orElseGet(() -> {
            Player p = new Player();
            p.setSteamId(submitReq.getSteamId());
            p.setUsername(submitReq.getUsername());
            return playerRepository.save(p);
        });

        if (submitReq.getUsername() != null
                && !submitReq.getUsername().isBlank()
                && !submitReq.getUsername().equals(player.getUsername())) {
            player.setUsername(submitReq.getUsername());
            playerRepository.save(player);
        }

        PuzzleTime existing = puzzleTimeRepository
                .findByPuzzle_PuzzleCodeAndPlayer_SteamId(puzzleCode, submitReq.getSteamId())
                .orElse(null);

        boolean created;
        PuzzleTime toSave;

        if (existing == null) {
            created = true;
            toSave = new PuzzleTime();
            toSave.setPuzzle(puzzle);
            toSave.setPlayer(player);
        } else {
            created = false;
            toSave = existing;
        }

        toSave.setTimeMs((int) submitReq.getTimeMs());
        toSave.setClientTimestamp(submitReq.getClientTimestamp());

        PuzzleTime saved = puzzleTimeRepository.save(toSave);
        return new UpsertResult(saved, created);
    }
}
