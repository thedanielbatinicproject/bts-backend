package online.beneaththestars.btsbackend.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNewsRequest {
    private String title;
    private String description;
    private String imageUrl;
}
