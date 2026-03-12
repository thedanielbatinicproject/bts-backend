package online.beneaththestars.btsbackend.config;

import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.entities.AdminUser;
import online.beneaththestars.btsbackend.services.Admin.AdminUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AdminBootstrapConfig {

    private final AdminUserService adminUserService;

    @Bean
    public CommandLineRunner createBootstrapSuperAdmin(
            @Value("${bts.admin.bootstrap.username:}") String username,
            @Value("${bts.admin.bootstrap.password:}") String password,
            @Value("${bts.admin.bootstrap.enabled:true}") boolean enabled
    ) {
        return args -> {
            if (username.isBlank() || password.isBlank()) {
                System.out.println("SUPERADMIN bootstrap skipped: username/password not configured.");
                return;
            }

            AdminUser created = adminUserService.createInitialSuperAdmin(username, password, enabled);

            if (created != null) {
                System.out.println("Bootstrap SUPERADMIN created successfully.");
            } else {
                System.out.println("SUPERADMIN bootstrap skipped: admin user already exists.");
            }
        };
    }
}