package online.beneaththestars.btsbackend.repo;

import online.beneaththestars.btsbackend.models.entities.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findAdminUserByAdminUsername(String adminUsername);
    List<AdminUser> findAllByEnabledTrue();
    boolean existsByAdminUsername(String adminUsername);
}
