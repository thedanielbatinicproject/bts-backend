package online.beneaththestars.btsbackend.models.dto.AdminDTOs;

import online.beneaththestars.btsbackend.models.enums.Role;

public record AdminSessionResponse(String username, Role role) {}
