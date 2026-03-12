package online.beneaththestars.btsbackend.controllers.adminAccess;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.AdminDTOs.AdminUserRequest;
import online.beneaththestars.btsbackend.models.entities.AdminUser;
import online.beneaththestars.btsbackend.models.enums.Role;
import online.beneaththestars.btsbackend.services.AdminAuthService;
import online.beneaththestars.btsbackend.services.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Validated
public class AdminUserController {
    private final AdminUserService adminUserService;
    private final AdminAuthService adminAuthService;

    @PostMapping("/admin-users")
    public ResponseEntity<AdminUser> createNewAdmin(@Valid @RequestBody AdminUserRequest adminRequest, HttpServletRequest httpRequest) {
        adminAuthService.requireSuperAdmin(httpRequest);
        AdminUser adminUser = adminUserService.createAdminUser(adminRequest.getUsername(),
                                                                adminRequest.getPassword(),
                                                                adminRequest.getEnabled());
        return ResponseEntity.ok(adminUser);
    }

    @GetMapping("/admin-users")
    public ResponseEntity<Page<AdminUser>> getAdminUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest httpRequest
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUser> result = adminUserService.listAdminUsers(pageable);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/admin-users/{adminUserId}")
    public ResponseEntity<AdminUser> editAdminUser(
            @PathVariable long adminUserId,
            @RequestBody AdminUserRequest req,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireSuperAdmin(httpRequest);

        return ResponseEntity.ok(
                adminUserService.patchAdminUser(
                        adminUserId,
                        req.getPassword(),
                        req.getEnabled(),
                        Role.ADMIN
                )
        );
    }

    @DeleteMapping("/admin-users/{adminUserId}")
    public ResponseEntity<Void> deleteAdminUser(
            @PathVariable long adminUserId,
            HttpServletRequest httpRequest
    ) {
        adminAuthService.requireSuperAdmin(httpRequest);
        adminUserService.deleteAdminUser(adminUserId);
        return ResponseEntity.noContent().build();
    }
}
