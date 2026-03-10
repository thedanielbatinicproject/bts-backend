package online.beneaththestars.btsbackend.models.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePuzzleRequest {
    @NotNull(message = "Puzzle code is required for creating new puzzle!")
    private Long puzzleCode;
    @Min(value = 1, message = "Chapter number cannot be set to lower than 1!")
    @Max(value = 7, message = "Chapter number cannot be set to higher than 7")
    private int chapterNumber;
    private String puzzleDescription;
    private boolean puzzleActive = true;
}
