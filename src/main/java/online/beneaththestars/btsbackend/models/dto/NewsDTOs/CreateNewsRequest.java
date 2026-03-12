package online.beneaththestars.btsbackend.models.dto.NewsDTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewsRequest {
    @NotBlank(message = "News must have title in create news request!")
    private String title;
    private String description;
    private String imageUrl;
}
