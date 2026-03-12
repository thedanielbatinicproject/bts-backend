package online.beneaththestars.btsbackend.models.dto.PuzzleTimeDTOs;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubmitPuzzleTimeRequest {
    @NotBlank(message = "SteamId must not be blank in creating new time record for puzzle!")
    private String steamId;
    @NotBlank(message = "You must send Steam username when submitting puzzle time.")
    private String username;
    @Min(1)
    @Max(86_400_000)
    private long timeMs;
    @NotNull
    private Instant clientTimestamp;
    @NotBlank(message = "New time record request MUST include valid signature!")
    private String signature;
}
