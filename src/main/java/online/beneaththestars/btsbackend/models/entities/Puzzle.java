package online.beneaththestars.btsbackend.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Puzzle {
    @Id
    @Column(nullable = false, unique = true, length = 64)
    private String puzzleCode;

    @Min(1)
    @Max(7)
    @Column(nullable = false)
    private int puzzleChapterNumber;

    @Column(nullable = true, length = 2000)
    private String puzzleDescription;

    @Column(nullable = false)
    private boolean puzzleActive = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }
}