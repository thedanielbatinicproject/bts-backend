package online.beneaththestars.btsbackend.models.dto;

import java.util.Map;

public record ApiErrorResponse(
        int code,
        String message,
        Map<String, Object> details
) {}
