package online.beneaththestars.btsbackend.models.dto.PuzzleDTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePuzzleRequest {
    @Min(value = 1, message = "Chapter number cannot be set to lower than 1!")
    @Max(value = 7, message = "Chapter number cannot be set to higher than 7!")
    private Integer chapterNumber; // nullable = "not provided"

    private String puzzleDescription; // nullable = "not provided"

    private Boolean puzzleActive; // nullable = "not provided"
}