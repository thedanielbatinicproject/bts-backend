package online.beneaththestars.btsbackend.models.dto.PuzzleDTOs;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePuzzleRequest {

    @NotBlank(message = "Puzzle code is required for creating new puzzle!")
    @Size(max = 64, message = "Puzzle code must be at most 64 characters long.")
    private String puzzleCode;

    @Min(value = 1, message = "Chapter number cannot be set to lower than 1!")
    @Max(value = 7, message = "Chapter number cannot be set to higher than 7!")
    private int chapterNumber;

    private String puzzleDescription;

    private boolean puzzleActive = true;
}