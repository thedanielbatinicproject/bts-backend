package online.beneaththestars.btsbackend.models.dto.AdminDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private Boolean enabled;

}
