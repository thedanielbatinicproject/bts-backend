package online.beneaththestars.btsbackend.models.services;

import jakarta.servlet.http.HttpServletRequest;
import online.beneaththestars.btsbackend.models.dto.AdminDTOs.AdminLoginRequest;
import online.beneaththestars.btsbackend.models.dto.AdminDTOs.AdminSessionResponse;
import online.beneaththestars.btsbackend.models.entities.AdminUser;

public interface IAdminAuthService {

    String SESSION_ADMIN_ID = "BTS_ADMIN_SESSION_ID";

    // /api/v1/admin/session
    void login(AdminLoginRequest adminLoginRequest, HttpServletRequest httpRequest);
    void logout(HttpServletRequest httpRequest);
    AdminSessionResponse getSessionInfo(HttpServletRequest httpRequest);

    // identity
    Long getLoggedInAdminId(HttpServletRequest httpRequest);
    AdminUser getLoggedInAdmin(HttpServletRequest httpRequest);

    // guards
    AdminUser requireLoggedInAdmin(HttpServletRequest httpRequest);
    void requireSuperAdmin(HttpServletRequest httpRequest);

    // convenience booleans
    boolean isLoggedIn(HttpServletRequest httpRequest);
    boolean isSuperAdmin(HttpServletRequest httpRequest);
}