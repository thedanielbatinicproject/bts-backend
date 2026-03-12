package online.beneaththestars.btsbackend.models.services;

import online.beneaththestars.btsbackend.models.entities.AdminUser;
import online.beneaththestars.btsbackend.models.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IAdminUserService {

    AdminUser createAdminUser(String username, String rawPassword, boolean enabled);
    Page<AdminUser> listAdminUsers(Pageable pageable);
    AdminUser patchAdminUser(long adminUserId, String newPassword, Boolean enabled, Role role);
    void deleteAdminUser(long adminUserId);

    AdminUser requireAdminUser(long adminUserId);
    boolean isUsernameTaken(String username);
    void requireNotSuperAdmin(AdminUser adminUser);
    void requireSuperAdminUser(AdminUser adminUser);
}