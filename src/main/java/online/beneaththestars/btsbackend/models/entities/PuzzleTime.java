package online.beneaththestars.btsbackend.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(
        name = "puzzle_times",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_puzzle_times_puzzle_player", columnNames = {"puzzle_code", "player_steam_id"})
        },
        indexes = {
                @Index(name = "idx_puzzle_times_puzzle_time", columnList = "puzzle_code,time_ms,updated_at"),
                @Index(name = "idx_puzzle_times_player", columnList = "player_steam_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK -> Puzzle(puzzleCode)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "puzzle_code", nullable = false)
    private Puzzle puzzle;

    // FK -> Player(steamId)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "player_steam_id", nullable = false)
    private Player player;

    @Column(nullable = false)
    private int timeMs;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column()
    private Instant clientTimestamp;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}