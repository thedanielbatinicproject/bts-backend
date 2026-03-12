package online.beneaththestars.btsbackend.services.Admin;

import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.entities.AdminUser;
import online.beneaththestars.btsbackend.models.enums.Role;
import online.beneaththestars.btsbackend.models.services.IAdminUserService;
import online.beneaththestars.btsbackend.repo.AdminUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService implements IAdminUserService {
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUser createAdminUser(String username, String rawPassword, boolean enabled) {
        AdminUser admin = new AdminUser();
        if (username == null || username.isBlank()) throw new RuntimeException("Username is required!");
        if (rawPassword == null || rawPassword.isBlank()) throw new RuntimeException("Password is required!");
        if (adminUserRepository.existsByAdminUsername(username))
            throw new RuntimeException("Admin with such username already exists!");
        admin.setAdminUsername(username);
        admin.setAdminPasswordHash(passwordEncoder.encode(rawPassword));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(enabled);
        return adminUserRepository.save(admin);
    }

    public Page<AdminUser> listAdminUsers(Pageable pageable) {
        return adminUserRepository.findAll(pageable);
    }

    public AdminUser patchAdminUser(long adminUserId, String newPassword, Boolean enabled, Role role) {
        AdminUser adminUser = requireAdminUser(adminUserId);

        //SUPERADMIN protection
        if (adminUser.getRole() == Role.SUPERADMIN) {
            if (enabled != null && !enabled) {
                throw new RuntimeException("Cannot disable SUPERADMIN!");
            }
            if (role != null && role != Role.SUPERADMIN) {
                throw new RuntimeException("Cannot change SUPERADMIN role!");
            }
        }

        if (newPassword != null) {
            if (newPassword.isBlank()) throw new RuntimeException("Password cannot be blank!");
            adminUser.setAdminPasswordHash(passwordEncoder.encode(newPassword));
        }
        if (enabled != null) {
            adminUser.setEnabled(enabled);
        }
        if (role != null) {
            adminUser.setRole(role);
        }

        return adminUserRepository.save(adminUser);
    }

    public void deleteAdminUser(long adminUserId) {
        adminUserRepository.delete(requireAdminUser(adminUserId));
    }

    public AdminUser requireAdminUser(long adminUserId) {
        AdminUser adminUser = adminUserRepository.findByAdminId(adminUserId);
        if (adminUser == null) {
            throw new RuntimeException("Admin user not found!");
        }
        return adminUser;
    }

    public boolean isUsernameTaken(String username) {
        if (username == null || username.isBlank()) return false;
        return adminUserRepository.findAdminUserByAdminUsername(username).isPresent();
    }

    public void requireNotSuperAdmin(AdminUser adminUser) {
        if (adminUser == null) throw new RuntimeException("Admin user is required!");
        if (adminUser.getRole() == Role.SUPERADMIN) {
            throw new RuntimeException("Cannot modify/delete SUPERADMIN!");
        }
    }

    public AdminUser createInitialSuperAdmin(String username, String rawPassword, boolean enabled) {
        if (username == null || username.isBlank()) throw new RuntimeException("Bootstrap username is required!");
        if (rawPassword == null || rawPassword.isBlank()) throw new RuntimeException("Bootstrap password is required!");

        if (adminUserRepository.count() > 0) {
            return null;
        }

        AdminUser admin = new AdminUser();
        admin.setAdminUsername(username);
        admin.setAdminPasswordHash(passwordEncoder.encode(rawPassword));
        admin.setRole(Role.SUPERADMIN);
        admin.setEnabled(enabled);
        return adminUserRepository.save(admin);
    }

    public void requireSuperAdminUser(AdminUser adminUser) {
        if (adminUser == null) throw new RuntimeException("Not logged in!");
        if (adminUser.getRole() != Role.SUPERADMIN) throw new RuntimeException("Forbidden!");
    }

}
