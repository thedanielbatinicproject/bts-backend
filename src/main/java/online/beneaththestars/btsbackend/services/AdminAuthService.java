package online.beneaththestars.btsbackend.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import online.beneaththestars.btsbackend.models.dto.AdminDTOs.AdminLoginRequest;
import online.beneaththestars.btsbackend.models.dto.AdminDTOs.AdminSessionResponse;
import online.beneaththestars.btsbackend.models.entities.AdminUser;
import online.beneaththestars.btsbackend.models.enums.Role;
import online.beneaththestars.btsbackend.models.services.IAdminAuthService;
import online.beneaththestars.btsbackend.repo.AdminUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AdminAuthService implements IAdminAuthService {
    public static final String SESSION_ADMIN_ID = "BTS_ADMIN_SESSION_ID";
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public void login(AdminLoginRequest adminLoginRequest, HttpServletRequest httpRequest){
        AdminUser admin = adminUserRepository.findAdminUserByAdminUsername(adminLoginRequest.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password!"));

        if (!admin.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This admin user is not enabled!");
        }

        if (adminLoginRequest.getPassword() == null)  throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required!");

        if (!passwordEncoder.matches(adminLoginRequest.getPassword(), admin.getAdminPasswordHash()))
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password!");


        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        session = httpRequest.getSession(true);
        session.setAttribute(SESSION_ADMIN_ID, admin.getAdminId());
    }

    public void logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public Long getLoggedInAdminId(HttpServletRequest httpRequest){
        HttpSession session = httpRequest.getSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(SESSION_ADMIN_ID);

        if (value instanceof Long id) return id;

        return null;
    };

    public AdminUser getLoggedInAdmin(HttpServletRequest httpRequest) {
        Long id = getLoggedInAdminId(httpRequest);
        if (id == null) {
            return null;
        }
        return adminUserRepository.findByAdminId(id);
    }

    public AdminUser requireLoggedInAdmin(HttpServletRequest httpRequest) {
        AdminUser admin = getLoggedInAdmin(httpRequest);
        if (admin == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin user is required for this action!");
        }
        if (!admin.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin user must be enabled!");
        }
        return admin;
    }

    public boolean isLoggedIn(HttpServletRequest httpRequest) {
        return getLoggedInAdminId(httpRequest) != null;
    }

    public void requireSuperAdmin(HttpServletRequest httpRequest) {
        AdminUser admin = requireLoggedInAdmin(httpRequest);
        if (admin.getRole() != Role.SUPERADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Super admin user is required!");
        }
    }

    public boolean isSuperAdmin(HttpServletRequest httpRequest) {
        AdminUser admin = getLoggedInAdmin(httpRequest);
        return admin != null && admin.getRole() == Role.SUPERADMIN;
    }

    public AdminSessionResponse getSessionInfo(HttpServletRequest httpRequest) {
        AdminUser admin = requireLoggedInAdmin(httpRequest);
        return new AdminSessionResponse(admin.getAdminUsername(), admin.getRole());
    };
}
