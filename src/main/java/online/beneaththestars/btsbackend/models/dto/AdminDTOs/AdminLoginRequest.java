package online.beneaththestars.btsbackend.models.dto.AdminDTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginRequest {
    @NotBlank(message = "Username for admin login is required!")
    private String username;
    @NotBlank(message = "Password for admin login is required!")
    private String password;
}
