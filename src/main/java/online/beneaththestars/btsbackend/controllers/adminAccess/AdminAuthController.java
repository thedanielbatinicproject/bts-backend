package online.beneaththestars.btsbackend.controllers.adminAccess;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.AdminDTOs.AdminLoginRequest;
import online.beneaththestars.btsbackend.models.dto.AdminDTOs.AdminSessionResponse;
import online.beneaththestars.btsbackend.services.Admin.AdminAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/session")
@RequiredArgsConstructor
@Validated
public class AdminAuthController {
    private final AdminAuthService adminAuthService;

    @PostMapping
    public ResponseEntity<Void> loginAdmin(
            @Valid @RequestBody AdminLoginRequest adminLoginRequest,
            HttpServletRequest httpRequest){
        adminAuthService.login(adminLoginRequest, httpRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<AdminSessionResponse> getSession(HttpServletRequest httpRequest){
        AdminSessionResponse dto = adminAuthService.getSessionInfo(httpRequest);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping
    public ResponseEntity<Void> logoutAdmin(HttpServletRequest httpRequest) {
        adminAuthService.logout(httpRequest);
        return ResponseEntity.noContent().build();
    }
}
